const app = require('express')();
const server = require('http').Server(app);
const io = require('socket.io')(server);
const mysql = require('mysql');
const LOBBYROOM = 'lobby';
//var gameClientSocket;
var playerNameSocketMap = new Map();  // could use object - {}. used as game uses names to refer to socket
var socketIDToPlayerName = new Map();  // used so can get users from rooms as rooms refers to users by socket id
// for game
var roomNames = ['1', '2', '3', '4', '5'];


server.listen(8081, function(){
	console.log('Server running on localhost:8081');
});

io.on('connection', function(socket){
	console.log('SocketIO: ID ' + socket.id + ' Connected!');
	socket.emit('socketID', { id: socket.id });
	//socket.emit('getPlayers', players);
	//socket.broadcast.emit('newPlayer', { id: socket.id });

		socket.on('login', function(data){
			var connection = mysql.createConnection({
			  host     : '127.0.0.1',
			  user     : 'root',
			  password : '',
			  database : 'gamedatabase'
			});

			connection.connect(function(err) {
			  if (err) {
			    console.error('Error connecting: ' + err.stack);
			    return;
			  }
			  console.log('SQL Login: ID ' + connection.threadId + ' connected');
			});

			var username = data.username;
			var password = data.password;
			// using ? 'escapes' values. this means special characters have a '\' appended to them, which will invalidate a given sql injection string'
			connection.query('SELECT * FROM users WHERE username = ? AND password = ?', [username, password], function (error, results, fields) {
				if (error) {
					console.log(error);
					socket.emit('registerAttempt', { successful : true, message : error.name + ": " + error.message });
				} else if(results == null) { socket.emit('loginAttempt', { successful : false, username : null, message : 'Login unsuccessful - please check your username and/or password are correct'} );
				} else if (results.length >= 1) { socket.emit('loginAttempt', { successful : true, username : username, message : 'Login successful!'} );
				}
			});

			connection.end(function(err) {
	  	// The connection is terminated now
			});
		});

		socket.on('register', function(data){
			var connection = mysql.createConnection({
				host     : '127.0.0.1',
				user     : 'root',
				password : '',
				database : 'gamedatabase'
			});

			connection.connect(function(err) {
				if (err) {
					console.error('Error connecting: ' + err.stack);
					return;
				}
				console.log('SQL Registration: ID ' + connection.threadId + ' connected');
			});

			var username = data.username;
			var password = data.password;
			var email = data.email;
			var flag = false;

			connection.query('SELECT * FROM users WHERE email = ?', [email], function(error, results, fields){
				if (error) {
					console.log(error);
					socket.emit('registerAttempt', { successful : false, message : error.name + ": " + error.message });
				} else if(results.length != 0){ // if there is result, means that there existing email in the db
					socket.emit('registerAttempt', {successful : false, message : "Email already in use - please try a different email"});
					flag = true;
				}
			});

			connection.query('SELECT * FROM users WHERE username = ?', username, function(error, results, fields){
				if (error) {
					console.log(error);
					socket.emit('registerAttempt', { successful : false, message : error.name + ": " + error.message });
				} else if(results.length != 0){ // if there is a result, means that there existing username found in the db
					socket.emit('registerAttempt', {successful : false, message : "Username already taken - please try a different username"});
					flag = true
				}
			});

			// will only insert data if username or email isn't taken
			if(!flag){
				connection.query('INSERT INTO users (email, username, password) VALUES (?, ?, ?)', [email, username, password], function (error, results, fields) {
					if (error) {
						console.log(error);
						socket.emit('registerAttempt', { successful : false, message : error.name + ": " + error.message });
					} else {
						if(results.affectedRows >= 1) { socket.emit('registerAttempt', { successful : true, message : 'Registration successful!' }); }
						else if (results.affectedRows < 1) { socket.emit('registerAttempt', { successful : false, message : 'Registration unsuccessful - unknown error'} ); }
					}
				});
			}

			// using ? 'escapes' values. this means special characters have a '\' appended to them, which will invalidate a given sql injection string'

			connection.end(function(err) {
				// The connection is terminated now
			});
		});

	socket.on('gameClientInit', function(){  // the game connects
		console.log('Game Client Connected!');
		gameClientSocket = socket;
	});

	socket.on('playerClientInit', function(data){  // a player connects
		console.log('Player Connected!');
		playerNameSocketMap.set(data.username, socket);
		socketIDtoPlayerName.set(socket.id, data.username);
	});

	socket.on('joinLobby', function(data){  // confirms client in in lobby. message acts as confirmation, w/ 'inLobby' verifying if join or leaving
			socket.join(LOBBYROOM);
			socket.emit('lobbyRoomResponse', {inLobby: true});
	});

	socket.on('leaveLobby', function(data){  // leaves the lobby for room which is sad
			socket.leave(LOBBYROOM);
			socket.emit('lobbyRoomResponse', {inLobby : false});
	});

	socket.on('getRooms', function(){
		var roomClientList = {};

		for(roomID in roomNames){
			var room = io.sockets.adapter.rooms[roomID]; // list of connected socket ids in socket property
			var nameList = [];

			if(room != null){  // if room has players in, hence has object
					Object.keys(room.sockets).forEach(function(socketId) {  // for each id, add username to list
						nameList.add(socketIDtoPlayerName.get(socketID));
					});
			}
			roomClientList[roomID] = nameList;
		}
		socket.emit('roomList', { roomUsers: roomClientList });  // need to add players connected in room
	});

	socket.on('joinRoomRequest', function(data){  // client requests to join room
		gameClientSocket.emit('requestJoinRoom', {username : playerNameSocketMap.get(data.username), room : data.room})
	});

	socket.on('joinRoomResponse', function(data){  // game responds if room is full or not
		clientSocket = playerNameSocketMap.get(data.username);
		if(data.response){  // if can join room as room not full
			clientSocket.join(data.room);  // join room
			clientSocket.to(LOBBYROOM).emit('newPlayerToRoom', {username : data.username, room : data.room});  // sends to all clients in lobby that player connected to room
		}
		clientSocket.emit('joinedRoomResponse', data);  // tell client response of room join
	});

	socket.on('gameSetup', function(data){ // game client sends data over to setup entites in client
		socket.to(data.room).emit('gameSetup', {playerData : data.playerData, fogData : data.fogData, mapData : data.mapData});
	});

	socket.on('playerReady', function(data){  // client responds when data loaded
		gameClientSocket.emit('playerReady', data);
	})

	socket.on('gameCountdown', function() {  // game client issue 5 second waring
		socket.to(data.room).emit('gameCountdown');
	});

	socket.on('gameBegin', function(data){  // game client starts game, notif players
		socket.to(data.room).emit('gameBegin');
	})

	socket.on('gameUpdate', function(data){
		socket.to(data.room).emit('gameUpdate', {playerData : data.playerData, fogData : data.fogData, mapData : data.mapData});
	})

	socket.on('gameKeyPress', function(data){
		gameClientSocket.emit('gameKeyPress', data)
	})

});

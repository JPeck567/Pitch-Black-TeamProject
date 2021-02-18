var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var mysql = require('mysql');
var playersSocket = [];
var gameClientSocket;

server.listen(8081, function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
	console.log("Client " + socket.id + " Connected!");
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
		  console.log('Connected as id ' + connection.threadId);
		});

		var username = data.username;
		var password = data.password;
		// using ? 'escapes' values. this means special characters have a '\' appended to them, which will invalidate a given sql injection string'
		connection.query('SELECT * FROM users WHERE username = ? AND password = ?', [username, password], function (error, results, fields) {
			console.log("login results" + results);
			if (error) throw error;
			if(results == null) { socket.emit('loginAttempt', { successful : false} ); }
			else if (results.length >= 1) { socket.emit('loginAttempt', { successful : true} ); }
			else { socket.emit('loginAttempt', { successful : null }); }// if somehow there are neither 0 rows or >1 rows
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
			console.log('Connected as id ' + connection.threadId);
		});

		var username = data.username;
		var password = data.password;
		var email = data.email;
		// using ? 'escapes' values. this means special characters have a '\' appended to them, which will invalidate a given sql injection string'
		connection.query('INSERT INTO users (email, username, password) VALUES (?, ?, ?)', [email, username, password], function (error, results, fields) {
				console.log("reg results" + results);
				if (error) {
					throw error;
				} else {
					if(results.affectedRows >= 1) { socket.emit('registerAttempt', { successful : true }); }
					else if (results.affectedRows < 1) { socket.emit('registerAttempt', { successful : false} ); }
					else { socket.emit('registerAttempt', { successful : null }); } // if somehow is an error
			    //console.log(error, results, fields);
				}
		});

		connection.end(function(err) {
			// The connection is terminated now
		});
	});

	socket.on('gameClientInit', function(){  // the game connects
		console.log("Game Connected!");
		gameClientSocket = socket;
		socket.emit('gameClientAcknowledge');
	});

	socket.on('playerClientInit', function(){  // a player connects
		console.log("Player Connected!");
		playersSocket.push(socket);  // add to list of players
		//playerIDMap.set(socket.id, socket);

		gameClientSocket.emit("newPlayer", { id: socket.id });
	});

	socket.on('newPlayerAcknowledge', function(data){
		var id = data.id;
		var p;
		for(p of playersSocket){
			if(p.id == id){
				p.emit('newPlayerAcknowledge');
			}
			break;
		}
	})

	socket.on('playerReady', function(){
		gameClientSocket.emit("playerReady");
	})

	socket.on('gameReady', function(data){
		console.log("Game Ready");
//		var p;

		socket.broadcast.emit("gameReady", data);
//		for(p of playersSocket){
//			console.log(p);
//			p.emit("gameStart", data);
//		}
	})

	socket.on('gameStart', function(data){
		socket.broadcast.emit("gameStart", data);
	})

	socket.on('gameUpdate', function(data){
		socket.broadcast.emit("gameUpdate", data);
//		var p;
//		for(p of playersSocket){
//			p.emit("gameUpdate", data);
//		}
	})

	socket.on('keyPress', function(data){
		gameClientSocket.emit('keyPress', data)
	})

	socket.on('win', function(data){
		var id = data.id;
		var p;
		for(p of playersSocket){
			if(p.id == id){
				p.emit('win');
			}
			break;
		}
	})
});

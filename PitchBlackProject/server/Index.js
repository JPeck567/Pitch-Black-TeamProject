const app = require('express')();
const server = require('http').Server(app);
const io = require('socket.io')(server);
const mysql = require('mysql');
const bcrypt = require('bcryptjs');
const SQL_USERNAME = 'localGame';
const SQL_PASSWORD = 'xRAFz71jyeW8JeNQ';
const LOBBYROOM = 'lobby';
const PORT = 8081;
const SALTROUNDS = 10;

var playerNameSocketMap = new Map(); // could use object - {}. used as game uses names to refer to socket
var socketIDToPlayerName = new Map(); // used so can get users from rooms as rooms refers to users by socket id

var roomNames = ['1', '2', '3', '4', '5'];
var gameClientSocket;

server.listen(PORT, function() {
  console.log('Server running on localhost:' + PORT);
});

io.on('connection', function(socket) {
  console.log('SocketIO: ID ' + socket.id + ' Connected!');
  socket.emit('socketID', { id: socket.id });

  socket.on('disconnecting', () => {  // before teardown which means socket leaves room. we need to know this so can't use 'disconnect'
    if(socketIDToPlayerName.has(socket.id)) {  // is connected as render client
        removeClient(socket);
        console.log("Client " + socket.id + " Disconnected");
        return;
    } else if (gameClientSocket != null) {
      if (socket.id === gameClientSocket.id) {  // check if actually game client
        gameClientSocket = null;
        console.log("Game Client Socket " + socket.id + " Disconnected");
        return;
      }
    }  // assume connection is for login/registration if not game or render client
    console.log("SQL Socket " + socket.id + " Disconnected");
  });

/*
	socket.on('disconnect', function(){
    if(socketIDToPlayerName.has(socket.id)) {  // is connected as render client
        removeClient(socket);
        console.log("Client " + socket.id + " Disconnected");
    } else if (gameClientSocket != null) {
      if (socket.id === gameClientSocket.id) {  // check if actually game client
        gameClientSocket = null;
        console.log("Game Client Socket " + socket.id + " Disconnected");
      } else {
        console.log("SQL Socket " + socket.id + " Disconnected");
      }
    } else {  // assume connection is for login/registration
      console.log("SQL Socket " + socket.id + " Disconnected");
    }
  });
s*/

  socket.on('login', function(data) {
    var connection = mysql.createConnection({
      host: '127.0.0.1',
      user: SQL_USERNAME,
      password: SQL_PASSWORD,
      database: 'gamedatabase'
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

    // checks if already logged in
	  if(playerNameSocketMap.has(username)) {
      socket.emit('loginAttempt', {successful: false, username: null, message: 'User is already logged in!'});
      return;
    }

    // SQL to check pass + username: 'SELECT * FROM users WHERE username = ? AND password = ?'
    // using ? 'escapes' values. this means special characters have a '\' appended to them, which will invalidate a given sql injection string'
    connection.query('SELECT password FROM users WHERE username = ?', [username], function(error, results, fields) {
      if (error) {
        console.log(error);
        socket.emit('loginAttempt', {
          successful: true,
          message: error.name + ": " + error.message
        });
      } else if (results.length == 0) {
        socket.emit('loginAttempt', {
          successful: false,
          username: null,
          message: 'Login unsuccessful - please check your username is correct'
        });
      } else if (results[0].password) {
        bcrypt.compare(password, results[0].password, function(err, result) {
          if(result){
            socket.emit('loginAttempt', {
              successful: true,
              username: username,
              message: 'Login successful!'
            });

            console.log('Player ' + data.username + ' Connected!');
            playerNameSocketMap.set(data.username, socket);
            socketIDToPlayerName.set(socket.id, data.username);
          } else {
            socket.emit('loginAttempt', {
              successful: false,
              username: username,
              message: 'Login unsuccessful - please check your password.'
            });
          }
        });
      }
    });

    connection.end(function(err) {
      // The connection is terminated now
    });
  });

  socket.on('register', function(data) {
    var connection = mysql.createConnection({
      host: '127.0.0.1',
      user: SQL_USERNAME,
      password: SQL_PASSWORD,
      database: 'gamedatabase'
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

		// using ? 'escapes' values. this means special characters have a '\' appended to them, which will invalidate a given sql injection string'
    connection.query('SELECT * FROM users WHERE email = ?', email, function(error, results, fields) {
      if (error) {
        console.log(error);
        socket.emit('registerAttempt', {
          successful: false,
          message: error.name + ": " + error.message
        });
        flag = true
      } else if (results.length != 0) { // if there is result, means that there existing email in the db
        socket.emit('registerAttempt', {
          successful: false,
          message: "Email already in use - please try a different email"
        });
        flag = true;
      }
    });

    connection.query('SELECT * FROM users WHERE username = ?', username, function(error, results, fields) {
      if (error) {
        console.log(error);
        socket.emit('registerAttempt', {
          successful: false,
          message: error.name + ": " + error.message
        });
        flag = true
      } else if (results.length != 0) { // if there is a result, means that there existing username found in the db
        socket.emit('registerAttempt', {
          successful: false,
          message: "Username already taken - please try a different username"
        });
        flag = true
      }
    });

    // will only insert data if username or email isn't taken
    if (!flag) {
      // auto gen a salt and make a hash an async
      bcrypt.hash(password, SALTROUNDS, function(err, hash) {
        connection.query('INSERT INTO users (email, username, password) VALUES (?, ?, ?)', [email, username, hash], function(error, results, fields) {
          if (error) {
            console.log(error);
            socket.emit('registerAttempt', {
              successful: false,
              message: error.name + ": " + error.message
            });
          } else {
            if (results.affectedRows >= 1) {
              socket.emit('registerAttempt', {
                successful: true,
                message: 'Registration successful!'
              });
            } else if (results.affectedRows < 1) {
              socket.emit('registerAttempt', {
                successful: false,
                message: 'Registration unsuccessful - unknown error'
              });
            }
          }
          connection.end(function(err) {
            // The connection is terminated now
          });
        });
      });
    } else {
      connection.end(function(err) {
        // The connection is terminated now
      });
    }
  });

  socket.on('gameClientInit', function() { // the game connects
    console.log('Game Client Connected!');
    gameClientSocket = socket;
  });

  socket.on('joinLobby', function(data) { // confirms client in in lobby. message acts as confirmation, w/ 'inLobby' verifying if join or leaving
    socket.join(LOBBYROOM);
    socket.emit('lobbyRoomResponse', {
      inLobby: true
    });
  });

  socket.on('leaveLobby', function(data) { // leaves the lobby for room which is sad
    socket.leave(LOBBYROOM);
    socket.emit('lobbyRoomResponse', {
      inLobby: false
    });
  });

  socket.on('getRooms', function() {
    var roomClientList = {};

    for (roomID of roomNames) {
      roomClientList[roomID] = getNamesInRoom(roomID);
    }

    socket.emit('roomList', {
      roomUsers: roomClientList
    }); // need to add players connected in room
  });

  socket.on('joinRoomRequest', function(data) { // client requests to join room
    gameClientSocket.emit('joinRoomRequest', {
      username: data.username,
      room: data.room
    });
  });

  socket.on('joinRoomResponse', function(data) { // game responds if room is full or not
    clientSocket = playerNameSocketMap.get(data.username);
    if (data.response) { // if can join room as room not full
      clientSocket.join(data.room); // join room
      clientSocket.to(LOBBYROOM).emit('newPlayerToRoom', {
        username: data.username,
        room: data.room
      }); // sends to all clients in lobby that player connected to room
    }
    clientSocket.emit('joinRoomResponse', data); // tell client response of room join
  });

  socket.on('gameSetup', function(data) { // game client sends data over to setup entites in client
    socket.to(data.room).emit('gameSetup', {
      playerData: data.playerData,
      fogData: data.fogData,
      mapData: data.mapData
    });
  });

  socket.on('playerReady', function(data) { // client responds when data loaded
    gameClientSocket.emit('playerReady', data);
  })

  socket.on('gameCountdown', function(data) { // game client issue 5 second waring
    socket.to(data.room).emit('gameCountdown', { countdownSeconds : data.countdownSeconds});
  });

  socket.on('gameBegin', function(data) { // game client starts game, notif players + lobby clients
    io.to(LOBBYROOM).emit('gameInSession', { inSession : true, room : data.room});
    socket.to(data.room).emit('gameBegin');
  });

  socket.on('gameUpdate', function(data) {  // game instance updates players
    socket.to(data.room).emit('gameUpdate', {
      playerData: data.playerData,
      fogData: data.fogData,
      mapData: data.mapData
    });
  });

  socket.on('gameKeyPress', function(data) {  // game client sends key input
    gameClientSocket.emit('gameKeyPress', data);
  });

  // triggers client LOSE state
  socket.on('gamePlayerDied', function(data) {  // if player(s) dies, remove from room
    var diedArray = data.diedArray[0];
    for(playerName of diedArray){
      var playerSocket = playerNameSocketMap.get(playerName);
       // player may die as disconnected and server responds by sending kill request.
       // if disconn, data not in map. checks if this is the case.
      if(playerSocket != null) {
        playerSocket.leave(data.room);
        playerSocket.emit('gamePlayerDied');
      }
    }
  });

  // triggers client WIN state for winner. if called, assumed other clients already died, hence already in lose state
	socket.on('gameFinish', function(data) {  // game instance updates players still in room with winner & clients return to menu.
		playerNameSocketMap.get(data.winnerName).emit('gameFinish', { winnerName : data.winnerName });
	});

  socket.on('resetRoom', function(data){ // when game finished, empty room in server, and tell clients
    var sockIds = io.sockets.adapter.rooms[data.room];

    if(sockIds != null) { // remove sockets in room, if they exist
      Object.keys(sockIds.sockets).forEach(function(socketId) { // for each id, add remove from room
        io.sockets.sockets[socketId].leave(data.room)
      });
    }
    io.to(LOBBYROOM).emit('gameInSession', { inSession : false, room : data.room}); // tell clients to reset their lobby room ui user list
  });
});  // closing brace + bracket from ' io.on('connection', function(socket) { /* the code */  '

// if render client disconnects
function removeClient(socket){
  let rooms = Object.keys(socket.rooms);  // gets rooms client connected to

  // checks if client was in room, if so should remove from game client
  // socket will auto move out of socket.io room in disconnection, so no extra action is needed room wise server side.
  for(room of rooms) {  // at most 1, the current game room
    if(roomNames.includes(room)) {  // if room exists in room list
      gameClientSocket.emit('playerDisconnected', {username : socketIDToPlayerName.get(socket.id), room : room} );
      socket.to(LOBBYROOM).emit('removePlayerFromRoom', {
        username: socketIDToPlayerName.get(socket.id),
        room: room
      });
      break;  // get out of for loop
    }
  }
  console.log("Player: " + socketIDToPlayerName.get(socket.id) + " ID: " + socket.id + " was removed from server")
  playerNameSocketMap.delete(socketIDToPlayerName.get(socket.id));
  socketIDToPlayerName.delete(socket.id);
}

function getNamesInRoom(roomID){
  var nameList = [];
  var sockIds = io.sockets.adapter.rooms[roomID]; // list of connected socket ids in socket property

  if (sockIds != null) { // if room has players in, hence has object
    Object.keys(sockIds.sockets).forEach(function(socketId) { // for each id, add username to list
      nameList.push(socketIDToPlayerName.get(socketId));
    });
  }
  return nameList;
}

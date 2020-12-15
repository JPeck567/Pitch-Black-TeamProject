var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var playersSocket = [];
//var playerIDMap = new Map();
var gameClientSocket;

server.listen(8080, function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
	//console.log("Player Connected!");
	socket.emit('socketID', { id: socket.id });
	//socket.emit('getPlayers', players);
	//socket.broadcast.emit('newPlayer', { id: socket.id });
	
	socket.on('gameClientInit', function(){
		console.log("Game Connected!");
		gameClientSocket = socket;
		socket.emit('gameClientAcknowledge');
	});

	socket.on('playerClientInit', function(){
		console.log("Player Connected!");
		playersSocket.push(socket);
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

//	socket.on('playerMoved', function(data){
//	    data.id = socket.id;
//	    socket.broadcast.emit('playerMoved', data);
//
//	    for(var i = 0; i < players.length; i++){
//	        if(players[i].id == data.id){
//	            players[i].x = data.x;
//	            players[i].y = data.y;
//	        }
//	    }
//	});

//	socket.on('disconnect', function(){
//		console.log("Player Disconnected");
//		socket.broadcast.emit('playerDisconnected', { id: socket.id });
//		for(var i = 0; i < players.length; i++){
//			if(players[i].id == socket.id){
//				players.splice(i, 1);
//			}
//		}
//	});

	//players.push(new player(socket.id, 0, 0));

//function player(id, x, y){
//	this.id = id;
//	this.x = x;
//	this.y = y;
//}
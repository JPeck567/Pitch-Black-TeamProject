package net.pitchblack.getenjoyment.frontend.client;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.pitchblack.getenjoyment.frontend.game.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.game.PitchBlackGraphics.Screens;
import net.pitchblack.getenjoyment.frontend.game.screens.GameScreen.GameState;
import net.pitchblack.getenjoyment.frontend.game.screens.LoginInitiator;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
	private static final float UPDATE_TIME = 1/60f;

	public enum ClientState {
		ACCOUNT,  // logging in / registration
		LOBBY, // in lobby
		INITIATED,  // being game session
		IDLE, // does nothing
		LOADING,  // load entites from given player id's
		READY,  // finished loading
		GAME;
	}

	public enum AccountState {
		LOGGED_IN,
		LOGIN_ATTEMPTED,
		REGISTRATION_ATTEMPTED,
		LOGGED_OUT;
	}

	private PitchBlackGraphics parent;

	private LoginInitiator loginInitiator;
	private String id;
	private Socket socket;
	private String username;
	private String currentRoom;
	private Boolean isConnected;
	private ClientState clientState;

	private AccountState accountState;
	public Client(PitchBlackGraphics parent) {
		this.parent = parent;
		id = null;
		socket = null;
		username = null;
		currentRoom = null;
		isConnected = false;

		clientState = ClientState.IDLE;
		accountState = AccountState.LOGGED_OUT;
	}

	public void beginConnection() {
		connectSocket();
		configSocketEvents();
	}

	public void endConnection() {
		socket.close();
		isConnected = false;
	}

	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8081"); //
			socket.connect();
			isConnected = true;
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void updateServer(float delta){
		switch(clientState) {
//			case INITIATED:
//				socket.emit("playerClientInit");
//				clientState = ClientState.IDLE;
//				break;
			case LOADING:
				//gameScreen.loadEntities(playerIds);
				// TODO: check if player clicks ready button after data loaded OR better to have lobby call a client method to do work below
				if(parent.isLobbyScreenReady()) {
					JSONObject data = new JSONObject();
					try {
						data.put("username", username)
							.put("room", currentRoom);
					} catch(JSONException e) { e.printStackTrace(); }
					socket.emit("playerReady", data);
				}
				//clientState = ClientState.IDLE;
				break;
			case GAME:
//				if(keyUpCode != -1 || keyDownCode != -1) {  // if either button pressed
//					JSONObject data = new JSONObject();
//					try {
//						data.put("username", username)
//							.put("room", currentRoom)
//							.put("keyUp", keyUpCode)
//							.put("keyDown", keyDownCode);
//						keyUpCode = -1;
//						keyDownCode = -1;
//						socket.emit("gameKeyPress", data);
//					} catch(JSONException e){ e.printStackTrace(); }
//				}
		default:
			break;
		}
	}

	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("Connected to localhost:8081");
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
				} catch (JSONException e) { e.printStackTrace(); }
				System.out.println("SocketIO: Connected with ID:" + id);
				//gameRenderer.setID(id);
				//renderState = RenderState.IDLE;
			}
		}).on("loginAttempt", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				boolean loggedInAttempt = false;
				String username = null;
				String message = null;
				try {
					loggedInAttempt = data.getBoolean("successful");
					username = data.getString("username");
					message = data.getString("message");
				} catch (JSONException e) { e.printStackTrace(); }

				loginInitiator.loginResponse(loggedInAttempt, message);
				if(loggedInAttempt) {
					accountState = AccountState.LOGGED_IN;
					Client.this.username = username;
					emitPlayerClientInit();
				} else {
					accountState = AccountState.LOGGED_OUT;
				}
			}
		}).on("registerAttempt", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				boolean registrationAttempt = false;
				String message = null;
				try {
					registrationAttempt = data.getBoolean("successful");
					message = data.getString("message");
				} catch (JSONException e) { e.printStackTrace(); }

				loginInitiator.registrationResponse(registrationAttempt, message);
				accountState = AccountState.LOGGED_OUT;
			}
		}).on("lobbyRoomResponse", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				boolean inLobby = false;
				try {
					inLobby = data.getBoolean("inLobby");
				} catch(JSONException e) { e.printStackTrace(); }
				if(inLobby) {
					clientState = ClientState.LOBBY;
				} else {
					clientState = ClientState.IDLE;
				}
			}
		}).on("roomList", new Emitter.Listener() {
			public void call(Object... args) {
				String roomUsers = null;
				HashMap roomUsersMap = null;
				JSONObject data = (JSONObject) args[0];
				try {
					roomUsers = data.getString("roomUsers");
					roomUsersMap = new ObjectMapper().readValue(roomUsers, HashMap.class);  // uses jackson dependency to easily turn json to java map
				} catch(JSONException | JsonProcessingException e) { e.printStackTrace(); }
				// convert JSON string to Java Map
				parent.addLobbyRoomData(roomUsersMap);
				//System.out.println(roomUsersMap.toString());
			}
		}).on("newPlayerToRoom", new Emitter.Listener() {
			public void call(Object... args) {
				String username = null;
				String room = null;
				JSONObject data = (JSONObject) args[0];
				try {
					room = data.getString("room");
					username = data.getString("username");
				} catch(JSONException e) { e.printStackTrace(); }
				parent.addLobbyNewPlayer(username, room);
			}
		}).on("removePlayerFromRoom", new Emitter.Listener() {
			public void call(Object... args) {
				String username = null;
				String room = null;
				JSONObject data = (JSONObject) args[0];
				try {
					room = data.getString("room");
					username = data.getString("username");
				} catch(JSONException e) { e.printStackTrace(); }
				parent.removeLobbyPlayer(username, room);
            }
        }).on("gameInSession", new Emitter.Listener() {
            public void call(Object... args) {
                String room = null;
                boolean inSession = false;
                JSONObject data = (JSONObject) args[0];
                try {
                    room = data.getString("room");
                    inSession = data.getBoolean("inSession");
                } catch(JSONException e) { e.printStackTrace(); }
                if(inSession){
                    parent.lobbyRoomInSession(room);
                } else {
                    parent.lobbyResetRoom(room);
                }
            }
        }).on("resetRoom", new Emitter.Listener() {
            public void call(Object... args) {
                String room = null;
                JSONObject data = (JSONObject) args[0];
                try {
                    room = data.getString("room");
                } catch(JSONException e) { e.printStackTrace(); }
                parent.lobbyResetRoom(room);
			}
		}).on("joinRoomResponse", new Emitter.Listener() {
			public void call(Object... args) {
				boolean response = false;
				String room = null;
				String message = null;
				JSONObject data = (JSONObject) args[0];
				try {
					response = data.getBoolean("response");
					room = data.getString("room");
					message = data.getString("message");
				} catch(JSONException e) { e.printStackTrace(); }
				if(response) {
					currentRoom = room;
					parent.lobbyJoinRoomResponse(true, room, message);
				} else {
					parent.lobbyJoinRoomResponse(false, room, message);
				}
			}
		}).on("gameSetup", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String playerData = null;
				String fogData = null;
				String mapData = null;
				try {
					playerData = data.getString("playerData");
					fogData = data.getString("fogData");
					mapData = data.getString("mapData");
				} catch(JSONException e) { e.printStackTrace(); }
				clientState = ClientState.LOADING;
				parent.setupGameScreen(playerData, fogData, mapData);
			}
		}).on("gameCountdown", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				//TODO: alert lobby to countdown
			}
		}).on("gameBegin", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				parent.gameScreenSetState(GameState.PLAYING);
				parent.postRunnableChangeScreen(Screens.GAME);
				clientState = ClientState.GAME;
			}
		}).on("gameUpdate", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String playerData = null;
				String fogData = null;
				String mapData = null;
				try {
					playerData = data.getString("playerData");
					fogData = data.getString("fogData");
					mapData = data.getString("mapData");
				} catch (JSONException e) { e.printStackTrace(); }
				parent.gameAddToGameDataBuffer(playerData, fogData, mapData);
			}
		}).on("gamePlayerDied", new Emitter.Listener() {
			@Override
			public void call(Object... args){
				parent.gameScreenSetState(GameState.LOSE);
			}
		}).on("gameFinish", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String winnerName = null;
				try {
					winnerName = data.getString("winnerName");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// should be redundant as losing players are messaged anyway and this event is only for winning players
				if (winnerName.equals("")) {  // for one player, assumes died anyway and lose state change should also occur anyway
					//parent.gameScreenSetState(GameState.LOSE);
				} else if(winnerName.equals(username)) {  // if won in multiplayer
					parent.gameScreenSetState(GameState.WIN);
				}

			}
		});
	}

	public void tick(float delta) {
		updateServer(delta);
	}

	public void emitSendLogin(String username, String password, LoginInitiator loginInit) {
		loginInitiator = loginInit;
		JSONObject data = new JSONObject();
		try{
			data.put("username", username);
			data.put("password", password);
		} catch(JSONException e) { e.printStackTrace(); }
		socket.emit("login", data);
		accountState = AccountState.LOGIN_ATTEMPTED;
	}

	public void emitSendRegistration(String email, String username, String password, LoginInitiator loginInit) {
		loginInitiator = loginInit;
		JSONObject data = new JSONObject();
		try{
			data.put("email", email);
			data.put("username", username);
			data.put("password", password);
		} catch(JSONException e) { e.printStackTrace(); }
		socket.emit("register", data);
		accountState = AccountState.REGISTRATION_ATTEMPTED;
	}

	public void emitPlayerClientInit() {
		JSONObject data = new JSONObject();
		try{
			data.put("username", username);
		} catch(JSONException e) { e.printStackTrace(); }
		clientState = ClientState.IDLE;
	}

	public void emitJoinLobby() {
		JSONObject data = new JSONObject();
		try{
			data.put("username", username);
		} catch(JSONException e) { e.printStackTrace(); }
		socket.emit("joinLobby", data);
	}

	public void emitJoinRoomRequest(String room) {
		JSONObject data = new JSONObject();
		try{
			data.put("username", username);
			data.put("room", room);
		} catch(JSONException e) { e.printStackTrace(); }
		socket.emit("joinRoomRequest", data);
	}

	public void emitGetRooms() {
		socket.emit("getRooms");
	}

	public void emitPlayerReady(){
		JSONObject data = new JSONObject();
		try {
			data.put("username", username)
					.put("room", currentRoom);
		} catch(JSONException e) { e.printStackTrace(); }

		socket.emit("playerReady", data);
	}

	public void emitKeyDown(int keycode) {
		JSONObject data = new JSONObject();
		try {
			data.put("username", username)
					.put("room", currentRoom)
					.put("keyUp", -1)
					.put("keyDown", keycode);
			socket.emit("gameKeyPress", data);
		} catch(JSONException e){ e.printStackTrace(); }
	}

	public void emitKeyUp(int keycode) {
		JSONObject data = new JSONObject();
		try {
			data.put("username", username)
					.put("room", currentRoom)
					.put("keyUp", keycode)
					.put("keyDown", -1);
			socket.emit("gameKeyPress", data);
		} catch(JSONException e){ e.printStackTrace(); }
	}

	public String getUsername() {
		return username;
	}

	public void setClientState(ClientState clientState) {
		this.clientState = clientState;
	}

	public boolean isPlaying() { return clientState == ClientState.GAME; }

	public void setAccountState(AccountState accountState) {
		this.accountState = accountState;
	}

	public AccountState getAccountState() {
		return accountState;
	}

	public boolean isInLobby() {
		return clientState == ClientState.LOBBY;
	}

	public void resetCurrentRoom() {
		currentRoom = null;
	}

}

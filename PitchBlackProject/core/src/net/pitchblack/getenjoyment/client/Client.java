package net.pitchblack.getenjoyment.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator;
import net.pitchblack.getenjoyment.logic.GameRenderer;
import net.pitchblack.getenjoyment.logic.GameWorld;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
	private static final float UPDATE_TIME = 1/60f;
	private float timer;
	
	public enum RenderState {
		ACCOUNT,  // logging in / registration
		INITIATED,  // being game session
		IDLE, // waiting for any events. does nothing
		LOADING,  // load entites from given player id's
		READY,  // finished loading
		PLAYING,  // ingame
		FINISH, 
		WIN, 
		LOSE
	}
	
	public enum AccountState {
		LOGGED_IN,
		LOGIN_ATTEMPTED,
		REGISTRATION_ATTEMPTED,
		LOGGED_OUT
	}

	private GameRenderer gameRenderer;
	private Player player;
	private String id;
	private Socket socket;
	private RenderState renderState;
	public AccountState accountState;
	private LoginInitiator loginInitiator;
	private String playerIds;
	private boolean playing;
	private int keyUpCode;
	private int keyDownCode;
	
	public Client() {
		//this.player = gameRenderer.getPlayer();
		renderState = RenderState.IDLE;
		accountState = AccountState.LOGGED_OUT;
		playerIds = "";
		playing = false;
		keyUpCode = -1;
		keyDownCode = -1;
	}
	
	public void beginConnection() {
		connectSocket();
		configSocketEvents();
	}
	
	public void endConnection() {
		socket.close();
	}

	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8081");
			socket.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void updateServer(float delta){
//		timer += dt;
//		if(timer >= UPDATE_TIME)
		switch(renderState) {
			case PLAYING:
				gameRenderer.render(delta);
				if(keyUpCode != -1 || keyDownCode != -1) {
					JSONObject data = new JSONObject();
					try{
						data.put("id", id);
						data.put("keyUp", keyUpCode);
						data.put("keyDown", keyDownCode);
						keyUpCode = -1;
						keyDownCode = -1;
						socket.emit("keyPress", data);
					} catch(JSONException e){
						Gdx.app.log("SOCKET.IO", "Data Update Error");
					}
				}
				break;
			case INITIATED:
				socket.emit("playerClientInit");
				renderState = RenderState.IDLE;
				break;
			case LOADING:
				gameRenderer.loadEntities(playerIds);
				renderState = RenderState.READY;
				break;
			case READY:
				socket.emit("playerReady");
				renderState = RenderState.IDLE;
			case WIN:
			case LOSE:
		default:
			break;	
		}
	}
	
	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
//				Gdx.app.log("SocketIO", "Connected");
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
				} catch (JSONException e) { e.printStackTrace(); }
				//Gdx.app.log("SocketIO", "My ID: "+ id);
				System.out.println("SocketIO: Connected with ID:" + id);
				//gameRenderer.setID(id);
				//renderState = RenderState.IDLE;
			}
		}).on("loginAttempt", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				boolean loggedInAttempt = false;
				try {
					loggedInAttempt = Boolean.parseBoolean(data.getString("successful"));
				} catch (JSONException e) { e.printStackTrace(); }
				
				loginInitiator.loginResponse(loggedInAttempt);
				if(loggedInAttempt) {
					accountState = AccountState.LOGGED_IN;
					// send logged in message
					//System.out.println("Login");
				} else if(!loggedInAttempt) {
					accountState = AccountState.LOGGED_OUT;
					// send login fail message
					//System.out.println("Login Failed");
				} else {
					accountState = AccountState.LOGGED_OUT; 
					// send error message
					//System.out.println("Login Error");
				}
			}
		}).on("registerAttempt", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				boolean registrationAttempt = false;
				try {
					registrationAttempt = Boolean.parseBoolean(data.getString("successful"));
				} catch (JSONException e) { e.printStackTrace(); }
				
				loginInitiator.registrationResponse(registrationAttempt);
				
				if(registrationAttempt) {
					// send message that account made
					//System.out.println("Register Successful");
				} else {
					// error somehow + send message
					//System.out.println("Register Unsuccessful");
				}
				
				accountState = AccountState.LOGGED_OUT;
			}

			
		}).on("newPlayerAcknowledge", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				renderState = RenderState.IDLE;
			}
		}).on("gameReady", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String pIds = "";
				try {
					pIds = data.getString("playerId");
				} catch(JSONException e) { }
				playerIds = pIds;
				renderState = RenderState.LOADING;
			}
		}).on("gameStart", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerData = data.getString("playerData");
					String fogData = data.getString("fogData");
					String mapData = data.getString("mapData");
					gameRenderer.addPlayerData(playerData);
					gameRenderer.addFogData(fogData);
					gameRenderer.addMapData(mapData);
				} catch(JSONException e) { Gdx.app.log("SocketIO", "Player ID Error"); }
				renderState = RenderState.PLAYING;
			}
		}).on("gameUpdate", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerData = data.getString("playerData");
					String fogData = data.getString("fogData");
					String mapData = data.getString("mapData");
					gameRenderer.addPlayerData(playerData);
					gameRenderer.addFogData(fogData);
					gameRenderer.addMapData(mapData);
				} catch(JSONException e){ Gdx.app.log("SocketIO", "Player ID Error");	}
			}
		}).on("win", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				renderState = RenderState.WIN;
			}
		});
}

	public void tick(float delta) {
		updateServer(delta);
	}
	
	public void sendLogin(String username, String password, LoginInitiator loginInit) {
		loginInitiator = loginInit;
		JSONObject data = new JSONObject();
		try{
			data.put("username", username);
			data.put("password", password);
		} catch(JSONException e) { e.printStackTrace(); }
		socket.emit("login", data);
		accountState = AccountState.LOGIN_ATTEMPTED;
	}
	
	public void sendRegistration(String email, String username, String password, LoginInitiator loginInit) {
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

	public void setRenderer(GameRenderer gameRenderer) {
		this.gameRenderer = gameRenderer;
	}

	public void keyDown(int keycode) {
		keyDownCode = keycode;
	}

	public void keyUp(int keycode) {
		keyUpCode = keycode;
	}

	public String getID() {
		return id;
	}

	public void setState(RenderState state) {
		renderState = state;
	}
	
	public static void main(String args[]) {
		Client c = new Client();
	}
}

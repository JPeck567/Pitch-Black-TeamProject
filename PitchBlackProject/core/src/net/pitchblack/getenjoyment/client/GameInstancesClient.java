package net.pitchblack.getenjoyment.client;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import net.pitchblack.getenjoyment.client.GameInstance.GameState;
import net.pitchblack.getenjoyment.graphics.PitchBlackGame;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class GameInstancesClient {
	private static final float UPDATE_TIME = 1/30f;  // 30 times a second
	private float timer;
	private Socket socket;
	private String id;
	
	private final PBAssetManager pbAssetManager;
	private HashMap<String, GameInstance> instanceMap;  // room name mapped to instance

	public GameInstancesClient() {
		pbAssetManager = new PBAssetManager();
		pbAssetManager.loadTextures();
		pbAssetManager.loadMaps();
		
		instanceMap = new HashMap<String, GameInstance>();
		instanceMap.put("1", getGameInstance("1"));
		instanceMap.put("2", getGameInstance("2"));
		instanceMap.put("3", getGameInstance("3"));
		instanceMap.put("4", getGameInstance("4"));
		instanceMap.put("5", getGameInstance("5"));
		
		connectSocket();
		configSocketEvents();
	}
	
	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");  // game is on same machine as server, so localhost
			socket.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void run() {
		long timeAtLastLoop = System.currentTimeMillis();
		while(true) {
			float delta = timeAtLastLoop - System.currentTimeMillis();  // time passed since last loop call
			timer += delta;
			
			if(timer >= UPDATE_TIME) {  // if enough time passed, run game instances
				timer = 0;
				timeAtLastLoop = System.currentTimeMillis();
				for(GameInstance gameInstance : instanceMap.values()) {
					gameInstance.tick(timer);
				}
				
			}
		}
	}	
	
	public void configSocketEvents() {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("Connected to localhost:8080");
			}
		}).on("socketID", new Emitter.Listener() {  // if connected to server, run instances
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String id = null;
				try {
					id = data.getString("id");
				} catch(JSONException e){
					System.out.println("SocketIO: " + "ID Error");
				}
				
				System.out.println("SocketIO: My ID: " + id);
				GameInstancesClient.this.id = id;
				socket.emit("gameClientInit");
				run();
			}
		}).on("requestJoinRoom", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				String username = null;
				String room = null;
				JSONObject data = (JSONObject) args[0];
				try {
					username = data.getString("username");
					room = data.getString("room");
					//socket.emit("newPlayerAcknowledge", idData);
				} catch(JSONException e) { e.printStackTrace(); }
				
				instanceMap.get(room).addPlayerToRoom(username);
			}
		}).on("playerReady", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String username = null;
				String room = null;
				try {
					username = data.getString("username");
					room = data.getString("room");
				} catch(JSONException e) { e.printStackTrace(); }
				
				instanceMap.get(room).addToReadyCount();
			}
		}).on("gameKeyPress", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String username = null;
				String room = null;
				String keyUp = null;
				String keyDown = null;
				try {
					username = data.getString("username");
					room = data.getString("room");
					keyUp = data.getString("keyUp");
					keyDown = data.getString("keyDown");
				} catch(JSONException e){ e.printStackTrace(); }
				
				instanceMap.get(room).gameKeyPress(username, keyUp, keyDown);
			}
		});
	}	

	public void emitJoinedRoomResponse(boolean response, String username, String roomName, String message) {
		JSONObject data = new JSONObject();
		try {
			data.put("response", response)
				.put("username", username)
				.put("room", roomName)
				.put("message", message);
		} catch (JSONException e) {
		}
		socket.emit("joinedRoomResponse", data);
	}
	
	public void emitGameSetup(String roomName,String playerData, String fogData, String mapData) {
		JSONObject data = new JSONObject();
		try {
			data.put("room", roomName)
				.put("playerData", playerData)
				.put("fogData", fogData)
				.put("mapData", mapData);
		} catch (JSONException e) { e.printStackTrace(); }
		
		socket.emit("gameSetup", data);
	}
	
	public void emitGameCountdown() {
		socket.emit("gameCountdown");
	}
	
	public void emitGameBegin(String roomName) {
		JSONObject data = new JSONObject();
		try {
			data.put("room", roomName);
		} catch (JSONException e) { e.printStackTrace(); }
		socket.emit("gameBegin", data);
	}
		
	public void emitGameUpdate(String roomName, String playerData, String fogData, String mapData) {
		JSONObject data = new JSONObject();
		try {
			data.put("room", roomName)
				.put("playerData", playerData)
				.put("fogData", fogData)
				.put("mapData", mapData);
		} catch (JSONException e) { e.printStackTrace(); }
		socket.emit("gameUpdate", data);
	}
		
	private GameInstance getGameInstance(String room) {
		GameInstance instance = instanceMap.get(room);
		
		if(instance == null) {  // no instance exists, so add new one
			instance = createGameInstance(room);
			instanceMap.put(room, instance);
		}

		return instance;
	}
	
	private GameInstance createGameInstance(String room) {
		return new GameInstance(room, new GameWorld(pbAssetManager), this);
	}


}

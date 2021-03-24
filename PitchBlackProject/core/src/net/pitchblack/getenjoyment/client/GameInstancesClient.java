package net.pitchblack.getenjoyment.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

public class GameInstancesClient implements ApplicationListener {
	private static final int UPDATE_TIME = (1 / 60) / 1000;  // 66.666 recurring milliseconds or 1/60th of a second therefore 60 times a second.
	private long timer;
	private long lastTime;
	private Socket socket;
	private String id;

	private final PBAssetManager pbAssetManager;
	private HashMap<String, GameInstance> instanceMap;  // room name mapped to instance

	public GameInstancesClient() {
		pbAssetManager = new PBAssetManager();
		connectSocket();
		configSocketEvents();
	}

	private void makeInstances(){
		instanceMap = new HashMap<String, GameInstance>();
		setupNewGameInstance("1");
		setupNewGameInstance("2");
		setupNewGameInstance("3");
		setupNewGameInstance("4");
		setupNewGameInstance("5");
	}
	
	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8081");  // game is on same machine as server, so localhost
			socket.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		GameInstancesClient c = new GameInstancesClient();

		while(true){
			c.run(Gdx.graphics.getDeltaTime());
		}
	}

	private void run(float delta) {
		for(GameInstance gameInstance : instanceMap.values()) {
			gameInstance.tick(delta);
		}
	}
	
	private void configSocketEvents() {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("Connected to localhost:8081");
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
				//GameInstancesClient.this.run();
			}
		}).on("joinRoomRequest", new Emitter.Listener() {
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
				GameInstance instance = GameInstancesClient.this.instanceMap.get(room);
				if(instance == null) {
					emitJoinRoomResponse(false, username, room, "Room is preparing a new game, try in a moment");
				} else {
					GameInstancesClient.this.instanceMap.get(room).addPlayerToRoom(username);
				}
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
				
				GameInstancesClient.this.instanceMap.get(room).addToReadyCount();
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
				
				GameInstancesClient.this.instanceMap.get(room).gameKeyPress(username, keyUp, keyDown);
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				String username = null;
				String room = null;
				try {
					username = data.getString("username");
					room = data.getString("room");
				} catch(JSONException e){ e.printStackTrace(); }

				GameInstancesClient.this.instanceMap.get(room).playerDisconnected(username);
			}
		});
	}	

	public void emitJoinRoomResponse(boolean response, String username, String roomName, String message) {
		JSONObject data = new JSONObject();
		try {
			data.put("response", response)
				.put("username", username)
				.put("room", roomName)
				.put("message", message);
		} catch (JSONException e) { e.printStackTrace(); }
		socket.emit("joinRoomResponse", data);
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
	
	public void emitGameCountdown(String roomName) {
        JSONObject data = new JSONObject();
        try {
            data.put("room", roomName);
        } catch (JSONException e) { e.printStackTrace(); }
		socket.emit("gameCountdown", data);
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

	public void emitGameFinish(String roomName, String winnerName) {
		JSONObject data = new JSONObject();
		try{
			data.put("room", roomName)
				.put("winnerName", winnerName);  // if null, do json null
		} catch (JSONException e) { e.printStackTrace(); }
		socket.emit("gameFinish", data);
	}

    public void emitGamePlayerDied(String room, ArrayList<String> recentlyDied) {
        JSONObject data = new JSONObject();
        try{
            data.put("room", room)
                .put("diedArray", new JSONArray().put(recentlyDied));
        } catch (JSONException e) { e.printStackTrace(); }
        socket.emit("gamePlayerDied", data);
    }

	public void emitResetRoom(String roomName) {
		JSONObject data = new JSONObject();
		try{
			data.put("room", roomName);
		} catch (JSONException e) { e.printStackTrace(); }
		socket.emit("resetRoom", data);
	}

	private void setupNewGameInstance(String room) {
		instanceMap.put(room, createGameInstance(room));
	}

	private GameInstance createGameInstance(String room) {
		return new GameInstance(room, pbAssetManager, this);
	}

	@Override
	public void create() {
		pbAssetManager.loadTextures();
		pbAssetManager.loadMaps();
		makeInstances();
	}

	@Override
	public void resize(int width, int height) {	}

	@Override
	public void render() {
		long nowTime = System.currentTimeMillis();
		float delta = (nowTime - lastTime); // difference in milliseconds
		timer += delta;
		if(timer >= UPDATE_TIME) {
			// System.out.println(timer);
			run(delta / 1000); // convert to seconds, as per Gdx.graphics.getDeltaTime();
			timer = 0;
		}
		lastTime = System.currentTimeMillis();

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	public void reinitialiseInstance(String room) {
		emitResetRoom(room);
		setupNewGameInstance(room);
	}
}

package net.pitchblack.getenjoyment.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.logic.GameRenderer;
import net.pitchblack.getenjoyment.logic.GameWorld;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
	
	private static final float UPDATE_TIME = 1/60f;
	private float timer;
	
	public enum RenderState{
		INITIATED,
		IDLE,
		LOADING,
		READY,
		PLAYING,
		FINISH, 
		WIN, 
		LOSE
	}
	
	private GameRenderer gameRenderer;
	private Player player;
	private String id;
	private Socket socket;
	private RenderState renderState;
	private String playerIds;
	private boolean playing;
	private int keyUpCode;
	private int keyDownCode;
	
	public Client() {
		//this.player = gameRenderer.getPlayer();
		renderState = RenderState.INITIATED;
		playerIds = "";
		playing = false;
		keyUpCode = -1;
		keyDownCode = -1;
		connectSocket();
		configSocketEvents();
	}

	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
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
//				JSONObject data = new JSONObject();
//				try{
//					data.put("id", id);
//					
//				} catch(JSONException e){
//					Gdx.app.log("SOCKET.IO", "Data Update Error");
//				}
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
		}
	}
	
	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
///				player = new Skins(Explorer);
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: "+ id);
					renderState = RenderState.IDLE;
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "ID Error");
				}
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
				} catch(JSONException e) {
				}
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
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "Player ID Error");
				}
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
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "Player ID Error");
				}
			}
		}).on("win", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				renderState = RenderState.WIN;
			}
			
		});
}
	
//		}).on("playerDisconnected", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				JSONObject data = (JSONObject) args[0];
//				try{
//					String id = data.getString("id");
//					gameWorld.removePlayer(id);
//				}catch(JSONException e){
//					Gdx.app.log("SocketIO", "Player ID Error");
//				}
//			}
//		}).on("playerMoved", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				JSONObject data = (JSONObject) args[0];
//				try{
//					String playerId = data.getString("id");
//					Double x = data.getDouble("x");
//					Double y = data.getDouble("y");
//					gameWorld.movePlayer(playerId, x.floatValue(), y.floatValue());
//					
//				} catch(JSONException e){
//				}
//			}
//		}).on("getPlayers",new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				JSONArray objects = (JSONArray) args[0];
//				try {
//					for(int i = 0; i < objects.length(); i++) {
//						gameWorld.movePlayer(objects.getJSONObject(i).getString("id"), 
//								(float) objects.getJSONObject(i).getDouble("x"),
//								(float) objects.getJSONObject(i).getDouble("y"));
//					}
//				} catch (JSONException e) {
//					
//				}
//			}

	public void render(float delta) {
		updateServer(delta);
	}

	public void addRenderer(GameRenderer gameRenderer) {
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
}

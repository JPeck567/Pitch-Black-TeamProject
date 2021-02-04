package net.pitchblack.getenjoyment.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class ClientGame extends ApplicationAdapter {
	private static final float UPDATE_TIME = 1/60f;
	private static final int PLAYER_MAX = 1;
	private float timer;
	
	public enum GameState{
		INITIATED,
		IDLE,
		WAITING,
		READY,
		WAITINGFORPLAYERS,
		PLAYING,
		FINISH,
	}
	
	private GameWorld gameWorld;
	private ArrayList<String> players;
	private int mapCount;
	int readyCount;
	private Player player;
	private GameState gameState;
	private String id;
	private Socket socket;
	
	public ClientGame(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		players = new ArrayList<String>();
		mapCount = gameWorld.getMapSequence().split("/n").length;
		readyCount = 0;
		//this.player = gameWorld.getPlayer("0");
		gameState = GameState.INITIATED;
		connectSocket();
		configSocketEvents();
	}
	
	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");  // client game on same machine as server, so uses localhost
			socket.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: "+ id);
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "ID Error");
				}
			}
		}).on("gameClientAcknowledge", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				gameState = GameState.WAITING;
			}
			
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					players.add(playerId);
					Gdx.app.log("SocketIO", "New Player Connected: " + id);
					gameWorld.createPlayer(playerId);
					JSONObject idData = new JSONObject();
					idData.put("id", playerId);
					socket.emit("newPlayerAcknowledge", idData);
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "Player ID Error");
				}
			}
		}).on("playerReady", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				readyCount++;
			}
		}).on("keyPress", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try{
					String id = data.getString("id");
					String keyUp = data.getString("keyUp");
					String keyDown = data.getString("keyDown");
					gameWorld.keyPress(id, keyUp, keyDown);
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Player ID Error");
				}
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
//					for(int i = 0; i < objects.length(); i++){
//						gameWorld.movePlayer(objects.getJSONObject(i).getString("id"), 
//								(float) objects.getJSONObject(i).getDouble("x"),
//								(float) objects.getJSONObject(i).getDouble("y"));
//					}
//				} catch (JSONException e){
//				}
//			}

		
	public void updateServer(float delta){
		timer += delta;
//		if(timer >= UPDATE_TIME) {
//		System.out.println(gameState);
		switch(gameState){
			case INITIATED:
				socket.emit("gameClientInit");
				gameState = GameState.IDLE;
				break;
			case WAITING:
				if(players.size() == PLAYER_MAX) {
					gameState = GameState.READY;
				}
				break;
			case READY:
				JSONObject idData = new JSONObject();
				try {
					String idString = "";
					for(String id : players) {
						idString += id + "/n";
					}
					idData.put("playerId", idString);
				} catch (JSONException e) {
				}
				socket.emit("gameReady", idData);
				gameState = GameState.WAITINGFORPLAYERS;
				break;
			case WAITINGFORPLAYERS:
				if(readyCount == PLAYER_MAX) {
					gameState = GameState.PLAYING;
					JSONObject data = new JSONObject();
					try {
						data.put("playerData", gameWorld.getPlayerData());
						data.put("fogData", gameWorld.getFogData());
						data.put("mapData", gameWorld.getMapSequence().toString());
					} catch (JSONException e) {
					}
					socket.emit("gameStart", data);
				}
				break;
			case PLAYING:
				gameWorld.update(delta);
				JSONObject data = new JSONObject();
				try {
					data.put("playerData", gameWorld.getPlayerData());
					data.put("fogData", gameWorld.getFogData());
					data.put("mapData", gameWorld.getMapSequence().toString());
				} catch (JSONException e) {
				}
				socket.emit("gameUpdate", data);
				
				if(gameWorld.finished()) {
					gameState = GameState.FINISH;
				}
				break;
				
			case FINISH:
				Player p = gameWorld.getWinner();
				JSONObject winID = new JSONObject();
				try {
					winID.put("id", p.getID());
				} catch (JSONException e) {
				}
				socket.emit("win", winID);
				break;
			default:
				break;
		}
	}
//	}
	
	public void render(float delta) {
		//System.out.println("Heelo");
		updateServer(delta);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
	
}
package net.pitchblack.getenjoyment.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.logic.GameWorld;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
	
	private static final float UPDATE_TIME = 1/60f;
	private float timer;
	
	private GameWorld gameWorld;
	private Player player;
	private String id;
	private Socket socket;
	
	public Client(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		this.player = gameWorld.getPlayer("0");
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
	
	public void updateServer(float dt){
		timer += dt;
		if(timer >= UPDATE_TIME && player != null && player.hasMoved()){
			JSONObject data = new JSONObject();
			try{
				data.put("x", player.getX());
				data.put("y", player.getY());
				socket.emit("playerMoved", data);
			} catch(JSONException e){
				Gdx.app.log("SOCKET.IO", "Data Update Error");
			}
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
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: "+ id);
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "ID Error");
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					Gdx.app.log("SocketIO", "New Player Connected: " + id);
					//gameWorld.addPlayer(playerId, null);
				} catch(JSONException e){
					Gdx.app.log("SocketIO", "Player ID Error");
				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try{
					String id = data.getString("id");
					gameWorld.removePlayer(id);
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Player ID Error");
				}
			}
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try{
					String playerId = data.getString("id");
					Double x = data.getDouble("x");
					Double y = data.getDouble("y");
					gameWorld.movePlayer(playerId, x.floatValue(), y.floatValue());
					
				} catch(JSONException e){
				}
			}
		}).on("getPlayers",new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray objects = (JSONArray) args[0];
				try {
					for(int i = 0; i < objects.length(); i++){
						gameWorld.movePlayer(objects.getJSONObject(i).getString("id"), 
								(float) objects.getJSONObject(i).getDouble("x"),
								(float) objects.getJSONObject(i).getDouble("y"));
					}
				} catch (JSONException e){
				}
			}
		});
	}
}

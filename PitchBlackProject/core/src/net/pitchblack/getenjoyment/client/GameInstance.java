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

public class GameInstance {
	private static final float UPDATE_TIME = 1/30f;  // 30 times a second
	private static final int PLAYER_MAX = 1;
	private final String roomName;
	private float timer;
	
	public enum GameState {
		INITIATED,
		IDLE,
		WAITING,  // waiting for PLAYER_MAX players to join
		READY,  // checking if all clients loaded data and ready
		COUNTDOWN,
		PLAYING,
		FINISH,
	}
	
	private GameInstancesClient instanceClient;
	private GameWorld gameWorld;
	private ArrayList<String> players;
	private int mapCount;
	private int readyCount;
	private Player player;
	private GameState gameState;
	private String id;

	
	public GameInstance(String roomName, GameWorld gameWorld, GameInstancesClient gameClient) {
		this.roomName = roomName;
		this.gameWorld = gameWorld;
		players = new ArrayList<String>();
		mapCount = gameWorld.getMapSequence().split("/n").length;
		readyCount = 0;
		gameState = GameState.INITIATED;
	}
		
//	public void configSocketEvents(){
//		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				Gdx.app.log("SocketIO", "Connected");
//			}
//		}).on("socketID", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				JSONObject data = (JSONObject) args[0];
//				try {
//					id = data.getString("id");
//					Gdx.app.log("SocketIO", "My ID: "+ id);
//				} catch(JSONException e){
//					Gdx.app.log("SocketIO", "ID Error");
//				}
//			}
//		}).on("gameClientAcknowledge", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				gameState = GameState.WAITING;
//			}	
//		}).on("newPlayer", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				JSONObject data = (JSONObject) args[0];
//				try {
//					String playerId = data.getString("id");
//					players.add(playerId);
//					Gdx.app.log("SocketIO", "New Player Connected: " + id);
//					gameWorld.createPlayer(playerId);
//					JSONObject idData = new JSONObject();
//					idData.put("id", playerId);
//					socket.emit("newPlayerAcknowledge", idData);
//				} catch(JSONException e) {
//					Gdx.app.log("SocketIO", "Player ID Error");
//				}
//			}
//		}).on("playerReady", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				readyCount++;
//			}
//		}).on("keyPress", new Emitter.Listener() {
//			@Override
//			public void call(Object... args) {
//				JSONObject data = (JSONObject) args[0];
//				try {
//					String id = data.getString("id");
//					String keyUp = data.getString("keyUp");
//					String keyDown = data.getString("keyDown");
//					gameWorld.keyPress(id, keyUp, keyDown);
//				} catch(JSONException e) {
//					Gdx.app.log("SocketIO", "Player ID Error");
//				}
//			}
//		});
//	}
	
	public void updateServer(float timePassed){
		switch(gameState){
			case WAITING:
				if(players.size() == PLAYER_MAX) {
					gameWorld.setupPlayers(players);
										
					instanceClient.emitGameSetup(roomName, gameWorld.getPlayerData(), gameWorld.getFogData(), gameWorld.getMapSequence());
					
					gameState = GameState.READY;
					
					/*
					 * 1. DONE when full, send data
					 * 2. DONE have client load + have player click ready button
					 * 3. DONE when 4 clients ready, send message to begin in 5 seconds
					 * 4. after 5 sec, server side, send message to begin game!
					 */
					
					timer = 0;
				}
				break;
			case READY:
				if(readyCount == PLAYER_MAX) {
					instanceClient.emitGameCountdown();
				}
				gameState = GameState.COUNTDOWN;
				break;
			case COUNTDOWN:
				timer += timePassed;
				if(timer >= 5000) {  // 5 seconds
					instanceClient.emitGameBegin(roomName);
					gameState = GameState.PLAYING;
				}
				break;
			case PLAYING:
				gameWorld.update(timePassed);

				instanceClient.emitGameUpdate(roomName, gameWorld.getPlayerData(), gameWorld.getFogData(), gameWorld.getMapSequence());
				
				if(gameWorld.finished()) {
					gameState = GameState.FINISH;
				}
				
				break;
			default:
				break;
					
		}	
	}	
	//			case FINISH:
	//				Player p = gameWorld.getWinner();
	//				JSONObject winID = new JSONObject();
	//				try {
	//					winID.put("id", p.getID());
	//				} catch (JSONException e) {
	//				}
	//				socket.emit("win", winID);
	//				break;
	//			default:
	//				break;
	
	public void addPlayerToRoom(String username) {
		if(players.size() < PLAYER_MAX) {
			if(!players.contains(username)) {
				instanceClient.emitJoinedRoomResponse(false, username, roomName, "Player already in room");
			} else {
				instanceClient.emitJoinedRoomResponse(true, username, roomName, "");
				players.add(username);
			}
		} else {
			instanceClient.emitJoinedRoomResponse(false, username, roomName, "Room full!");
		}
	}
	
	public void addToReadyCount() {
		readyCount++;
	}
	
	public void tick(float timePassed) {
		updateServer(timePassed);
	}

	public void gameKeyPress(String username, String keyUp, String keyDown) {
		gameWorld.keyPress(username, keyUp, keyDown);
	}
}
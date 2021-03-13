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
	private static final int COUNTDOWN_INTERVAL = 5; // in seconds
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

	
	public GameInstance(String roomName, GameWorld gameWorld, GameInstancesClient instanceClient) {
		this.instanceClient = instanceClient;
		this.roomName = roomName;
		this.gameWorld = gameWorld;
		players = new ArrayList<String>();
		mapCount = gameWorld.getMapSequence().split("/n").length;
		readyCount = 0;
		gameState = GameState.WAITING;
	}

	public void updateServer(float timePassed){
		//System.out.println(gameState);
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
				// System.out.println(timer + ": " + timePassed);
				// System.out.println("GameInstanceTimer: " + timer);
				timer += timePassed;
				if(timer >= COUNTDOWN_INTERVAL) {  // 5 seconds
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
			if(players.contains(username)) {
				instanceClient.emitJoinRoomResponse(false, username, roomName, "Player already in room");
			} else {
				instanceClient.emitJoinRoomResponse(true, username, roomName, "Player joined successfully");
				players.add(username);
			}
		} else {
			instanceClient.emitJoinRoomResponse(false, username, roomName, "Room full!");
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
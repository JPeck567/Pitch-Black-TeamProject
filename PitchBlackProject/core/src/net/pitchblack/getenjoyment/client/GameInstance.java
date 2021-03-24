package net.pitchblack.getenjoyment.client;

import java.util.ArrayList;

import net.pitchblack.getenjoyment.helpers.PBAssetManager;

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class GameInstance {
	public static final int PLAYER_MAX = 1;
	private static final float UPDATE_TIME = 1/30f;  // 30 times a second
	private static final int COUNTDOWN_INTERVAL = 5; // in seconds
	private final String roomName;
	private float timer;

    public enum GameState {
		INITIATED,
		IDLE,
		WAITING,  // waiting for PLAYER_MAX players to join
		READY,  // checking if all clients loaded data and ready
		COUNTDOWN,
		PLAYING,
		FINISH;

	}
	private GameInstancesClient instanceClient;
    private PBAssetManager pbAssetManager;
    private GameWorld gameWorld;

	private ArrayList<String> players;
	private ArrayList<String> recentlyDied;
	private Player player;
	private GameState gameState;
	private String id;
	private int readyCount;

	public GameInstance(String roomName, PBAssetManager pbAssetManager, GameInstancesClient instanceClient) {
		this.instanceClient = instanceClient;
		this.roomName = roomName;
		this.pbAssetManager = pbAssetManager;
		this.gameWorld = new GameWorld(pbAssetManager, this);
		players = new ArrayList<String>();
		recentlyDied = new ArrayList<String>();
		gameState = GameState.WAITING;
		readyCount = 0;
	}

	public void tick(float timePassed) {
		updateServer(timePassed);
	}

	public void updateServer(float timePassed){
		switch(gameState){
			case WAITING:
				if(players.size() == PLAYER_MAX) {

					gameWorld.setupPlayers(players);
					instanceClient.emitGameSetup(roomName, gameWorld.getPlayerData(), gameWorld.getFogData(), gameWorld.getMapSequence());
					gameState = GameState.READY;

					/*
					 * 1. when full, send data
					 * 2. have client load + have player click ready button
					 * 3. when 4 clients ready, send message to begin in 5 seconds
					 * 4. after 5 sec, server side, send message to begin game & start game sim
					 */
					timer = 0;
				}
				break;
			case READY:
				if(readyCount == PLAYER_MAX) {
					instanceClient.emitGameCountdown(roomName);
				}
				gameState = GameState.COUNTDOWN;
				break;
			case COUNTDOWN:
				timer += timePassed;
				if(timer >= COUNTDOWN_INTERVAL) {  // 5 seconds
					instanceClient.emitGameBegin(roomName);
					gameState = GameState.PLAYING;
					System.out.println("Room " + roomName + " starting game");
				}
				break;
			case PLAYING:
			    //System.out.println(timePassed);
			    //System.out.println(gameWorld.getPlayerData());
				gameWorld.update(timePassed);

				if(recentlyDied.size() > 0){
					instanceClient.emitGamePlayerDied(roomName, recentlyDied);
					recentlyDied.clear();
				}


				instanceClient.emitGameUpdate(roomName, gameWorld.getPlayerData(), gameWorld.getFogData(), gameWorld.getMapSequence());

				if(gameWorld.finished()) {
					gameState = GameState.FINISH;
					// carry on to next state
				} else {
					break;
				}
			case FINISH:
				String winnerName = (PLAYER_MAX > 1) ? gameWorld.getWinner() : ""; // if playing with others, get winner

                if(!winnerName.equals("")) {  // if single player, player already died and removed from server vars
                    instanceClient.emitGameFinish(roomName, winnerName);
                }

				refreshInstance();  // return to state of waiting & clear room

				System.out.println("Room " + roomName + "refreshing game");
			default:
				break;
		}
	}

	public void addPlayerToRoom(String username) {
        if (gameState == GameState.WAITING) {  // if not waiting for players
            if (players.size() < PLAYER_MAX) {
                if (players.contains(username)) {
                    instanceClient.emitJoinRoomResponse(false, username, roomName, "Player already in room");
                } else {
                    instanceClient.emitJoinRoomResponse(true, username, roomName, "Player joined successfully");
                    players.add(username);
                }
            } else {
                instanceClient.emitJoinRoomResponse(false, username, roomName, "Room full!");
            }
        } else if (gameState == GameState.PLAYING) {
            instanceClient.emitJoinRoomResponse(false, username, roomName, "Game is in progress!");
        }
	}

	public void addToReadyCount() {
		readyCount++;
	}

	public void gameKeyPress(String username, String keyUp, String keyDown) {
		if(gameState == GameState.PLAYING) {
			gameWorld.keyPress(username, keyUp, keyDown);
		}
	}

	public void playerDisconnected(String username) {  // player disconnect
		players.remove(username);
		System.out.println("Removed player: " + username);
		if(gameState == GameState.PLAYING) {
			gameWorld.removePlayer(username);
		}
	}

    public void addToRecentlyDied(ArrayList<String> diedArray) {
        recentlyDied.addAll(diedArray);
    }

	private void refreshInstance(){
	    instanceClient.reinitialiseInstance(roomName);
        /*
		instanceClient.emitResetRoom(roomName);
		gameWorld = new GameWorld(pbAssetManager, this);
		players.clear();

		readyCount = 0;
		gameState = GameState.WAITING;
		*/
	}
}
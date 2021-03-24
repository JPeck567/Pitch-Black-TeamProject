package net.pitchblack.getenjoyment.graphics;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.client.Client.AccountState;
import net.pitchblack.getenjoyment.client.Client.ClientState;
import net.pitchblack.getenjoyment.graphics.screens.*;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen.GameState;
import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator.WindowType;

import net.pitchblack.getenjoyment.helpers.PBAssetManager;

public class PitchBlackGraphics extends Game {
	public static final String LOG = "PitchBlack";


    public enum Screens{
		MENU,
		LOBBY,
		GAME,
		SETTINGS,
		WIN,
		LOSE,
		CREDITS;

	}
	private LoginInitiator loginInit;
	private MenuScreen menuScreen;

	private LobbyScreen lobbyScreen;

	private GameScreen gameScreen;
	private WinScreen winScreen;
	private LoseScreen loseScreen;
	private SettingsScreen settingsScreen;
	private CreditsScreen creditsScreen;
	private Client client;
	public final PBAssetManager pbAssetManager = new PBAssetManager();

	public PitchBlackGraphics(){
		client = new Client(this);
	}
	@Override
	public void create() {
		// Load assets first
		pbAssetManager.loadTextures();
		pbAssetManager.loadMaps();
		pbAssetManager.loadMenuAssets();
		client.setClientState(ClientState.ACCOUNT);
		client.beginConnection();
		new Thread(new Runnable() {
			   @Override
			   public void run() {
			      // occurs asynchronously to the rendering thread. menu not rendered until logged in
			      loginInit = new LoginInitiator(PitchBlackGraphics.this, client);
				  loginInit.setWindow(WindowType.OPTIONS);
			   }
			}).start();
	}

	public void changeScreen(Screens screen) {
		switch (screen) {
			case MENU:
				if (screenIsNull(menuScreen)) {
					menuScreen = new MenuScreen(this, client);
				}
				this.setScreen(menuScreen);
				break;
			case LOBBY:
				if (screenIsNull(lobbyScreen)) {
					lobbyScreen = new LobbyScreen(this, client);
				} else {
					lobbyScreen.restartLobby();
				}
				this.setScreen(lobbyScreen);
				break;
			case GAME:
				if (screenIsNull(gameScreen)) {
					gameScreen = new GameScreen(this, client);
				}
				this.setScreen(gameScreen);
				break;
			case SETTINGS:
				if (screenIsNull(settingsScreen)) {
					settingsScreen = new SettingsScreen(this);
				}
				this.setScreen(settingsScreen);
				break;
			case WIN:
				if (screenIsNull(winScreen)) {
					winScreen = new WinScreen(this);
				}
				this.setScreen(winScreen);
				break;
			case LOSE:
				if (screenIsNull(loseScreen)) {
					loseScreen = new LoseScreen(this);
				}
				this.setScreen(loseScreen);
				break;
			case CREDITS:
				if (screenIsNull(creditsScreen)) {
					creditsScreen = new CreditsScreen(this);
					this.setScreen(creditsScreen);
				}
		default:
			break;
		}
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	private boolean screenIsNull(Screen screen){
		return screen == null;
	}

	public void addLobbyRoomData(HashMap<String, ArrayList<String>> roomUsersMap) {
		if(!screenIsNull(lobbyScreen)){
			lobbyScreen.addRoomData(roomUsersMap);
		}
	}

	public void addLobbyNewPlayer(String username, String room) {
		if(!screenIsNull(lobbyScreen)) {
			lobbyScreen.addNewPlayer(username, room);
		}
	}

	public void removeLobbyPlayer(String username, String room) {
		if(!screenIsNull(lobbyScreen)) {
			lobbyScreen.removePlayer(username, room);
		}
	}

	public void lobbyJoinRoomResponse(Boolean joined, String room, String message) {
		if(!screenIsNull(lobbyScreen)) {
			lobbyScreen.joinRoomResponse(joined, room, message);
		}
	}
	public boolean isLobbyScreenReady() {
		if(screenIsNull(lobbyScreen)){
			return false;
		}
		return lobbyScreen.ready();
	}

	public void setupGameScreen(final String playerData, final String fogData, final String mapData) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (gameScreen == null) {
					gameScreen = new GameScreen(PitchBlackGraphics.this, client);;
				}
				gameScreen.setupRenderer(playerData, fogData, mapData);
				client.setClientState(ClientState.READY);
			}
		});
	}

	public void lobbyResetRoom(String room) {
		if(!screenIsNull(lobbyScreen)){
			lobbyScreen.resetRoom(room);
		}
	}

	public void gameAddToGameDataBuffer(String playerData, String fogData, String mapData) {
		gameScreen.addToGameDataBuffer(playerData, fogData, mapData);
	}

	public void gameScreenSetState(GameState gameState) {
		gameScreen.setGameState(gameState);
	}

	public void postRunnableChangeScreen(final Screens screen) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				changeScreen(screen);
			}
		});
	}

	@Override
	public void render() {
		if(client.getAccountState() == AccountState.LOGGED_IN) {
			client.tick(Gdx.graphics.getDeltaTime());
			super.render();
		}
	}
}

package net.pitchblack.getenjoyment.graphics;

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
		LOSE;

		// CREDITS
	}
	private LoginInitiator loginInit;

	private WelcomeScreen welcomeScreen;

	private LobbyScreen lobbyScreen;
	private GameScreen gameScreen;
	private WinScreen winScreen;
	private LoseScreen loseScreen;
	private SettingsScreen settingsScreen;
	//private CreditsScreen creditsScreen;
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
				if (welcomeScreen == null) {
					welcomeScreen = new WelcomeScreen(this, client);
				}
				this.setScreen(welcomeScreen);
				break;
			case LOBBY:
				if (lobbyScreen == null) {
					lobbyScreen = new LobbyScreen(this, client);
				}
				this.setScreen(lobbyScreen);
				break;
			case GAME:
				if (gameScreen == null) {
					gameScreen = new GameScreen(this, client);
				}
				this.setScreen(gameScreen);
				break;
			case SETTINGS:
				if (settingsScreen == null) {
					settingsScreen = new SettingsScreen(this);
				}
				this.setScreen(settingsScreen);
				break;
			case WIN:
				if (winScreen == null) {
					winScreen = new WinScreen(this);
				}
				this.setScreen(winScreen);
				break;
			case LOSE:
				if (loseScreen == null) {
					loseScreen = new LoseScreen(this);
				}
				this.setScreen(loseScreen);
				break;
//			case CREDITS:
//				if (creditsScreen == null) {
//					creditsScreen = new CreditsScreen(this);
//					this.setScreen(creditsScreen);
//				}
		default:
			break;
		}
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}
	public void addLobbyRoomData(HashMap roomUsersMap) {
		if(lobbyScreen != null){
			lobbyScreen.addRoomData(roomUsersMap);
		}
	}
	public void addLobbyNewPlayer(String username, String room) {
		if(lobbyScreen != null) {
			lobbyScreen.addNewPlayer(username, room);
		}
	}
	public void lobbyJoinRoomResponse(Boolean joined, String room, String message) {
		if(lobbyScreen != null) {
			lobbyScreen.joinRoomResponse(joined, room, message);
		}
	}

	public boolean isLobbyScreenReady() {
		if(lobbyScreen == null){
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

	public void addGameData(String playerData, String fogData, String mapData) {
		gameScreen.addGameData(playerData, fogData, mapData);
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

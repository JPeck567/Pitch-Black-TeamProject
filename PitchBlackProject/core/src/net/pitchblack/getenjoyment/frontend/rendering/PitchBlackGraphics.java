package net.pitchblack.getenjoyment.frontend.rendering;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import net.pitchblack.getenjoyment.frontend.client.Client;
import net.pitchblack.getenjoyment.frontend.client.Client.AccountState;
import net.pitchblack.getenjoyment.frontend.client.Client.ClientState;
import net.pitchblack.getenjoyment.frontend.rendering.screens.game.GameScreen;
import net.pitchblack.getenjoyment.frontend.rendering.screens.game.GameScreen.GameState;

import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.rendering.screens.ui.*;

public class PitchBlackGraphics extends Game {
	public static final String LOG = "PitchBlack";

	public enum Screens{
		MENU,
		SETTINGS,
		CREDITS,
		LOBBY,
		INTERMISSION,
		COUNTDOWN,
		GAME,
		WIN,
		LOSE;
	}

	private LoginInitiator loginInit;
	private MenuScreen menuScreen;
	private LobbyScreen lobbyScreen;

	private GameScreen gameScreen;

	private WinScreen winScreen;
	private LoseScreen loseScreen;
	private SettingsScreen settingsScreen;
	private CreditsScreen creditsScreen;
	private IntermissionScreen intermissionScreen;
	private CountdownScreen countdownScreen;

	private Client client;
	public final PBAssetManager pbAssetManager = new PBAssetManager();

	public PitchBlackGraphics(String serverURL) {
		client = new Client(this, serverURL);
	}

	@Override
	public void create() {
		// Load assets first
		pbAssetManager.loadTextures();
		pbAssetManager.loadMaps();
		pbAssetManager.loadMenuAssets();
		countdownScreen = new CountdownScreen(this);
		// pre-made as vital countdown has to be precice. if was making object aserver
		// counts down will yield client render slightly inaccurate.

		client.setClientState(ClientState.ACCOUNT);
		client.beginConnection();
		new Thread(new Runnable() {
			   @Override
			   public void run() {
			      // occurs asynchronously to the rendering thread. menu not rendered until logged in
			      loginInit = new LoginInitiator(PitchBlackGraphics.this, client);
				  loginInit.setWindow(LoginInitiator.WindowType.OPTIONS);
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
			case COUNTDOWN:
				this.setScreen(countdownScreen);
				break;
			case GAME:
				if (screenIsNull(gameScreen)) {
					gameScreen = new GameScreen(this, client);
				} else {
					gameScreen.restartGame();
				}
				Gdx.input.setInputProcessor(gameScreen.getInputHandler());
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
				}
				this.setScreen(creditsScreen);
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

	public void lobbyRoomInSession(String room) {
		lobbyScreen.setRoomInSession(room);
	}

	public void setupGameScreen(final String playerData, final String fogData, final String mapData) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (gameScreen == null) {
					gameScreen = new GameScreen(PitchBlackGraphics.this, client);;
				}
				gameScreen.setupRenderer(playerData, fogData, mapData);
				// once data loaded, show ready button
				lobbyScreen.setReadyButtonVisible();
				// client.setClientState(ClientState.READY);
			}
		});
	}

	public void setCountdownSeconds(int countdownSeconds){
		countdownScreen.setMillisecondsLeft(countdownSeconds);
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

	public IntermissionScreen getIntermissionScreen(){
		if(screenIsNull(intermissionScreen)){
			intermissionScreen = new IntermissionScreen(this);
		}
		return intermissionScreen;
	}

	@Override
	public void render() {
		if(client.getAccountState() == AccountState.LOGGED_IN) {
			client.tick(Gdx.graphics.getDeltaTime());
			super.render();
		}
	}
}

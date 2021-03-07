package net.pitchblack.getenjoyment.graphics;

import java.awt.EventQueue;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.client.Login;
import net.pitchblack.getenjoyment.client.Registration;
import net.pitchblack.getenjoyment.client.Client.AccountState;
import net.pitchblack.getenjoyment.client.Client.ClientState;
//import net.pitchblack.getenjoyment.graphics.screens.CreditsScreen;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.graphics.screens.LobbyScreen;
import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator;
import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator.WindowType;
import net.pitchblack.getenjoyment.graphics.screens.SettingsScreen;
//import net.pitchblack.getenjoyment.graphics.screens.SettingsScreen;
import net.pitchblack.getenjoyment.graphics.screens.WelcomeScreen;
//import net.pitchblack.getenjoyment.helpers.AssetLoader;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

public class PitchBlackGraphics extends Game {
	public static final String LOG = "PitchBlack";
	
	public enum Screens{
		MENU,
		LOBBY,
		GAME,
		SETTINGS,
		CREDITS
	}
	
	private LoginInitiator loginInit;
	private WelcomeScreen welcomeScreen;
	private LobbyScreen lobbyScreen;
	private GameScreen gameScreen;
	private SettingsScreen settingsScreen;
	//private CreditsScreen creditsScreen;
	private Client client;
	
	public final PBAssetManager pbAssetManager = new PBAssetManager();
	
	public PitchBlackGraphics(){
		//pbAssetManager.loadSkins();
		client = new Client();
	}

	@Override
	public void create() {
		client.setClientState(ClientState.ACCOUNT);
		client.beginConnection();
		new Thread(new Runnable() {
			   @Override
			   public void run() {
			      // occurs asynchronously to the rendering thread
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
	
	public GameScreen getGameScreen() {
		if (gameScreen == null) {
			gameScreen = new GameScreen(this, client);
		}
		return gameScreen;
	}
	
	@Override
	public void render() {
		if(client.getAccountState() == AccountState.LOGGED_IN) {
			super.render();	
		}
	}
}

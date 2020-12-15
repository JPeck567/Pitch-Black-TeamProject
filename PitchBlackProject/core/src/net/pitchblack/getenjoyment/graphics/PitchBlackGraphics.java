//package net.pitchblack.getenjoyment.graphics;
//
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Game;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//
//import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
//import net.pitchblack.getenjoyment.graphics.screens.WelcomeScreen;
//import net.pitchblack.getenjoyment.helpers.PBAssetManager;
//
//public class PitchBlackGraphics extends Game {
//	public static final int MENU = 0;
//	public static final int GAME = 1;
//	private WelcomeScreen welcomeScreen;
//	private GameScreen gameScreen;
//	public final PBAssetManager pbAssetManager = new PBAssetManager();
//
//	@Override
//	public void create() {
//		changeScreen(0);
//	}
//
//	public void changeScreen(int screen) {
//		switch (screen) {
//		case MENU:
//			if (welcomeScreen == null)
//				welcomeScreen = new WelcomeScreen(this);
//			this.setScreen(welcomeScreen);
//			break;
//		case GAME:
//			if (gameScreen == null)
//				gameScreen = new GameScreen(this);
//			this.setScreen(gameScreen);
//			break;
//		}
//	}
//}

package net.pitchblack.getenjoyment.graphics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

//import net.pitchblack.getenjoyment.graphics.screens.CreditsScreen;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
//import net.pitchblack.getenjoyment.graphics.screens.SettingsScreen;
import net.pitchblack.getenjoyment.graphics.screens.WelcomeScreen;
//import net.pitchblack.getenjoyment.helpers.AssetLoader;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

public class PitchBlackGraphics extends Game {
	public static final String LOG = "PitchBlack";

	public static final int MENU = 0;
	public static final int GAME = 1;
	public static final int SETTINGS = 2;
	public static final int CREDITS = 3;
	private WelcomeScreen welcomeScreen;
	private GameScreen gameScreen;
//	private SettingsScreen settingsScreen;
//	private CreditsScreen creditsScreen;

	public final PBAssetManager pbAssetManager = new PBAssetManager();

	static public Skin gameskin;

	@Override
	public void create() {
		changeScreen(0);
		//this.setScreen(0);
	}

	public void changeScreen(int screen) {
		switch (screen) {
		case MENU:
			if (welcomeScreen == null)
				welcomeScreen = new WelcomeScreen(this);
			this.setScreen(welcomeScreen);
			break;
		case GAME:
			if (gameScreen == null)
				gameScreen = new GameScreen(this);
			this.setScreen(gameScreen);
			break;
//		case SETTINGS:
//			if (settingsScreen == null) {
//				settingsScreen = new SettingsScreen(this);
//				this.setScreen(settingsScreen);
//				break;
//			}
//		case CREDITS:
//			if (creditsScreen == null) {
//				creditsScreen = new CreditsScreen(this);
//				this.setScreen(creditsScreen);
//			}
		}
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	public void render() {
		super.render();
	}
}

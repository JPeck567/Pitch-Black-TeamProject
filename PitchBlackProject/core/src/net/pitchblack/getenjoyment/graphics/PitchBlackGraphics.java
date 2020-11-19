package net.pitchblack.getenjoyment.graphics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.graphics.screens.WelcomeScreen;
import net.pitchblack.getenjoyment.helpers.AssetLoader;

public class PitchBlackGraphics extends Game {
	public static final int MENU = 0;
	public static final int GAME = 1;
	private WelcomeScreen welcomeScreen;
	private GameScreen gameScreen;

	@Override
	public void create() {
		AssetLoader.load();
		changeScreen(0);
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
		}
	}
}

package net.pitchblack.getenjoyment.frontend.helpers;

import net.pitchblack.getenjoyment.frontend.game.screens.GameScreen;

import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {
	private GameScreen gameScreen;
	
	public InputHandler(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	@Override
	public boolean keyDown(int keycode) {
		gameScreen.keyDown(keycode);
		return true; // to show key press has been handled
	}

	@Override
	public boolean keyUp(int keycode) {
		gameScreen.keyUp(keycode);
		return true; // to show key press has been handled
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

}

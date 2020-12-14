package net.pitchblack.getenjoyment.helpers;

import com.badlogic.gdx.Input.Keys;

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.logic.GameRenderer;
import net.pitchblack.getenjoyment.logic.GameWorld;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public class InputHandler implements InputProcessor {
	private GameRenderer gameRenderer;
	
	// for server, send message, then call appropriate method in game world
	
	public InputHandler(GameRenderer gameRenderer) {
		this.gameRenderer = gameRenderer;
	}

	@Override
	public boolean keyDown(int keycode) {
		gameRenderer.keyDown(keycode);
		return true; // to show key press has been handled
	}

	@Override
	public boolean keyUp(int keycode) {
		gameRenderer.keyUp(keycode);
		return true; // to show key press has been handled
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

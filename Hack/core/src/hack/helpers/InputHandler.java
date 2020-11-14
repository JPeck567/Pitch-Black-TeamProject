package hack.helpers;

import com.badlogic.gdx.Input.Keys;

import hack.entities.Player;
import hack.graphics.HackGraphicsTest;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public class InputHandler implements InputProcessor {
	private Player player;
	
	public InputHandler(Player player) {
		this.player = player;
	}

	@Override
	public boolean keyDown(int keycode) {
		player.keyDown(keycode);
		return true; // to show key press has been handled
	}

	@Override
	public boolean keyUp(int keycode) {
		player.keyUp(keycode);
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

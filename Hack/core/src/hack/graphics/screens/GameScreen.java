package hack.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import hack.client.Client;
import hack.graphics.HackGraphicsTest;
import hack.helpers.AssetLoader;
import hack.helpers.InputHandler;
import hack.logic.GameRenderer;
import hack.logic.GameWorld;

public class GameScreen implements Screen {
	
	private HackGraphicsTest parent;
	
	private GameWorld gameWorld;
	private GameRenderer gameRenderer;
	private InputHandler inputHandler;
	
	private Client client;
	
	public GameScreen(HackGraphicsTest hGT) {
		parent = hGT;
				
		gameWorld = new GameWorld();
		gameRenderer = new GameRenderer(gameWorld);	
		inputHandler = new InputHandler(gameWorld.getPlayer());
		
		client = new Client(gameWorld);
		
		Gdx.input.setInputProcessor(inputHandler);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) { // delta relates to the number of seconds since the render method was last called, usually a fractional number. therefore we can deduce the number of frames per second by taking its reciprocal.
		gameWorld.update(delta);
		client.updateServer(delta);
		gameRenderer.render(delta);
		
	}

	@Override
	public void resize(int width, int height) {
		OrthographicCamera c = gameRenderer.getCamera();
		c.viewportHeight = height;
		c.viewportWidth = width;
		c.update();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}

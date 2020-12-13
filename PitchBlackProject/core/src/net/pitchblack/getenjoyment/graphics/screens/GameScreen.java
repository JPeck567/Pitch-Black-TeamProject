package net.pitchblack.getenjoyment.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.helpers.InputHandler;
import net.pitchblack.getenjoyment.logic.GameRenderer;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class GameScreen implements Screen {
	
	private PitchBlackGraphics parent;
	
	private PBAssetManager pbManager;
	
	private GameWorld gameWorld;
	private GameRenderer gameRenderer;
	private InputHandler inputHandler;
	
	private Client client;
	
	public GameScreen(PitchBlackGraphics parent) {
		this.parent = parent;
		
		PBAssetManager pbManager = parent.pbAssetManager;
		pbManager.loadTextures();
		pbManager.loadMaps();
		

		Texture playerTexture = pbManager.getAsset(PBAssetManager.playerTexture);
		Texture fogTexture = pbManager.getAsset(PBAssetManager.fogTexture);
		
		// will be client game eventually for client
		gameWorld = new GameWorld(
				pbManager.getAsset(PBAssetManager.map0),
				playerTexture.getWidth(),
				playerTexture.getHeight(),
				pbManager
				);
		
		gameRenderer = new GameRenderer(gameWorld, pbManager, playerTexture);	
		inputHandler = new InputHandler(gameWorld);
		
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
		Viewport v = gameRenderer.getViewport();
		v.update(width, height, true);

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
		pbManager.dispose();
	}

}

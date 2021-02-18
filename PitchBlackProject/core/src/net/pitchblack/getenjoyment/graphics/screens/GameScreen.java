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
import net.pitchblack.getenjoyment.client.Client.RenderState;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.helpers.InputHandler;
import net.pitchblack.getenjoyment.helpers.MusicManager;
import net.pitchblack.getenjoyment.helpers.MusicManager.PitchBlackMusic;
import net.pitchblack.getenjoyment.logic.GameRenderer;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class GameScreen implements Screen {
	
	private PitchBlackGraphics parent;
	
	private PBAssetManager pbManager;
	
	private GameWorld gameWorld;
	private GameRenderer gameRenderer;
	private InputHandler inputHandler;
	
	private Client client;
	
	public GameScreen(PitchBlackGraphics parent, Client client) {
		this.parent = parent;
		
		PBAssetManager pbManager = parent.pbAssetManager;
		pbManager.loadTextures();
		pbManager.loadMaps();
		Texture playerTexture = pbManager.getAsset(PBAssetManager.playerTexture);
		
		this.client = client;
		client.beginConnection();
		//client.sendRegistration("email", "jorge", "password");
		//client.sendLogin("jorge", "password");
		
		gameRenderer = new GameRenderer(client, pbManager);	
		inputHandler = new InputHandler(gameRenderer);
		
		client.setRenderer(gameRenderer);
		//client.setState(RenderState.INITIATED);
		
		Gdx.input.setInputProcessor(inputHandler);
		
		MusicManager music = new MusicManager();
        MusicManager.getInstance().play(PitchBlackMusic.GAME);
        music.setVolume(PreferencesManager.getMusicVolume());
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(float delta) { // delta relates to the number of seconds since the render method was last called, usually a fractional number. therefore we can deduce the number of frames per second by taking its reciprocal.
		//gameWorld.update(delta);
		client.tick(delta);
		//gameRenderer.render(delta);	
	}

	@Override
	public void resize(int width, int height) {
		Viewport v = gameRenderer.getViewport();
		v.update(width, (int) v.getWorldHeight(), true);
		
		gameRenderer.getCamera().viewportHeight = v.getWorldHeight();
		gameRenderer.getCamera().viewportWidth = v.getWorldWidth();		

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

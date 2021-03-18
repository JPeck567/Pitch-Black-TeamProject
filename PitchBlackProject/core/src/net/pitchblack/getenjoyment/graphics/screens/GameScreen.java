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
import net.pitchblack.getenjoyment.client.Client.ClientState;
import net.pitchblack.getenjoyment.client.GameInstance;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics.Screens;
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
	private InputHandler inputHandler;
	private GameRenderer gameRenderer;
	public enum GameState {
		IDLE,
		PLAYING,
		WIN,
		LOSE
	}
	private GameState gameState;

	private Client client;
	
	public GameScreen(PitchBlackGraphics parent, Client client) {
		this.parent = parent;

		PBAssetManager pbManager = parent.pbAssetManager;
		inputHandler = new InputHandler(this);
		gameRenderer = new GameRenderer(this, client, pbManager);
		gameState = GameState.IDLE;

		this.client = client;

		Gdx.input.setInputProcessor(inputHandler);

		//MusicManager music = new MusicManager();
        MusicManager.getInstance().play(PitchBlackMusic.GAME);
		MusicManager.getInstance().setVolume(PreferencesManager.getMusicVolume());
	}
	
	public void setupRenderer(String playerData, String fogData, String mapData) {
		gameRenderer.setupData(playerData, fogData, mapData);
	}
	
	public void addGameData(String playerData, String fogData, String mapData) {
		gameRenderer.addGameData(playerData, fogData, mapData);
	}

	public void keyUp(int keyCode){
		if(gameState == GameState.PLAYING) {
			client.emitKeyUp(keyCode);
		}
	}

	public void keyDown(int keyCode){
		if(gameState == GameState.PLAYING) {
			client.emitKeyDown(keyCode);
		}
	}

	public void setGameState(GameState gameState){
		this.gameState = gameState;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(float delta) { // delta relates to the number of seconds since the render method was last called, usually a fractional number. therefore we can deduce the number of frames per second by taking its reciprocal.
		switch (gameState){
			case PLAYING:
				gameRenderer.render(delta);
				break;
			case WIN:
				gameState = GameState.WIN;
				client.setClientState(ClientState.IDLE);
				gameRenderer.resetRenderer();
				parent.changeScreen(Screens.WIN);
				break;
			case LOSE:
				gameState = GameState.LOSE;
				client.setClientState(ClientState.IDLE);
				gameRenderer.resetRenderer();
				parent.changeScreen(Screens.LOSE);
				break;
		}
	}

	@Override
	public void resize(int width, int height) {
		Viewport v = gameRenderer.getViewport();
		v.update(width, height, true);

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
		pbManager.manager.dispose();
	}

}

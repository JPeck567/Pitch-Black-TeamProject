package net.pitchblack.getenjoyment.frontend.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.pitchblack.getenjoyment.frontend.client.Client;
import net.pitchblack.getenjoyment.frontend.client.Client.ClientState;
import net.pitchblack.getenjoyment.frontend.game.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.game.GameRenderer;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.InputHandler;
import net.pitchblack.getenjoyment.frontend.helpers.MusicManager;
import net.pitchblack.getenjoyment.frontend.helpers.MusicManager.PitchBlackMusic;

public class GameScreen implements Screen {
	
	private PitchBlackGraphics parent;
	private PBAssetManager pbManager;
	private InputHandler inputHandler;
	private GameRenderer gameRenderer;
	private BitmapFont font;
	private SpriteBatch fontBatcher;

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
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		fontBatcher = new SpriteBatch();
		gameState = GameState.IDLE;

		this.client = client;

		Gdx.input.setInputProcessor(inputHandler);
		playMusic();
	}

	private void playMusic(){
		if(PreferencesManager.isMusicEnabled()){
			MusicManager.getInstance().play(PitchBlackMusic.GAME);
			MusicManager.getInstance().setVolume(PreferencesManager.getMusicVolume());
		}
	}

	public void restartGame(){ // setup game screen pre-game to set input to game screen + play sounds if needed
		Gdx.input.setInputProcessor(inputHandler);
		playMusic();
	}
	
	public void setupRenderer(String playerData, String fogData, String mapData) {
		gameRenderer.setupData(playerData, fogData, mapData);
	}
	
	public void addToGameDataBuffer(String playerData, String fogData, String mapData) {
		gameRenderer.addToGameDataBuffer(playerData, fogData, mapData);
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

	public void drawText(SpriteBatch batcher, String text, float x, float y){
		//fontBatcher.begin();
		font.draw(fontBatcher, text, x, y);
		//fontBatcher.end();
	}

	public void setGameState(GameState gameState){
		this.gameState = gameState;
	}

	// go from game state back to normal as would be from menu
	private void resetFromGameState(){
		gameState = GameState.IDLE;
		client.setClientState(ClientState.IDLE);
		client.resetCurrentRoom();
		gameRenderer.resetRenderer();
	}

	@Override
	public void show() {	}

	@Override
	public void render(float delta) { // delta relates to the number of seconds since the render method was last called, usually a fractional number. therefore we can deduce the number of frames per second by taking its reciprocal.
		switch (gameState){
			case PLAYING:
				gameRenderer.render(delta);
				break;
			case WIN:
				resetFromGameState();
				parent.changeScreen(PitchBlackGraphics.Screens.WIN);
				break;
			case LOSE:
				resetFromGameState();
				parent.changeScreen(PitchBlackGraphics.Screens.LOSE);
				break;
		}
	}

	@Override
	public void resize(int width, int height) {
		gameRenderer.resize(width, height);
	}

	@Override
	public void pause() {	}

	@Override
	public void resume() {	}

	@Override
	public void hide() {	}

	@Override
	public void dispose() {
		pbManager.manager.dispose();
	}
}

package net.pitchblack.getenjoyment.graphics;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import net.pitchblack.getenjoyment.client.ClientGame;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class PitchBlackGame {
	public final PBAssetManager pbManager = new PBAssetManager();
	private Boolean running;
	private GameWorld gameWorld;
	public ClientGame gameClient;
	
	public PitchBlackGame() {
		pbManager.loadTextures();
		pbManager.loadMaps();
		
		Texture playerTexture = pbManager.getAsset(PBAssetManager.playerTexture);
		
		running = true;
		gameWorld = new GameWorld(
				pbManager.getAsset(PBAssetManager.map0),
				playerTexture.getWidth(),
				playerTexture.getHeight(),
				pbManager
				);
		gameClient = new ClientGame(gameWorld);
		run();
	}

	private void run() {
		while(running) {
			gameClient.tick(Gdx.graphics.getDeltaTime());
		}
	}
}

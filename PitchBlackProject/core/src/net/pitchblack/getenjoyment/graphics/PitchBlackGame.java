package net.pitchblack.getenjoyment.graphics;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import net.pitchblack.getenjoyment.client.GameInstance;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.logic.GameWorld;

// no longer used
@Deprecated
public class PitchBlackGame {
	public final PBAssetManager pbManager = new PBAssetManager();
	private Boolean running;
	private GameWorld gameWorld;
	public GameInstance gameClient;
	
	public PitchBlackGame() {
		pbManager.loadTextures();
		pbManager.loadMaps();
		
		Texture playerTexture = pbManager.getAsset(PBAssetManager.playerTexture);
		
		running = true;
		gameWorld = new GameWorld(pbManager);
		gameClient = new GameInstance(null, null, null);
		//run();
	}

	public void run() {
		while(running) {
			gameClient.tick(Gdx.graphics.getDeltaTime());
		}
	}
}

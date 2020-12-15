package net.pitchblack.getenjoyment.graphics;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import net.pitchblack.getenjoyment.client.ClientGame;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.logic.GameWorld;

public class PitchBlackGame implements ApplicationListener {
	public final PBAssetManager pbManager = new PBAssetManager();
	public ClientGame gameClient;
	
	public PitchBlackGame() {
	}

	@Override
	public void create() {
		pbManager.loadTextures();
		pbManager.loadMaps();
		
		Texture playerTexture = pbManager.getAsset(PBAssetManager.playerTexture);

		// will be client game eventually for client
		GameWorld gameWorld = new GameWorld(
				pbManager.getAsset(PBAssetManager.map0),
				playerTexture.getWidth(),
				playerTexture.getHeight(),
				pbManager
				);
		
		gameClient = new ClientGame(gameWorld);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		gameClient.render(Gdx.graphics.getDeltaTime());
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
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}

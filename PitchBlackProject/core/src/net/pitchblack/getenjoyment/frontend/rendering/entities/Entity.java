package net.pitchblack.getenjoyment.frontend.rendering.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.rendering.screens.game.GameRenderer;

public class Entity extends Sprite {
	private final SpriteType spriteType;

    public enum SpriteType {
		FOG,
		PLAYER_ROGUE,
		PLAYER_CLOWN,
		PLAYER_ENGINEER,
		PLAYER_NINJA;
    }

	public Entity(SpriteType spriteType, PBAssetManager pbAssetManager) {
		super(getTextureFromType(spriteType, pbAssetManager));
		this.spriteType = spriteType;
		resizeSprite();
	}

    @Override
    public void draw(Batch batch) {
		super.draw(batch);
	}

    private static Texture getTextureFromType(SpriteType t, PBAssetManager pbAssetManager) {
		switch (t) {
			case PLAYER_ROGUE:
				return pbAssetManager.getAsset(PBAssetManager.roguePlayerTexture);
			case PLAYER_CLOWN:
				return pbAssetManager.getAsset(PBAssetManager.clownPlayerTexture);
			case PLAYER_NINJA:
				return pbAssetManager.getAsset(PBAssetManager.ninjaPlayerTexture);
			case PLAYER_ENGINEER:
				return pbAssetManager.getAsset(PBAssetManager.engineerPlayerTexture);
			case FOG:
				return pbAssetManager.getAsset(PBAssetManager.fogTexture);
			default:  // should use a placeholder if texture not found
				return pbAssetManager.getAsset(PBAssetManager.roguePlayerTexture);
		}
	}

	private void resizeSprite() {
		setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
//		switch (spriteType) {
//			case PLAYER_ROGUE:
//				setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
//			case FOG:
//				setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
//				return;
//			default:
//				setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
//		}
	}

	public SpriteType getSpriteType() {
		return spriteType;
	}
}

package net.pitchblack.getenjoyment.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.logic.GameRenderer;

public class Entity extends Sprite {
	private final Type type;
	private final String id;
	private EntityState entityState;  // either ascending, descending or dead
	private boolean movementLeft;
	private boolean movementRight;
	
	public enum Type {
		PLAYER,
		FOG
	}
	
	public enum EntityState {
		  ASCENDING,
		  DESCENDING,
		  STANDING,
		  LEFT,
		  RIGHT,
		  DEAD
		}
	
	public Entity(String id, Type type, EntityState moveState, PBAssetManager pbAssetManager) {
		super(getTextureFromType(type, pbAssetManager));
		this.id = id;
		this.type = type;
		movementLeft = false;
		movementRight = false;
		resizeSprite();
	}

	private static Texture getTextureFromType(Type t, PBAssetManager pbAssetManager) {
		switch (t) {
			case PLAYER:
				return pbAssetManager.getAsset(PBAssetManager.playerTexture);
			case FOG:
				return pbAssetManager.getAsset(PBAssetManager.fogTexture);
			default:  // should use a placeholder if texture not found
				return pbAssetManager.getAsset(PBAssetManager.playerTexture);
		}
	}
	
	private void resizeSprite() {
		switch (type) {
			case PLAYER:
				setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
			case FOG:
				setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
			default:
				setBounds(0, 0, getTexture().getWidth() / GameRenderer.PPM, getTexture().getHeight() / GameRenderer.PPM);
		}
	}

	public void setCoordinates(float x, float y) {
		setPosition(x, y);
	}
	
	public void setMovement(boolean left, boolean right) {
		movementLeft = left;
		movementRight = right;
	}
	
	public void setState(EntityState entityState) {
		this.entityState = entityState;
	}
		
	public Type getType() {
		return type;
	}

	public EntityState getState() {
		return entityState;
	}


}

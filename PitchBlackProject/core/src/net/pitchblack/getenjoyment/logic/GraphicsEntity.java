package net.pitchblack.getenjoyment.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GraphicsEntity extends Sprite  {
	private Type type;
	
	enum Type {
		PLAYER,
		FOG
	}
	
	public GraphicsEntity(Texture texture, Type type, float startX, float startY) {
		super(texture);
		setPosition(startX, startY);
		this.type = type;
	}
	
	@Override
	public float getX() {
		return getX();
	}

	@Override
	public float getY() {
		return getY();
	}

}

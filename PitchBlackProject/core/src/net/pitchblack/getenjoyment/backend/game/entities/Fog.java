package net.pitchblack.getenjoyment.backend.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.pitchblack.getenjoyment.backend.game.logic.GameWorld;

public class Fog {

	private Vector2 position;
	private static final float SPEED = Player.SPEED * 5.5f;
	private Texture image;
	//private EntityType type;
	private Body body;
	private float width, height;
	
	private boolean allPlayersEliminated = false;
	private int playersLeft = 4;

	public Fog(Body body, float width, float height) {
		this.body = body;
		this.body.setLinearVelocity(SPEED, 0);
		this.body.setTransform(body.getPosition().x - 30, body.getPosition().y, 0);  // starting pos
		body.setSleepingAllowed(false);
		//this.position = new Vector2(x, y);
		//image = new Texture("fog.png");	
		this.width = width;
		this.height = height;		
	}

	// b2d coords based in center, so move x + y to point to top left
	public float getScreenX() {
		return body.getPosition().x * GameWorld.PPM - (width / 2);// + (3 / GameWorld.PPM);
	}
	
	public float getScreenY() {
		return body.getPosition().y * GameWorld.PPM - (height / 2);// - (3 / GameWorld.PPM);
	}
	
	@Override
	public String toString() {
		return getScreenX() / GameWorld.PPM + "," + getScreenY() / GameWorld.PPM;
	}
	
}

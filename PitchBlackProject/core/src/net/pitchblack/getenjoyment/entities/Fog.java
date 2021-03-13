package net.pitchblack.getenjoyment.entities;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;  
import com.badlogic.gdx.physics.box2d.Body;

import net.pitchblack.getenjoyment.logic.GameWorld;

public class Fog {

	private Vector2 position;
	private static final float SPEED = Player.SPEED * 3.5f;
	private Texture image;
	//private EntityType type;
	private Body body;
	private float width, height;
	
	private boolean allPlayersEliminated = false;
	private int playersLeft = 4;
	
	
	public Fog(Body body, float width, float height) {
		this.body = body;
		this.body.setLinearVelocity(SPEED, 0);
		body.setSleepingAllowed(false);
		//this.position = new Vector2(x, y);
		//image = new Texture("fog.png");	
		this.width = width;
		this.height = height;		
	}

	public float getX() {
		return body.getPosition().x - (width / 2 / GameWorld.PPM);// + (3 / GameWorld.PPM);
	}
	
	public float getY() {
		return body.getPosition().y - (height / 2 / GameWorld.PPM);// - (3 / GameWorld.PPM);
	}
	
	@Override
	public String toString() {
		return getX() + "," + getY();
	}
	
}

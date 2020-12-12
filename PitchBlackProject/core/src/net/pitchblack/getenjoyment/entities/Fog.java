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
	private static final float SPEED = Player.SPEED * 2.7f;
	private Texture image;
	//private EntityType type;
	private Body body;
	private float width, height;
	
	private boolean allPlayersEliminated = false;
	private int playersLeft = 4;
	
	
	
	public Fog(Body body, float width, float height) {
		this.body = body;
		this.body.setLinearVelocity(SPEED, 0);
		
		//this.position = new Vector2(x, y);
		//image = new Texture("fog.png");	
		this.width = width;
		this.height = height;		
	}
	
	
	public void update(float deltaTime, int playersLeft) {		
		
//		//only runs if players are left
//		if(playersLeft > 0) {
//			//runs a timer every 50 milliseconds
//			//which then uses the moveX method to move the fog along
//			Timer slowdown = new Timer();
//			slowdown.schedule(new TimerTask(){
//			    @Override
//			    public void run() {
//			       //0.8 can be changed to a higher or lower value to change the speed of the fog
//			       moveX(speed * 0.8f);
//			    }
//			}, 0, 50);	
//			
//		}		
	}
	
	
	public void render(SpriteBatch batch) {
		batch.draw(image, position.x, position.y, width, height);
	}
	
	
	public void moveX(float amount) {
		this.position.x += amount;
	}
	
	public float getX() {
		return body.getPosition().x - (width / 2 / GameWorld.PPM);// + (3 / GameWorld.PPM);
	}
	
	public float getY() {
		return body.getPosition().y - (height / 2 / GameWorld.PPM);// - (3 / GameWorld.PPM);
	}	
}

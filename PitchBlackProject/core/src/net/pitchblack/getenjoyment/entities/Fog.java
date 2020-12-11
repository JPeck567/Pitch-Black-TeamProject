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
import com.badlogic.gdx.math.Vector2;  // powerful! is a 2d vector
import com.badlogic.gdx.physics.box2d.Body;


public class Fog {

	private Vector2 position;
	private int speed = 1;
	private Texture image;
	private EntityType type;
	
	private boolean allPlayersEliminated = false;
	private int playersLeft = 4;
	
	public enum EntityType {
		
		//width and height need to be changed to fit the map height and the width appropriate for the map width
		FOG("fog", 192, 320);
		
		private String id;
		private int width, height;
		
		private EntityType(String id, int width, int height) {
			this.id = id;
			this.width = width;
			this.height = height;
		}

		public String getId() {
			return id;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}			
	}
	
	public Fog(float x, float y) {
		
		this.position = new Vector2(x, y);
		type = EntityType.FOG;
		image = new Texture("fog.png");		
	}
	
	
	public void update(float deltaTime, float gravity) {		
		// -------needs to be changed to when the game starts ---------------
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			
			if (!allPlayersEliminated) {
				//runs a timer every 50 milliseconds
				//which then uses the moveX method to move the fog along
				Timer slowdown = new Timer();
				slowdown.schedule(new TimerTask(){
				    @Override
				    public void run() {
				       //0.8 can be changed to a higher or lower value to change the speed of the fog
				       moveX(speed * 0.8f);
				    }
				}, 0, 50);	
				
				
			}			
		}	
	}
	
	
	public void render(SpriteBatch batch) {
		batch.draw(image, position.x, position.y, type.getWidth(), type.getHeight());
	}
	
	
	public void moveX(float amount) {
		this.position.x += amount;
	}
	
	
	
	
	
	
	
}

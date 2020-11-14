package hack.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;  // powerful! is a 2d vector

import hack.entities.Player.State;
import hack.helpers.AssetLoader;
import hack.logic.GameRenderer;
import hack.logic.GameWorld;

public class Player extends Sprite {
	public static final float SPEED = 100 * 2;
	public static final float FALLING_SPEED = 100 * 2;
	public static final float GRAVITY = 60 * 1.8f;
	
	public static final int JUMP_SPEED = 22*20;
	public static final double JUMP_LIMIT = 200*20;
	
	//private Vector2 position;
	private Vector2 velocity;
	
	private Rectangle boundRect;
	private int jumpHeight;
	
	private State state;
	
	private TiledMapTileLayer collisionLayer;
	
	enum State {
		  ASCENDING,
		  DESCENDING,
		  STANDING
		}
	
	public Player(TiledMapTileLayer collisionLayer) {
		super(AssetLoader.player);
		//position = new Vector2(x, y);
		velocity = new Vector2(0, 0);
		
		boundRect = this.getBoundingRectangle();
		this.jumpHeight = 0;
		state = State.DESCENDING;
		
		this.collisionLayer = collisionLayer;
	}

	public void update(float delta) {
		float oldY = getY();
		float oldX = getX();
		//System.out.println(position.x + " " + position.y + " " + jumpHeight);
		// uses delta to work out pos to achieve frame-rate independent movement. if execution time takes x2 normal speed to execute, we will compensate
		// by moving at 2x the normal distance. makes it super slow which not sure how to go about
		velocity.y -= GRAVITY * delta;
		
		if(velocity.y > FALLING_SPEED) {
			velocity.y = FALLING_SPEED;
		} else if(velocity.y < FALLING_SPEED) {
			velocity.y = -FALLING_SPEED;
		}	

		if(state == State.ASCENDING) {  // meaning to jump up
			jumpHeight += JUMP_SPEED;
			if(jumpHeight >= JUMP_LIMIT) {
				state = State.DESCENDING;
				jumpHeight = 0;
			} else {
				velocity.y += JUMP_SPEED;
			}
		}
		
		// - for y as y is facing down when rendered
		setPosition(getX() + velocity.x * delta, getY() - velocity.y * delta);

		if(velocity.y < 0) {  // just went up
			if(collidesTop()) {
				setY(oldY);
				state = State.STANDING;
			}
			
		} else if (velocity.y > 0) { // just went down
			if(collidesBottom()) {
				setY(oldY);
			}
		}
		
		if(velocity.x > 0) {  // just went right
			if(collidesRight()) {
				setX(oldX);
			}
		} else if (velocity.x < 0) { // just went left
			if(collidesLeft()) {
				setX(oldX);
			}
		}
	}
	
	private boolean isCellBlocked(float x, float y) {
		Cell cell = collisionLayer.getCell((int) (x / GameWorld.TILE_DIM), (int) (y / GameWorld.TILE_DIM));
		return cell != null ? true : false;
	}
	
	public boolean collidesRight() {
		boolean collides = false;
	
		for(float step = 0; step < getHeight(); step += GameWorld.TILE_DIM / 2)
			if(collides = isCellBlocked(getX() + getWidth(), getY() + step))
				break;
	
		return collides;
	}
	
	public boolean collidesLeft() {
		boolean collides = false;
	
		for(float step = 0; step < getHeight(); step += GameWorld.TILE_DIM / 2)
			if(collides = isCellBlocked(getX(), getY() + step))
				break;
	
		return collides;
	}
	
	public boolean collidesTop() {
		boolean collides = false;
	
		for(float step = 0; step < getWidth(); step += GameWorld.TILE_DIM / 2)
			if(collides = isCellBlocked(getX() + step, getY() + getHeight()))
				break;
	
		return collides;
	
	}
	
	public boolean collidesBottom() {
		boolean collides = false;
	
		for(float step = 0; step < getWidth(); step += GameWorld.TILE_DIM / 2)
			if(collides = isCellBlocked(getX() + step, getY()))
				break;
	
		return collides;
	
	}
	
	public void keyDown(int keycode) {
		switch(keycode) {
			case Keys.A:
				System.out.println("A Down");
				velocity.x += -SPEED;
				break;
			case Keys.D:
				System.out.println("D Down");
				velocity.x += SPEED;
				break;
			case Keys.SPACE:
				if(state == State.STANDING) {  // so cannot keep jumping
					state = State.ASCENDING;
					velocity.y -= JUMP_SPEED;
				}
				System.out.println("Space Pressed");
				break;
		}
	}
	
	public void keyUp(int keycode) {
		switch(keycode) {
			case Keys.A:
				System.out.println("A Up");
				velocity.x += SPEED;
				break;
			case Keys.D:
				System.out.println("D Up");
				velocity.x += -SPEED;
				break;
		}
	}
	
	public boolean hasMoved() {
		return !velocity.isZero();
	}
}

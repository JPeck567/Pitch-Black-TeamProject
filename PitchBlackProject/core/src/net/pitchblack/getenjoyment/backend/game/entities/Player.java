package net.pitchblack.getenjoyment.backend.game.entities;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.pitchblack.getenjoyment.backend.game.logic.GameWorld;

public class Player {
	public static final float SPEED = 1.5f;
	public static final Vector2 SPEED_VECTOR = new Vector2(SPEED, 0);
	public static final Vector2 PUSH_SPEED_VECTOR = new Vector2(SPEED / 20, 0);
	public static final float TERMINAL_VELOCITY = 12f;
	public static final float JUMP_FORCE = 9f;
	private static final int JUMP_LIMIT = 2; // 2
	
	private Vector2 position;
	private Vector2 velocity;
	private Body body;
	private final String id;
	private final int playerNo;

	private float height;
	private float width;
	private Rectangle boundRect;
	
	private State state;
	private boolean pushState;
	private int jumps;  // number of jumps taken; it is capped at JUMP_LIMIT
	private boolean movementLeft;
	private boolean movementRight;
	private boolean jumped;
	
	private float time;
	
	private TiledMapTileLayer collisionLayer; // in server, will update so often if player moves to different map 
	
	public enum State {
		  ASCENDING,
		  DESCENDING,
		  STANDING,
		  LEFT,
		  RIGHT,
		  DEAD
		}
	
	public Player(String id, int playerNo, Body body, float height, float width) {
		this.id = id;
		this.playerNo = playerNo;
		this.height = height;
		this.width = width;
		
		position = new Vector2(GameWorld.START_POS_X, GameWorld.START_POS_Y);
		velocity = new Vector2(0, 0);
		this.body = body;
		body.setFixedRotation(true);
		body.setSleepingAllowed(false);
		
		state = State.STANDING;
		pushState = false;
		movementLeft = false;
		movementRight = false;
		jumped = false;
		
		time = 0;
	}

	public void update(float delta) {
		float oldY = position.y;
		float oldX = position.x;
		
		if(state != State.DEAD) {
			if(body.getLinearVelocity().y < 0 && jumps != 0) {
				state = State.DESCENDING;
			}
			
			//System.out.println(pushState);
			//System.out.print(" " + state);
			//System.out.println(body.getPosition().toString());
			
			if(movementLeft && body.getLinearVelocity().x > -TERMINAL_VELOCITY) {
				// if pushing, divide by 2
				body.applyLinearImpulse(pushState ? PUSH_SPEED_VECTOR.scl(-1) : SPEED_VECTOR.cpy().scl(-1), body.getWorldCenter(), true);
			}
			
			if(movementRight && body.getLinearVelocity().x < TERMINAL_VELOCITY) {
				body.applyLinearImpulse(pushState ? PUSH_SPEED_VECTOR : SPEED_VECTOR, body.getWorldCenter(), true);
			}
		}
	}

	
	public void keyDown(int keycode) {
		switch(keycode) {
			case Keys.A:
				//System.out.println("A Down");
				movementLeft = true;
				//velocity.x += -SPEED;
				//Vector2 velA = body.getLinearVelocity();
				//velA.x = -SPEED;
				//body.setLinearVelocity(velA);
				//body.setLinearVelocity(body.getLinearVelocity().y - SPEED, body.getLinearVelocity().y);
				break;
			case Keys.D:
				//System.out.println("D Down");
				movementRight= true;
				//velocity.x += SPEED;
				//Vector2 velD = body.getLinearVelocity();
				//velD.x = SPEED;
				//body.setLinearVelocity(velD);
				//body.applyForceToCenter(new Vector2(0, SPEED), true);
				//body.applyLinearImpulse(new Vector2(SPEED, 0), body.getWorldCenter(), true);
				break;
			case Keys.SPACE:
				//System.out.println("Space Down");
// 				if(state == State.STANDING) {  // so cannot keep jumping
//					velocity.y += JUMP_VELOCITY;
//					state = State.ASCENDING;
//				}
				//body.setLinearVelocity(body.getLinearVelocity().x, JUMP_FORCE);
				//body.setLinearVelocity(0, JUMP_FORCE);
//	            
//				break;
//				
				if(jumps < JUMP_LIMIT) {
					Vector2 vel2 = body.getLinearVelocity();
					Vector2 pos = body.getPosition();
					
					//body.applyForce(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
					//body.applyForceToCenter(0, 35f, true);
					body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
					state = State.ASCENDING;
					jumps++;
				}
				break;	
		}
	}
	
	public void keyUp(int keycode) {
		switch(keycode) {
			case Keys.A:  // same as d
				//System.out.println("A Up");
				Vector2 vel1 = body.getLinearVelocity();
				vel1.x = 0;
				body.setLinearVelocity(vel1);
				movementLeft = false;
				break;
//				Vector2 velA = body.getLinearVelocity();
//				//velocity.x += SPEED;
//				body.setLinearVelocity(0, body.getLinearVelocity().y);
//				break;
			case Keys.D:
				//System.out.println("D Up");
				//velocity.x += -SPEED;
				Vector2 vel2 = body.getLinearVelocity();
				vel2.x = 0;
				body.setLinearVelocity(vel2);
				movementRight = false;
				break;
		}
	}

	public boolean hasMoved() {
		return !movementRight || !movementLeft;
	}

	// screen coords
	// b2d pos is center, so put x to left side. (x,y) will point to top left
	public float getScreenX() {
		return (body.getPosition().x * GameWorld.PPM) - (width / 2);// + (3 / GameWorld.PPM);
	}

	// put y to top
	public float getScreenY() {
		return (body.getPosition().y * GameWorld.PPM) - (height / 2);// - (3 / GameWorld.PPM);
	}
	
	public Vector2 getVelocity(){
		return body.getLinearVelocity().cpy();
	}

	public void setState(State state) {
		if(state == State.STANDING) {
			jumps = 0;
		}
		this.state = state;
	}
	
	public void setPushState(Boolean flag){
		pushState = flag;
	}

	public void removePushVelocity(float x) {
		Vector2 vel = body.getLinearVelocity();
		vel.x -= x;
		body.setLinearVelocity(vel);
	}

	public Body getBody() {
		return body;
	}
	
	public String getID() {
		return id;
	}

	public void kill() {
		state = State.DEAD;
	}

	// converts coords to world coords
	@Override
	public String toString() {
		return id + "," + getScreenX() / GameWorld.PPM + "," + getScreenY() / GameWorld.PPM + "," +
			   state + "," + movementLeft + "," + movementRight + "," + playerNo;
	}
}
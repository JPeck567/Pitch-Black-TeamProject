package net.pitchblack.getenjoyment.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import net.pitchblack.getenjoyment.logic.GameWorld;

public class BodyFactory {  // use to be singleton, but when shared between game instances, world field needs to be different
	private World world;
	
	private BodyFactory(World world) {
		this.world = world;
	}

	public Body createBody(float width, float height, float posX, float posY, BodyDef.BodyType bodyType, String userData) {
		// create body for, which defines object type and position
		BodyDef bodyDef = new BodyDef();
		
		bodyDef.type = bodyType;
		bodyDef.position.set(posX / GameWorld.PPM, posY / GameWorld.PPM);
		bodyDef.linearDamping = 1f;
		Body body = world.createBody(bodyDef);  // adds body to world sim
		
		// define dimensions of the body using a fixture
		PolygonShape shape = new PolygonShape();
	    shape.setAsBox(width / 2 / GameWorld.PPM , height / 2 / GameWorld.PPM);
	    
	    // creation of fixture
	    FixtureDef fixtureDef = new FixtureDef();
	    
	    fixtureDef.shape = shape;
	    fixtureDef.density = 1.1f;
	    fixtureDef.friction = 0f;
	    body.createFixture(fixtureDef);  // sets fixture
	    
//	    FixtureDef bottomSensor = new FixtureDef();
//	    PolygonShape bottomSensorShape = new PolygonShape();
//	    bottomSensorShape.setAsBox(hx, hy);
//	    bottomSensor.isSensor = true;
//	    body.createFixture()
	    
	    shape.dispose();
	    
	    body.setUserData(userData);
	    return body;
	}
	
	public static BodyFactory getInstance(World world){
		return new BodyFactory(world);
	}
}

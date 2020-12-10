package net.pitchblack.getenjoyment.logic;

import java.util.List;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import net.pitchblack.getenjoyment.entities.Entity;
import net.pitchblack.getenjoyment.helpers.MapBodyFactory;

public class CollisionHandler {
	private GameWorld gameWorld;
	
	public CollisionHandler(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	public boolean isCollisionWithMap(Body body, World world) {
		//MapObjects collisionObjectLayer = gameWorld.getMap().getLayers().get("collisionObjLayer").getObjects();
		List<Body> collisionBodies = MapBodyFactory.getCollisionBodies(gameWorld.getMap(), world);

		
		return false;
		
	}
}

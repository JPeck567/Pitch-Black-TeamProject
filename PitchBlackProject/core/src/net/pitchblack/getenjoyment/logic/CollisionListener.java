package net.pitchblack.getenjoyment.logic;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.pitchblack.getenjoyment.entities.Player;

public class CollisionListener implements ContactListener {
	private GameWorld gameWorld;

	public CollisionListener(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();

		String bodyAData = bodyA.getUserData().toString();
		String bodyBData = bodyB.getUserData().toString();

		// should use collision filtering for way better collison checking
		// (https://www.iforce2d.net/b2dtut/collision-filtering)

		if (bodyAData.startsWith(GameWorld.PLAYER_USER_DATA) && bodyBData.equals(GameWorld.MAP_USER_DATA)) { // player to map
			// System.out.println("map collision");
			String id = bodyAData.split(",")[1];
			gameWorld.getPlayer(id).setState(Player.State.STANDING);
		} else if (bodyBData.startsWith(GameWorld.PLAYER_USER_DATA)
				&& bodyA.getUserData().equals(GameWorld.MAP_USER_DATA)) { // map to player
			// System.out.println("map collision");
			String id = bodyBData.split(",")[1];
			gameWorld.getPlayer(id).setState(Player.State.STANDING);
		} else if ((bodyA.getUserData().toString().startsWith(GameWorld.PLAYER_USER_DATA) 
				&& bodyB.getUserData().toString().startsWith(GameWorld.PLAYER_USER_DATA))) { // player to player
			// System.out.println("Contact!");
			String id = bodyAData.split(",")[1];
			Player p = gameWorld.getPlayer(id);
			p.setPushState(true);
		} else if (bodyAData.startsWith(GameWorld.FOG_USER_DATA) && bodyBData.equals(GameWorld.PLAYER_USER_DATA)) { // fog to player
			System.out.println("Fog Collison");
			String id = bodyBData.split(",")[1];
			gameWorld.addToKillList(id);
		} else if (bodyAData.startsWith(GameWorld.PLAYER_USER_DATA) && bodyBData.equals(GameWorld.FOG_USER_DATA)) { // player to fog
			System.out.println("Fog Collison");
			String id = bodyAData.split(",")[1];
			gameWorld.addToKillList(id);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();

		String bodyAData = bodyA.getUserData().toString();
		String bodyBData = bodyB.getUserData().toString();

		// player to player
		if ((bodyAData.startsWith(GameWorld.PLAYER_USER_DATA) && bodyBData.startsWith(GameWorld.PLAYER_USER_DATA))) {
			System.out.println("No Contact!");
			Player p = gameWorld.getPlayer(bodyAData.split(",")[1]);
			p.setPushState(false);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {	}
}
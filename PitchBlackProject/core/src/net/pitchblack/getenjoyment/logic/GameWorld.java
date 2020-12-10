package net.pitchblack.getenjoyment.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import net.pitchblack.getenjoyment.entities.BodyFactory;
import net.pitchblack.getenjoyment.entities.Entity;
import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.helpers.MapBodyFactory;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

public class GameWorld {
	
	public static final float PPM = 32; // pixels per meter
	public static final float START_POS_X = 16f;
	public static final float START_POS_Y = (PPM * 5f);
	public static final int SPEED_MODIFIER = 0;
	public static final Vector2 GRAVITY_VECT = new Vector2(0, -9.81f);  // so downwards at 9.81px per second
	public static int TILE_DIM;
	public static final String PLAYER_USER_DATA = "player";
	public static final String MAP_USER_DATA = "mapCollision";
	public static final String FOG_USER_DATA = "fog";
	
	
	private World physWorld;
	private BodyFactory bodyFactory;
	private ArrayList<Body> mapCollisionBodies;
	
	private TiledMap map;
	private HashMap<Vector2, Body> mapBody;
	private float playerWidth, playerHeight;
	private Player player;
	private HashMap<String, Player> players;
	private HashMap<String, Vector2> otherPlayers;  // will be all players in server, so <String, Player>
	private int playerCount;
	private ArrayList<Entity> entities;
	
	private CollisionHandler collisionHandler;
	
	public GameWorld(TiledMap map, int playerWidth, int playerHeight) {
		this.playerWidth = playerWidth;
		this.playerHeight = playerHeight;
		
		physWorld = new World(GRAVITY_VECT, true);  // last param tells world to not simulate inactive bodies (ie two equal forces against each other)
		physWorld.setContactListener(new CollisionListener(this));
		
		bodyFactory = BodyFactory.getInstance(physWorld);
		
		mapCollisionBodies = MapBodyFactory.getCollisionBodies(map, physWorld);
		this.map = map;
		//TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("collision");
		//TILE_DIM = ((TiledMapTileLayer) map.getLayers().get("collision")).getTileHeight();
		//collisionLayer.get
		
		
		players = new HashMap<String, Player>();
		playerCount = 0;
		// will be in loop for 4 players
		// player body
		player = createPlayer();
		Player player2 = createPlayer();
		
		otherPlayers = new HashMap<String, Vector2>();
	}
	
	public void update(float delta) {
		physWorld.step(delta, 6, 2);
		
		// in server, updates all players
		player.update(delta);
		
	}
	
	public Player createPlayer() {
		Body playerBody = bodyFactory.createBody(playerWidth, playerHeight, START_POS_X, START_POS_Y, BodyDef.BodyType.DynamicBody, PLAYER_USER_DATA + playerCount);
	    playerBody.setLinearVelocity(0, 0);
	    Player p = new Player(playerBody, playerWidth / PPM, playerHeight / PPM);
	    players.put(String.valueOf(playerCount), p);
	    playerCount++;
	    return p;
	}
	
	public void addPlayer(String id, Object o) {
		otherPlayers.put(id, null);
	}
	
	public boolean isPlayerNull(String id) {
		return otherPlayers.get(id) == null;
	}
	
	public void removePlayer(String id) {
		otherPlayers.remove(id);
	}
	
	public void movePlayer(String id, float x, float y) {  // acts by just adding a new vector, so can simulate addition of new players
		otherPlayers.put(id, new Vector2(x, y));
	}
	
	public Player getPlayer(String id){
		return players.get(id);
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Vector2> getOtherPlayers(){
		return (Map<String, Vector2>) otherPlayers.clone();
	}

	public void keyUp(int keycode) {
		player.keyUp(keycode);
	}
	
	public void keyDown(int keycode) {  // will have param for player id
		player.keyDown(keycode);
	}

	public World getWorld() {
		return physWorld;
	}
}
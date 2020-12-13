package net.pitchblack.getenjoyment.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
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
import net.pitchblack.getenjoyment.entities.Fog;
import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.helpers.MapBodyFactory;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

public class GameWorld {
	
	public static final float PPM = 32; // pixels per meter

	public static final float START_POS_X = 1f * PPM;
	public static final float START_POS_Y = 16f * PPM;

	public static final int SPEED_MODIFIER = 0;
	public static final Vector2 GRAVITY_VECT = new Vector2(0, -9.81f);  // so downwards at 9.81px per second
	public static int TILE_DIM;
	public static final String PLAYER_USER_DATA = "player";
	public static final String MAP_USER_DATA = "mapCollision";
	public static final String FOG_USER_DATA = "fog";
	public static final int INITIAL_NUMBER_OF_MAPS = 3;
	public final float MAP_WIDTH_PX;
	public final float MAP_HEIGHT_PX;
	
	private World physWorld;
	private BodyFactory bodyFactory;
	
	private TiledMap map;
	private HashMap<Integer, TiledMap> mapsMap;  // stores all the loaded maps
	private HashMap<Integer, ArrayList<Body>> mapsCollisionBodiesMap;  // stores bodies for each map
	private ArrayList<Integer> gameMapSequence;  // the maps selection for the game initially
	private Random random;
	
	private float playerWidth, playerHeight;
	private Player player;
	private Fog fog;
	private HashMap<String, Player> players;
	private HashMap<String, Vector2> otherPlayers;  // will be all players in server, so <String, Player>
	private int playerCount;
	private ArrayList<Entity> entities;
	private float fogWidth, fogHeight;
	
	private CollisionHandler collisionHandler;

	public GameWorld(TiledMap map, int playerWidth, int playerHeight, PBAssetManager pbAssetManager) {
		MapProperties prop = map.getProperties();
		MAP_WIDTH_PX = ((float) prop.get("width", Integer.class)) * PPM;
		MAP_HEIGHT_PX = ((float) prop.get("height", Integer.class)) * PPM;

		this.playerWidth = playerWidth;
		this.playerHeight = playerHeight;
		
		this.fogWidth = 192;
		this.fogHeight = map.getProperties().get("height", Integer.class) * PPM;
				
		physWorld = new World(GRAVITY_VECT, true);  // last param tells world to not simulate inactive bodies (ie two equal forces against each other)
		physWorld.setContactListener(new CollisionListener(this));
		
		bodyFactory = BodyFactory.getInstance(physWorld);
		
		mapsMap = pbAssetManager.getMaps();
		
		gameMapSequence = new ArrayList<Integer>();
		gameMapSequence.add(0); // starting map
		
		random = new Random();
		
		mapsCollisionBodiesMap = new HashMap<Integer, ArrayList<Body>>();
		mapSetup();
		//mapCollisionBodies = MapBodyFactory.getCollisionBodies(map, physWorld, 1);
		//this.map = map;
			
		players = new HashMap<String, Player>();
		playerCount = 0;
		
		// will be in loop for 4 players
		// player body + class
		player = createPlayer();
		Player player2 = createPlayer();
		
		fog = createFog();
		
		
		otherPlayers = new HashMap<String, Vector2>();
	}
	
	private void mapSetup() {
		// will be random
		int numberOfMaps = mapsMap.size();
		for(int i = 0; i < INITIAL_NUMBER_OF_MAPS; i++) {
			gameMapSequence.add(getRandomMapNum()); 
		}
		
		for(int i = 0; i < gameMapSequence.size() ; i++ ) {  // cycles through the map sequence. i is the seq number of the map
			appendMap(gameMapSequence.get(i), i + 1); // +1 as i starts at 0
			//TiledMap currentMap = mapsMap.get(mapNumber);  // gets tiled map from map number in i'th position in sequence
			//mapsCollisionBodiesMap.put(mapNumber, MapBodyFactory.getCollisionBodies(currentMap, physWorld, i + 1));
		}
		
	}

	public void update(float delta) {
		physWorld.step(delta, 6, 2);
		
		// in server, updates all players
		player.update(delta);
		fog.update(delta, playerCount);

		
		if(player.getX() > gameMapSequence.size() * MAP_WIDTH_PX) {
			appendMap(0, gameMapSequence.size() + 1);
		}
		
		// also delete bodies before the fog
		if(player.getY() < 0) {
			player.kill();
			physWorld.destroyBody(player.getBody());
			playerCount--;
		}

	}
	
	public Player createPlayer() {
		Body playerBody = bodyFactory.createBody(playerWidth, playerHeight, START_POS_X, START_POS_Y, BodyDef.BodyType.DynamicBody, PLAYER_USER_DATA + playerCount);
	    playerBody.setLinearVelocity(0, 0);
	    Player p = new Player(playerBody, playerWidth / PPM, playerHeight / PPM);
	    players.put(String.valueOf(playerCount), p);
	    playerCount++;
	    return p;
	}

	public Fog createFog() {
		Body fogBody = bodyFactory.createBody(fogWidth, fogHeight, (fogWidth * -5), fogHeight / 2, BodyDef.BodyType.KinematicBody, FOG_USER_DATA);
		fogBody.setLinearVelocity(0, 0); //  - (fogWidth * .75f ) -(fogWidth * 2f)
		Fog f = new Fog(fogBody, fogWidth, fogHeight);
		return f;
	}

	private void appendMap(int mapNumber, int position) {  // position starts from 1
		TiledMap currentMap = mapsMap.get(mapNumber);  // gets tiled map from map number in i'th position in sequence
		mapsCollisionBodiesMap.put(mapNumber, MapBodyFactory.getCollisionBodies(currentMap, physWorld, position));
	}
	
	public int getRandomMapNum() {
		int numberOfMaps = mapsMap.size();
		int mapNo = random.nextInt(numberOfMaps);
		
		while(mapNo == 0) {
			mapNo = random.nextInt(numberOfMaps);
		}
		
		return mapNo;

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


	public Fog getFog() {
		return fog;
	}

	
	public ArrayList<Integer> getMapSequence(){
		return gameMapSequence;

	}
}
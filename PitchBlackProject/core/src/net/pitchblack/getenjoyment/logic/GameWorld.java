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
import net.pitchblack.getenjoyment.entities.Player.State;
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
	public enum GameState{
		WAITING,
		READY,
		PLAYING,
		FINISH
	}
	
	private GameState gameState;
	
	private World physWorld;
	private BodyFactory bodyFactory;
	
	private TiledMap map;
	private HashMap<Integer, TiledMap> mapsMap;  // stores all the loaded maps
	private HashMap<Integer, ArrayList<Body>> mapsCollisionBodiesMap;  // stores bodies for each map
	private ArrayList<Integer> gameMapSequence;  // the maps selection for the game initially
	private Random random;
	
	//private Player player;
	private HashMap<Integer, Player> players;
	//private HashMap<String, Vector2> otherPlayers;  // will be all players in server, so <String, Player>
	private ArrayList<Integer> alivePlayers;
	private ArrayList<Integer> deadPlayers;
	private ArrayList<Integer> toRemove;
	private int playerCount;
	
	private Fog fog;
	private float playerWidth, playerHeight;
	private float fogWidth, fogHeight;
	
	private CollisionHandler collisionHandler;

	public GameWorld(TiledMap map, int playerWidth, int playerHeight, PBAssetManager pbAssetManager) {
		gameState = GameState.WAITING;
		
		MapProperties prop = map.getProperties();
		MAP_WIDTH_PX = ((float) prop.get("width", Integer.class)) * PPM;
		MAP_HEIGHT_PX = ((float) prop.get("height", Integer.class)) * PPM;

		this.playerWidth = playerWidth;
		this.playerHeight = playerHeight;
		
		this.fogWidth = 192;
		this.fogHeight = MAP_HEIGHT_PX;
		
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
		
		players = new HashMap<Integer, Player>();
		alivePlayers = new ArrayList<Integer>();
		deadPlayers = new ArrayList<Integer>();
		toRemove = new ArrayList<Integer>();
		playerCount = 0;
		
		fog = createFog();
		
		for(int i = 0; i < 4; i++) {
			createPlayer();
		}
	}
	
	private void mapSetup() {
		// will be random
//		for(int i = 0; i < INITIAL_NUMBER_OF_MAPS; i++) {
//			gameMapSequence.add(getRandomMapNum()); 
//		}
		gameMapSequence.add(2);
		
		for(int i = 0; i < gameMapSequence.size() ; i++ ) {  // cycles through the map sequence. i is the seq number of the map
			appendMap(gameMapSequence.get(i), i + 1); // +1 as i starts at 0
			//TiledMap currentMap = mapsMap.get(mapNumber);  // gets tiled map from map number in i'th position in sequence
			//mapsCollisionBodiesMap.put(mapNumber, MapBodyFactory.getCollisionBodies(currentMap, physWorld, i + 1));
		}
	}
	
	public void update(float delta) {
		playingUpdate(delta);
		
		// it breaks fog collision if I use a switch case????
//		switch(gameState){
//		case PLAYING:
//			playingUpdate(delta);
//			break;
//		case WAITING:
//			for(int i = 0; i < 4; i++) {
//				createPlayer();
//			}
//			if(players.size() == 4) {
//				gameState = GameState.READY;
//			}
//			break;
//		case READY:
//			gameState = GameState.PLAYING;
//			break;
//		case FINISH:
//			break;
//		}
	}
	
	private void playingUpdate(float delta) {
		physWorld.step(delta, 6, 2);
		
		// check if map needs extending
		// find biggest x coord
		float xCoord = 0;
		for(int id : alivePlayers) {
			Player p = players.get(id);
			xCoord = (p.getX() > xCoord) ? p.getX() : xCoord;
		}
		
		// check if xCoord is further that the second to last map
		if(xCoord * PPM > (gameMapSequence.size() - 1) * MAP_WIDTH_PX) {
			int pos = gameMapSequence.size() + 1;
			int mapNo = 2;
			appendMap(mapNo, pos);
			gameMapSequence.add(mapNo);
		}
		
		// update fog
		fog.update(delta, playerCount);

		// update players
		for(int id : alivePlayers) {
			Player p = players.get(id);
			
			// death check
			// below map
			if(p.getY() < 0) {
				p.kill();
				toRemove.add(id);
			} else {
				p.update(delta);
			}	
		}
		sweepDeadBodies();
		
		// win/lose check
		if(alivePlayers.size() == 1) {
			gameState = GameState.FINISH;
		}
	}
	public Player createPlayer() {
		Body playerBody = bodyFactory.createBody(playerWidth, playerHeight, START_POS_X, START_POS_Y, BodyDef.BodyType.DynamicBody, PLAYER_USER_DATA + playerCount);
	    playerBody.setLinearVelocity(0, 0);
	    Player p = new Player(playerCount, playerBody, playerWidth / PPM, playerHeight / PPM);
	    
	    players.put(playerCount, p);
	    alivePlayers.add(playerCount);
	    playerCount++;
	    return p;
	}

	public Fog createFog() {
		Body fogBody = bodyFactory.createBody(fogWidth, fogHeight, (fogWidth * -5), fogHeight / 2, BodyDef.BodyType.KinematicBody, FOG_USER_DATA);
		fogBody.setLinearVelocity(0, 0);
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
	
	public void addToKillList(String id) {
		int numId = Integer.parseInt(id);
		
		// to check if not already dead, as collision listener can trigger several times
		if(!deadPlayers.contains(numId)){
			toRemove.add(numId);
		}
	}
	
	// to avoid sync issues with box2d
	private void sweepDeadBodies() {
	for(int id : toRemove) {
			Player p = players.get(id);
			p.setState(State.DEAD);
			physWorld.destroyBody(p.getBody());
			
			alivePlayers.remove(alivePlayers.indexOf(id));
			deadPlayers.add(id);
		}
		toRemove.clear();
	}
	
	public void addPlayer(int id, Object o) {
		players.put(id, null);
	}
	
	public boolean isPlayerNull(String id) {
		return players.get(id) == null;
	}
	
	public void removePlayer(String id) {
		players.remove(id);
	}
	
	public void movePlayer(String id, float x, float y) {  // acts by just adding a new vector, so can simulate addition of new players
		//players.put(Integer.parseInt(id), new Vector2(x, y));
	}
	
	public String getPlayerData() {
		String playerData = "";
		for(int id : players.keySet()) {
			playerData += players.get(id).toString() + "/n";
		}
		
		playerData += fog.toString();
		
		return playerData;
	}
	
	public Player getPlayer(String id){
		return players.get(Integer.parseInt(id));
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Vector2> getOtherPlayers(){
		return (Map<String, Vector2>) players.clone();
	}
	
	public void keyUp(int id, int keycode) {
		players.get(id).keyUp(keycode);
	}
	
	public void keyDown(int id, int keycode) {  // will have param for player id
		players.get(id).keyDown(keycode);
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
	
	public GameState getGameState() {
		return gameState;
	}
}
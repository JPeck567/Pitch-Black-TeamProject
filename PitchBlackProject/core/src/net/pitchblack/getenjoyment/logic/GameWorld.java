package net.pitchblack.getenjoyment.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import net.pitchblack.getenjoyment.client.GameInstance;
import net.pitchblack.getenjoyment.entities.BodyFactory;
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

	private GameInstance gameInstance;

	private World physWorld;
	private BodyFactory bodyFactory;

	private final HashMap<Integer, TiledMap> mapsMap;  // stores all the loaded maps
	private HashMap<Integer, ArrayList<Body>> mapsCollisionBodiesMap;  // stores bodies for each map
	private ArrayList<Integer> gameMapSequence;  // the maps selection for the game
	private final Random random;
	
	//private Player player;
	private final HashMap<String, Player> players;  // all players
	//private HashMap<String, Vector2> otherPlayers;  // will be all players in server, so <String, Player>
	private final ArrayList<String> alivePlayers;
	private final ArrayList<String> deadPlayers;
	private final ArrayList<String> toRemove;
	private final ArrayList<String> recentlyDied;

	private Fog fog;
	private float playerWidth, playerHeight;
	private float fogWidth, fogHeight;

	public GameWorld(PBAssetManager pbAssetManager, GameInstance gameInstance) {
	    this.gameInstance = gameInstance;

		MapProperties prop = pbAssetManager.getAsset(PBAssetManager.map0).getProperties();
		MAP_WIDTH_PX = ((float) prop.get("width", Integer.class)) * PPM;
		MAP_HEIGHT_PX = ((float) prop.get("height", Integer.class)) * PPM;

		Texture playerTexture = pbAssetManager.getAsset(PBAssetManager.playerTexture);
		this.playerWidth = playerTexture.getWidth();
		this.playerHeight = playerTexture.getHeight();
		
		this.fogWidth = 1950;
		this.fogHeight = MAP_HEIGHT_PX;
		
		createB2DWorld();
		
		bodyFactory = BodyFactory.getInstance(physWorld);
		
		mapsMap = pbAssetManager.getMaps();

		random = new Random();

		gameMapSequence = new ArrayList<Integer>();
		mapsCollisionBodiesMap = new HashMap<Integer, ArrayList<Body>>();  // stores all bodies for each map in here
		mapSequenceSetup();
		
		players = new HashMap<String, Player>();
		alivePlayers = new ArrayList<String>();
		deadPlayers = new ArrayList<String>();
		toRemove = new ArrayList<String>();
        recentlyDied = new ArrayList<String>(); // used by instance client to poll who has died. after, clears list
		
		fog = createFog();
	}

	private void createB2DWorld(){
		physWorld = new World(GRAVITY_VECT, true);  // last param tells world to not simulate inactive bodies (ie two equal forces against each other)
		physWorld.setContactListener(new CollisionListener(this));
	}

	private void mapSequenceSetup() {  // create list of initial maps at random + add collision bodies to physWorld
		addMapToGame(0, 1);  // initial map is 0, in pos 1 (not 0 as cannot * by 0 to work out coord

        // start at 2 as pos 1 is taken
		for (int i = 2; i < INITIAL_NUMBER_OF_MAPS; i++) {
			addMapToGame(getRandomMapNum(), i); // pos 0 is taken up by map 0, and as loop starts at 0, we +1
		}
	}

    private void addMapToGame(int mapNumber, int position){
        gameMapSequence.add(mapNumber);
        addMapCollisionBodies(mapNumber, position);
    }

	private void addMapCollisionBodies(int mapNumber, int position) {  // map body adds bodies to physWorld, and also returns bodies to store in mapsCollisionBodiesMap
		TiledMap currentMap = mapsMap.get(mapNumber);  // gets tiled map from map number in i'th position in sequence
		mapsCollisionBodiesMap.put(mapNumber, MapBodyFactory.getCollisionBodies(currentMap, physWorld, position));
	}

	public int getRandomMapNum() {
		int numberOfMaps = mapsMap.size();
		int mapNo = 0;

		while(mapNo == 0) {
			mapNo = random.nextInt(numberOfMaps);
		}

		return mapNo;
	}

	public void setupPlayers(List<String> playerList) {
		for(String name : playerList) {
			createPlayer(name);
		}
	}

	private void createPlayer(String id) {
		Body playerBody = bodyFactory.createBody(playerWidth, playerHeight, START_POS_X, START_POS_Y, BodyDef.BodyType.DynamicBody, PLAYER_USER_DATA + "," + id);
		playerBody.setLinearVelocity(0, 0);
		Player p = new Player(id, playerBody, playerWidth / PPM, playerHeight / PPM);

		players.put(id, p);
		alivePlayers.add(id);
	}

	private Fog createFog() {
		Body fogBody = bodyFactory.createBody(fogWidth, fogHeight, (fogWidth * -2) / 3, fogHeight / 2, BodyDef.BodyType.KinematicBody, FOG_USER_DATA);
		return new Fog(fogBody, fogWidth, fogHeight);
	}

	public void update(float delta) {
        playingUpdate(delta);
    }

	private void playingUpdate(float delta) {
		physWorld.step(delta, 4, 2);

		// check if map needs extending
		// find biggest x coord
		// done in the loop for updating players
		float xCoord = 0;

		// update fog
		//fog.update(delta, playerCount);

		// update players
		for(String id : alivePlayers) {
			Player p = players.get(id);

			// checks if x coord is biggest
			xCoord = Math.max(p.getX(), xCoord);

			// death check //
            // is fallen out of map
			if(p.getY() < 0) {
				toRemove.add(id);
			// caught by fog. should use collision of box2d but is kinematic body???
			} else if (fog.getX() + (fogWidth / PPM) >= p.getX() - (playerWidth / PPM)) {
				toRemove.add(id);
			} else {
				p.update(delta);
			}
			//float i = fog.getX() + (fogWidth / GameWorld.PPM);
			//float j = p.getX() - (playerWidth / GameWorld.PPM);
			//System.out.println( i + "," + j);
		}

		// check if xCoord is further that the second to last map
		if(xCoord * PPM > (gameMapSequence.size() - 1) * MAP_WIDTH_PX) {
			int pos = gameMapSequence.size() + 1;
			int mapNo = getRandomMapNum();
			addMapToGame(mapNo, pos);
		}
		sweepDeadBodies();
	}

	public void addToKillList(String id) {	// collision listener finds collision from player to fog, so add to kill list
		// to check if not already dead, as collision listener can trigger several times
		if(!deadPlayers.contains(id)){
			toRemove.add(id);
		}
	}
	
	// to avoid sync issues with box2d
	private void sweepDeadBodies() {
	for(String id : toRemove) {
        Player p = players.get(id);
        p.kill();
        physWorld.destroyBody(p.getBody());
        alivePlayers.remove(id);  // removal by object. arraylist scans for first occurance of id + removes it
        deadPlayers.add(id);
    }
    gameInstance.addToRecentlyDied(toRemove);
    toRemove.clear();
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
		for(String id : players.keySet()) {
			playerData += players.get(id).toString() + "/n";
		}
		return playerData;
	}
	
	public String getFogData() {
		return fog.toString();
	}
	
	public Player getPlayer(String id){
		return players.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Vector2> getOtherPlayers(){
		return (Map<String, Vector2>) players.clone();
	}
	
	private void keyUp(String id, int keycode) {
		players.get(id).keyUp(keycode);
	}
	
	private void keyDown(String id, int keycode) {  // will have param for player id
		players.get(id).keyDown(keycode);
	}

	public World getWorld() {
		return physWorld;
	}

	public Fog getFog() {
		return fog;
	}
	
	public String getMapSequence(){
		String mapSeq = "";
		for(int i : gameMapSequence) {
			mapSeq += i + ",";
		}
		
		return mapSeq;
	}

	public String[] getRecentlyDied(){
	    return (String[]) recentlyDied.toArray();
    }

	public void keyPress(String id, String keyUp, String keyDown) {
		int keyUpCode = Integer.parseInt(keyUp);
		int keyDownCode = Integer.parseInt(keyDown);
		
		if(keyDownCode != -1) {
			keyDown(id, keyDownCode);
		}
		
		if(keyUpCode != -1) {
			keyUp(id, keyUpCode);
		}
	}

	public boolean finished() {
		if(GameInstance.PLAYER_MAX == 1){
			// if one player alive
			return alivePlayers.size() == 0;
		} else {
			return alivePlayers.size() == 1;
		}

	}
	
	public String getWinner() {  // assumes game finished
		return players.get(alivePlayers.get(0)).getID();
	}

    public void refreshWorld() {
		createB2DWorld();
		mapSequenceSetup();
		createFog();
		players.clear();
		alivePlayers.clear();
		deadPlayers.clear();
    }

    public void getDeadPlayers() {

    }
}

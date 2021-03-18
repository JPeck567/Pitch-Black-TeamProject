package net.pitchblack.getenjoyment.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.client.Client.ClientState;
import net.pitchblack.getenjoyment.entities.Entity;
import net.pitchblack.getenjoyment.entities.Entity.Type;
import net.pitchblack.getenjoyment.entities.Entity.EntityState;
import net.pitchblack.getenjoyment.entities.Fog;
import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen.GameState;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.helpers.SoundManager;

public class GameRenderer {
	public static final float PPM = 32; // pixels per meter
	public static float MAP_WIDTH;
	public static float MAP_HEIGHT;

	private GameScreen gameScreen;
	private Client client;
	private String username;
	private PBAssetManager pbAssetManager;

	// will need array list of string id's to sprites to update from server
	private HashMap<String, Entity> entities;

	private Entity clientPlayer;
	private Entity fog;
	// private Sprite playerSprite;
	// private Sprite fogSprite;

	private OrthographicCamera camera;
	private Viewport viewport;

	// private ShapeRenderer shapeRenderer; // draws lines and shapes easily

	private HashMap<Integer, TiledMap> mapMap;
	private ArrayList<Integer> gameMaps;
	private OrthogonalTiledMapRenderer mapRenderer;

	private SpriteBatch batcher;
	private Box2DDebugRenderer debugRenderer;

	public GameRenderer(GameScreen gameScreen, Client client, PBAssetManager pbAssetManager) {
	    this.gameScreen = gameScreen;
		this.pbAssetManager = pbAssetManager;

		this.client = client;
		this.username = client.getUsername();

		viewport = new ExtendViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
		camera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldWidth());  // this camera allows a 3d plane to be projected onto a since 2d plane
		viewport.setCamera(camera);

		// get all maps
		mapMap = pbAssetManager.getMaps();

		// placeholder to get properties + to setup map renderer with something
		TiledMap firstMap = mapMap.get(0);
		MapProperties prop = firstMap.getProperties();
		
		MAP_WIDTH = ((float) prop.get("width", Integer.class));
		MAP_HEIGHT = ((float) prop.get("height", Integer.class));

		mapRenderer = new OrthogonalTiledMapRenderer(firstMap, 1 / PPM); // map + scaling
		mapRenderer.setView(camera);

		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(camera.combined);
		batcher.enableBlending();  // to enable transparency for images I think
		batcher.setProjectionMatrix(camera.combined);
		
		entities = new HashMap<String, Entity>();

		//debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
	}

	public void render(float delta) {
	    /// SCREEN CONTROL ///
		if(clientPlayer.isDead()){
			gameScreen.setGameState(GameState.LOSE);
		}

		/// CAMERA CONTROL ///
		controlCamera();

		/// RENDERING ///
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batcher.begin();

		renderMaps();

		for (String id : entities.keySet()) {
			entities.get(id).draw(batcher);
		}

		fog.draw(batcher);
		batcher.end();

		// debugRenderer.render(gameWorld.getWorld(), camera.combined);
	}

	private void controlCamera() {
		camera.position.set(clientPlayer.getX(), clientPlayer.getY(), 0);

		// if camera x is too far right
		if (camera.position.x - (camera.viewportWidth * 0.5f) <= 0) { // left coords
			camera.position.set(camera.viewportWidth * .5f, camera.position.y, 0);
			camera.update();
			mapRenderer.setView(camera);
			batcher.setProjectionMatrix(camera.combined);
		}

		// if camera y too far down
		if (camera.position.y - (camera.viewportHeight * 0.5f) <= 0) {
			camera.position.set(camera.position.x, camera.viewportHeight * 0.5f, 0);
			camera.update();
			mapRenderer.setView(camera);
			batcher.setProjectionMatrix(camera.combined);
			// if camera y too far up
		} else if (camera.position.y + (camera.viewportHeight * 0.5f) >= MAP_HEIGHT) {
			camera.position.set(camera.position.x, MAP_HEIGHT - camera.viewportHeight * 0.5f, 0);
			camera.update();
			mapRenderer.setView(camera);
			batcher.setProjectionMatrix(camera.combined);
		}
	}

	private void renderMaps() {
		Vector3 camVec = camera.position.cpy();
		for (int mapNumber : gameMaps) {
			if ((int) mapRenderer.getMap().getProperties().get("mapNumber") != mapNumber) {
				// if the map loaded into the render is not the one needed to be rendered in this loop
				mapRenderer.setMap(mapMap.get(mapNumber));
			}
			mapRenderer.render();

			// bring forward to next map
			// slightly messy as has to render several times w/ diff camera pos
			// should remake into one big map with new segments added on instead
			camera.translate(-MAP_WIDTH, 0);
			camera.update();
			mapRenderer.setView(camera);
			batcher.setProjectionMatrix(camera.combined);
		}

		// reset camera for player
		camera.position.set(camVec);
		camera.update();
		mapRenderer.setView(camera);
		batcher.setProjectionMatrix(camera.combined);
	}

	// create player entities based on username plus their associated data, and also
	// fog used before game begins
	public void setupData(String playerData, String fogData, String mapData) {
		for (String individualPlayerData : playerData.split("/n")) { // splits into individual player records
			String[] players = individualPlayerData.split(","); // split individual player data into array
			addNewEntity(players[0], Type.PLAYER, EntityState.STANDING); // 0 index is username, used to setup player
		}

		fog = new Entity(null, Type.FOG, null, pbAssetManager);
		clientPlayer = entities.get(username);

		// reuse data parsing methods which are used ingame
		addGameData(playerData, fogData, mapData);
	}

	public void addGameData(String playerData, String fogData, String mapData) {
		addPlayerData(playerData);
		addFogData(fogData);
		addMapData(mapData);
	}

	/*
	 * Player Data Schema "playerID, playerX, playerY, playerState, playerMoveLeft,
	 * playerMoveRight, playerJumped, /n playerID, playerX, playerY, playerState,
	 * playerMoveLeft, playerMoveRight, playerJumped, Split into player records with
	 * "/n" Split attributes with ","
	 */
	private void addPlayerData(String data) {
		String[] dataArray = data.split("/n");

//		String fogDataEntry = dataArray[dataArray.length - 1];  // 0: x, 1: y
//		dataArray[dataArray.length - 1] = null;
//		String[] fogData = fogDataEntry.split(",");
//		fog.setPosition(Float.parseFloat(fogData[0]), Float.parseFloat(fogData[1]));	

		for (String dataEntry : dataArray) {
			if (dataEntry != null) {
				String[] playerData = dataEntry.split(","); // 0: id, 1: x, 2: y, 3: state, 4: moveLeft, 5: moveRight,
															// 6: jumped
				Entity e = entities.get(playerData[0]);
				if (!EntityState.valueOf(playerData[3]).equals(EntityState.DEAD)) {
					// player didn't die
					float f1 = Float.parseFloat(playerData[1]);
					float f2 = Float.parseFloat(playerData[2]);

					if (e == null) {
						continue;
					}

					e.setPosition(f1, f2);

					// if standing before jumping or falling before jumping again, play sound.
					if ((e.getState() == EntityState.STANDING
							&& EntityState.valueOf(playerData[3]) == EntityState.ASCENDING)
							|| (e.getState() == EntityState.DESCENDING
									&& EntityState.valueOf(playerData[3]) == EntityState.ASCENDING)) {
						SoundManager.getInstance().play(PitchBlackSound.JUMP);
					}

					e.setState(EntityState.valueOf(playerData[3]));
					e.setMovement(Boolean.parseBoolean(playerData[4]), Boolean.parseBoolean(playerData[5]));
				} else { // player died
					if (entities.containsKey(playerData[0])) {
						e.setState(EntityState.valueOf(playerData[3]));
					}
				}
			}
		}
	}

	private void addFogData(String fogData) {
		String[] data = fogData.split(",");
		fog.setPosition(Float.parseFloat(data[0]), Float.parseFloat(data[1]));
	}

	private void addMapData(String mapData) {
		String[] data = mapData.split(",");
		gameMaps = new ArrayList<Integer>();
		for (String map : data) {
			gameMaps.add(Integer.parseInt(map));
		}
	}

	public void addMapToSequence(int mapNumber) {
		gameMaps.add(mapNumber);
	}

	private void addNewEntity(String id, Type type, EntityState entityState) {
		entities.put(id, new Entity(id, type, entityState, pbAssetManager));
	}

	public void resetRenderer(){
		entities.clear();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Viewport getViewport() {
		return viewport;
	}
}

package net.pitchblack.getenjoyment.frontend.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.pitchblack.getenjoyment.frontend.client.Client;
import net.pitchblack.getenjoyment.frontend.game.entities.Entity;
import net.pitchblack.getenjoyment.frontend.game.screens.Hud;
import net.pitchblack.getenjoyment.frontend.game.screens.GameScreen;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;

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
	private String[] gameDataBuffer;

	private Entity clientPlayer;
	private Entity fog;
	// private Sprite playerSprite;
	// private Sprite fogSprite;

	private OrthographicCamera camera;
	private final Vector3 camScreenPosOrigin;
	private final float camScreenOrigX = 16.875f;
	private final float camScreenOrigY = 15.014999f;
	private final Matrix4 camScreenMatOrigin;
	private Viewport viewport;

	// private ShapeRenderer shapeRenderer; // draws lines and shapes easily

	private HashMap<Integer, TiledMap> mapMap;
	private ArrayList<Integer> gameMaps;
	private OrthogonalTiledMapRenderer mapRenderer;

	private Hud hud;
	private BitmapFont font;

	private SpriteBatch batcher;

	private Box2DDebugRenderer debugRenderer;

	public GameRenderer(GameScreen gameScreen, Client client, PBAssetManager pbAssetManager) {
	    this.gameScreen = gameScreen;
		this.pbAssetManager = pbAssetManager;
		entities = new HashMap<String, Entity>();
		gameDataBuffer = new String[]{"", "", ""};

		this.client = client;
		this.username = client.getUsername();

		viewport = new ExtendViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
		camera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldWidth());  // this camera allows a 3d plane to be projected onto a since 2d plane
		camScreenPosOrigin = camera.position.cpy();

		viewport.setCamera(camera);

		// get all maps
		mapMap = pbAssetManager.getMaps();

		// placeholder to get properties + to setup map renderer with a map
		TiledMap firstMap = mapMap.get(0);
		MapProperties prop = firstMap.getProperties();

		MAP_WIDTH = ((float) prop.get("width", Integer.class));
		MAP_HEIGHT = ((float) prop.get("height", Integer.class));

		mapRenderer = new OrthogonalTiledMapRenderer(firstMap, 1 / PPM); // map + scaling
		mapRenderer.setView(camera);

		batcher = new SpriteBatch();
		batcher.enableBlending();  // to enable transparency for images I think
		batcher.setProjectionMatrix(camera.combined);

		hud = new Hud(batcher);
		camScreenMatOrigin = hud.getCamera().combined;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		//font.getData().setScale(0.1f);


		//debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
	}

	public void render(float delta) {
		/// LOAD DATA HERE ///
		// avoids concurrent access exception for map if loading map whilst client adds new maps to list
		if(!gameDataBuffer[0].equals("")){  // if not empty
			addGameData();
		}

		/// UPDATE ///
		updateCamera();
		hud.update(delta);

		/// RENDERING ///
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batcher.begin();

		renderMaps();

		for (Entity e: entities.values()) {
			e.draw(batcher);
		}

		fog.draw(batcher);

		batcher.end();

		batcher.setProjectionMatrix(hud.getCamera().combined);
		batcher.begin();
		for (Entity e: entities.values()) {
			// Quite a hacky way around rendering bitmap font. it uses hud cam, as game cam scales things too big
			// and the font cannot get small enough. problem is that hud cam is static, so player name will stay on
			// screen. must translate screen coords back by the amount game cam has moved to correct positioning
			float x = (e.getX()) - (camera.position.x - camScreenOrigX);
			float y = (e.getY()) - (camera.position.y - camScreenOrigY);

			GlyphLayout glyphLayout = new GlyphLayout(); // finds width of text to center it
			glyphLayout.setText(font,e.getName());

			font.draw(batcher, e.getName(),
					// x : middle of player's x moved backward by half width of text, effectively rendering text midpoint in line with player midpoint on x
					((x + e.getWidth() / 2) * PPM) - glyphLayout.width / 2,
					(y * PPM) - PPM * 2);

		}
		batcher.end();

		hud.draw();
		batcher.setProjectionMatrix(camera.combined);

		// debugRenderer.render(gameWorld.getWorld(), camera.combined);
	}

	private void updateCamera() {
		boolean cameraMoved = false;
		camera.position.set(clientPlayer.getX(), clientPlayer.getY(), 0);

		// if camera x is too far right
		if (camera.position.x - (camera.viewportWidth * 0.5f) <= 0) { // left coords
			camera.position.set(camera.viewportWidth * .5f, camera.position.y, 0);
			cameraMoved = true;
		}

		// if camera y too far down
		if (camera.position.y - (camera.viewportHeight * 0.5f) <= 0) {
			camera.position.set(camera.position.x, camera.viewportHeight * 0.5f, 0);
			cameraMoved = true;
		// if camera y too far up
		} else if (camera.position.y + (camera.viewportHeight * 0.5f) >= MAP_HEIGHT) {
			camera.position.set(camera.position.x, MAP_HEIGHT - camera.viewportHeight * 0.5f, 0);
			cameraMoved = true;
		}

		if(cameraMoved) {
			camera.update();
			mapRenderer.setView(camera);
			batcher.setProjectionMatrix(camera.combined);
		}
	}

	private void renderMaps() {
		Vector3 camVec = camera.position.cpy();
		for (int mapNumber : gameMaps) {
			if (mapRenderer.getMap() != mapMap.get(mapNumber)) {
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
			addNewEntity(players[0], Entity.Type.PLAYER, Entity.EntityState.STANDING); // 0 index is username, used to setup player
		}

		fog = new Entity(null, Entity.Type.FOG, null, pbAssetManager);
		clientPlayer = entities.get(username);

		// reuse data parsing methods which are used ingame
		addToGameDataBuffer(playerData, fogData, mapData);
		addGameData();
	}

	public void addToGameDataBuffer(String playerData, String fogData, String mapData){
		gameDataBuffer[0] = playerData;
		gameDataBuffer[1] = fogData;
		gameDataBuffer[2] = mapData;

	}

	public void addGameData() {
		addPlayerData(gameDataBuffer[0]);
		addFogData(gameDataBuffer[1]);
		addMapData(gameDataBuffer[2]);

		// clear array
		Arrays.fill(gameDataBuffer, "");
	}

	/*
	 * Player Data Schema
	 * "playerID, playerX, playerY, playerState, playerMoveLeft, playerMoveRight, playerJumped, /n playerID, ..." (and repeat)
	 * Is split into player records with "/n". Then split for attributes with ","
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
				if (!Entity.EntityState.valueOf(playerData[3]).equals(Entity.EntityState.DEAD)) {
					// player didn't die
					float f1 = Float.parseFloat(playerData[1]);
					float f2 = Float.parseFloat(playerData[2]);

					if (e == null) {
						continue;
					}

					e.setPosition(f1, f2);

					// if standing before jumping or falling before jumping again, play sound.
					if ((e.getState() == Entity.EntityState.STANDING
							&& Entity.EntityState.valueOf(playerData[3]) == Entity.EntityState.ASCENDING)
							|| (e.getState() == Entity.EntityState.DESCENDING
									&& Entity.EntityState.valueOf(playerData[3]) == Entity.EntityState.ASCENDING)) {
						if(PreferencesManager.isSoundEnabled()) {
							SoundManager.getInstance().play(PitchBlackSound.JUMP);
						}
					}

					e.setState(Entity.EntityState.valueOf(playerData[3]));
					e.setMovement(Boolean.parseBoolean(playerData[4]), Boolean.parseBoolean(playerData[5]));
				} else { // player died
					if (entities.containsKey(playerData[0])) {
						e.setState(Entity.EntityState.valueOf(playerData[3]));
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

	private void addNewEntity(String id, Entity.Type type, Entity.EntityState entityState) {
		entities.put(id, new Entity(id, type, entityState, pbAssetManager));
	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		camera.viewportHeight = viewport.getWorldHeight();
		camera.viewportWidth = viewport.getWorldWidth();

		hud.resize(width, height);
	}

	public void resetRenderer(){
		entities.clear();
	}

	public Hud getHud(){
		return hud;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Viewport getViewport() {
		return viewport;
	}
}

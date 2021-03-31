package net.pitchblack.getenjoyment.frontend.rendering.screens.game;

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
import net.pitchblack.getenjoyment.frontend.rendering.entities.Entity;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;
import net.pitchblack.getenjoyment.frontend.rendering.entities.Fog;
import net.pitchblack.getenjoyment.frontend.rendering.entities.Player;

public class GameRenderer {
	public static final float PPM = 32; // pixels per meter
	public static float MAP_WIDTH;
	public static float MAP_HEIGHT;

	private GameScreen gameScreen;
	private Client client;
	private String username;
	private PBAssetManager pbAssetManager;

	// will need array list of string id's to sprites to update from server
	private HashMap<String, Player> players;
	private String[] gameDataBuffer;

	private Entity clientPlayer;
	private Entity fog;
	// private Sprite playerSprite;
	// private Sprite fogSprite;

	private OrthographicCamera camera;
	private final Vector3 camScreenPosOrigin;
	private final float camScreenPosX;
	private final float camScreenPosY;
	private final float camScreenOrigX = 20f;
	private final float camScreenOrigY = 11.25f;
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
		players = new HashMap<String, Player>();
		gameDataBuffer = new String[]{"", "", ""};

		this.client = client;
		this.username = client.getUsername();

		viewport = new ExtendViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
		camera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldWidth());  // this camera allows a 3d plane to be projected onto a since 2d plane
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
		//camScreenPosOrigin = camera.position.cpy();
		camScreenPosOrigin = hud.getCamera().position.cpy().scl(1/PPM); // to know what game units to offset text by
		camScreenPosX = camScreenPosOrigin.x;
		camScreenPosY = camScreenPosOrigin.y;

		System.out.println(camScreenPosX + "," + camScreenOrigY + "," + camScreenPosOrigin);
		font = new BitmapFont();
		font.setColor(Color.WHITE);

		//debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
	}

	public void render(float delta) {
		/// LOAD DATA HERE ///
		// avoids concurrent access exception for map list if rendering maps whilst client adds new maps to list
		if(!gameDataBuffer[0].equals("")){  // if not empty
			addGameData();
		}

		/// UPDATE ///
		updateCamera();
		hud.update(delta);

		/// RENDERING ///
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// work out how far camera is from original position, as font rendering works on screen coords
		// so to work on game coords, must find the offset
		Vector3 offset = camera.position.cpy().sub(camScreenPosOrigin);

		batcher.begin();
		renderMaps();
		for (Player p: players.values()) {
			p.draw(batcher);

			batcher.setProjectionMatrix(hud.getCamera().combined);  // scales down font to proper size. game world is working on 1/PPM not 1/1 as hud is
			p.drawPlayerName(batcher, font, offset);
			batcher.setProjectionMatrix(camera.combined);
		}
		fog.draw(batcher);
		batcher.end();

		hud.draw();
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
	// also sets camera position
	public void setupRenderer(String playerData, String fogData, String mapData) {
		// DATA //
		for (String singlePlayerData : playerData.split("/n")) { // splits into individual player records
			String[] playerDataArray = singlePlayerData.split(","); // split individual player data into array
			players.put(playerDataArray[0], new Player(playerDataArray[0], Integer.parseInt(playerDataArray[6]), font, pbAssetManager));
			// 0 index is username, used to setup player. index 6 is to player number, to find which sprite to use
		}

		fog = new Fog(pbAssetManager);
		clientPlayer = players.get(username);

		// reuse data parsing methods which are used ingame
		addToGameDataBuffer(playerData, fogData, mapData);
		addGameData();

		// CAMERA & POSITIONING
		updateCamera(); // set cam to first position
		//camScreenPosOrigin = camera.position.cpy();
		//System.out.println("Player Coords: " + clientPlayer.getX() + "," + clientPlayer.getY());
		//System.out.println("Cam Pos: " + camera.position.cpy());
		//System.out.println("Cam Viewport: " + camera.viewportHeight + "," + camera.viewportWidth);
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
	 * "playerID, playerX, playerY, playerState, playerMoveLeft, playerMoveRight, playerJumped, playerNo /n playerID, ..." (and repeat)
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
															// 6: playerJumped, 7: playerNo
				Player p = (Player) players.get(playerData[0]);
				if (!Player.EntityState.valueOf(playerData[3]).equals(Player.EntityState.DEAD)) {
					// player didn't die
					float x = Float.parseFloat(playerData[1]);
					float y = Float.parseFloat(playerData[2]);

					if (p == null) {
						continue;
					}

					p.setPosition(x, y);

					// if standing before jumping or falling before jumping again, play sound.
					if ((p.getState() == Player.EntityState.STANDING
							&& Player.EntityState.valueOf(playerData[3]) == Player.EntityState.ASCENDING)
							|| (p.getState() == Player.EntityState.DESCENDING
									&& Player.EntityState.valueOf(playerData[3]) == Player.EntityState.ASCENDING)) {
						if(PreferencesManager.isSoundEnabled()) {
							SoundManager.getInstance().play(PitchBlackSound.JUMP);
						}
					}

					p.setState(Player.EntityState.valueOf(playerData[3]));
					p.setMovement(Boolean.parseBoolean(playerData[4]), Boolean.parseBoolean(playerData[5]));
				} else { // player died
					if (players.containsKey(playerData[0])) {
						p.setState(Player.EntityState.valueOf(playerData[3]));
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

	public void resize(int width, int height) {
		//System.out.println("Resize: W: " + width + "H: " + height);
		viewport.update(width, height, true);
		camera.viewportHeight = viewport.getWorldHeight();
		camera.viewportWidth = viewport.getWorldWidth();

		//camScreenPosOrigin.scl(width, height, 1);

		hud.resize(width, height);
		System.out.println("Hud Cam Pos: " + hud.getCamera().position);
	}

	public void resetRenderer(){
		players.clear();
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

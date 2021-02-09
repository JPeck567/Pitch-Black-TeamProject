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
import net.pitchblack.getenjoyment.client.Client.RenderState;
import net.pitchblack.getenjoyment.entities.Entity;
import net.pitchblack.getenjoyment.entities.Entity.Type;
import net.pitchblack.getenjoyment.entities.Entity.State;
import net.pitchblack.getenjoyment.entities.Fog;
import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.helpers.SoundManager;

public class GameRenderer {
	public static final float PPM = 32; // pixels per meter
	public static float MAP_WIDTH;
	public static float MAP_HEIGHT;

	private Client client;
	private String id;
	private PBAssetManager pbAssetManager;
		
	// will need array list of string id's to sprites to update from server
	private HashMap<String, Entity> entities;
	
	private Entity player;  // replace w/ class for just pos + state
	private Entity fog;
	//private Sprite playerSprite;
	//private Sprite fogSprite;
		
	private OrthographicCamera camera;
	private Viewport viewport;

	//private ShapeRenderer shapeRenderer;  // draws lines and shapes easily

	private HashMap<Integer, TiledMap> mapMap;
	private ArrayList<Integer> gameMaps;
	private OrthogonalTiledMapRenderer mapRenderer;

	private SpriteBatch batcher;
	private Box2DDebugRenderer debugRenderer;


	public GameRenderer(Client client, PBAssetManager pbAssetManager) {
		this.pbAssetManager = pbAssetManager;
		this.client = client;
		
		this.camera = new OrthographicCamera();  // this camera allows a 3d plane to be projected onto a since 2d plane
		//camera.setToOrtho(true, Gdx.graphics.getWidth() / GameWorld.PPM , Gdx.graphics.getHeight() / GameWorld.PPM);  // to use orthographic projection (true), width and height - which are half of screen = scaled down 2x
		viewport = new ExtendViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM,  camera);
		
		// get all maps
		mapMap = pbAssetManager.getMaps();
		
		// map creation
		//Integer[] ints = {0, 0, 0, 0};
		//List<Integer> initialMaps = Arrays.asList(ints);
		//map = pbAssetManager.getAsset(PBAssetManager.map0);
		//gameMaps = gameWorld.getMapSequence();
		
		TiledMap firstMap = mapMap.get(0);
		MapProperties prop = firstMap.getProperties();
		
		MAP_WIDTH = ((float) prop.get("width", Integer.class));
		MAP_HEIGHT = ((float) prop.get("height", Integer.class));
		
		//CAMERA_YPOS_PX = ((viewport.getWorldHeight() / 2) + 50) * PPM;
		
		mapRenderer = new OrthogonalTiledMapRenderer(firstMap, 1 / PPM); // map + scaling
		mapRenderer.setView(camera);
		
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(camera.combined);
		batcher.enableBlending();
		
		// initial position
		camera.position.set(0, camera.viewportHeight / 4, 0);
		camera.update();
		mapRenderer.setView(camera);
		batcher.setProjectionMatrix(camera.combined);
		
		entities = new HashMap<String, Entity>();
		//player = gameWorld.getPlayer("0");
		fog = new Entity(Type.FOG, null, null, pbAssetManager);
	
		//this.playerSprite = new Sprite(playerTexture);
		//this.playerSprite.setBounds(0, 0, playerTexture.getWidth() / PPM, playerTexture.getHeight() / PPM);
		
		//this.fogSprite = new Sprite(pbAssetManager.getAsset(PBAssetManager.fogTexture));
		//this.fogSprite.setBounds(0, 0, 192 / PPM, MAP_HEIGHT); // no / by ppm, as it is * by ppm too
		
		debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true); 
		
		// initial position
		//camera.position.set(viewport.getWorldWidth() / 2, (viewport.getWorldHeight() / 2), 0);// - (50 / PPM)
		//camera.update();
		
//		camera.position.set(camera.viewportHeight / 4, camera.viewportHeight / 4, 0);
//		camera.update();
//		mapRenderer.setView(camera);
//		batcher.setProjectionMatrix(camera.combined);
		
		//loadPlayerEntities();
	}

	public void render(float delta) {
		// sets entity data
		//addData(gameWorld.getPlayerData());
		
		// move player texture
		//player.setPosition(player.getX(), player.getY());
		//System.out.println(player.getState());
		
		if(player.getState() == State.DEAD) {
			client.setState(RenderState.LOSE);
		}
		
		// control camera here
		camera.position.set(player.getX(), player.getY(), 0);
			// camera x is too far right
			if(camera.position.x - (camera.viewportWidth * 0.5f) <= 0) {  // left coords
				camera.position.set(camera.viewportWidth * .5f, camera.position.y, 0);
				camera.update();
				mapRenderer.setView(camera);
				batcher.setProjectionMatrix(camera.combined);
			} 
			
			// camera y too far down
			if(camera.position.y - (camera.viewportHeight * 0.5f) <= 0) {
				camera.position.set(camera.position.x, camera.viewportHeight * 0.5f, 0);
				camera.update();
				mapRenderer.setView(camera);
				batcher.setProjectionMatrix(camera.combined);
			// camera y too far up
			} else if(camera.position.y + (camera.viewportHeight * 0.5f) >= MAP_HEIGHT) {
				camera.position.set(camera.position.x, MAP_HEIGHT - camera.viewportHeight * 0.5f, 0);
				camera.update();
				mapRenderer.setView(camera);
				batcher.setProjectionMatrix(camera.combined);
			}
			
		// move fog
		//fogSprite.setPosition(fog.getX(), fog.getY());

		// start rendering here
		// Black background is drawn which prevents flickering.
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Vector3 camVec = camera.position.cpy();
		
		//gameMaps = gameWorld.getMapSequence();
		
		for(int mapNumber : gameMaps) {
			if((int) mapRenderer.getMap().getProperties().get("mapNumber") != mapNumber) {
				// if the map loaded into the render is not the one needed to be rendered in this loop
				mapRenderer.setMap(mapMap.get(mapNumber));
			}
			mapRenderer.render();
			
			// bring forward to next map
			// slightly messy as has to render several times w/ diff camera pos
			// should remake into one big map with new segments instead
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

		
		batcher.begin();
		//playerSprite.draw(batcher);
		// draw other players
		for(String id : entities.keySet()) {
			entities.get(id).draw(batcher);
		}
		fog.draw(batcher);
		
		//batcher.draw(playerTexture, player.getX(), player.getY());
		// other player entities
//		for(Vector2  vect2 : (gameWorld.getOtherPlayers()).values()) {
//			if(vect2 != null) {
//				batcher.draw(playerSprite, vect2.x, vect2.y);
//			}	
//		}
		
		batcher.end();
		
		//debugRenderer.render(gameWorld.getWorld(), camera.combined);
	}
	
	public void loadEntities(String idList) {
		for(String pID : idList.split("/n")) {
			entities.put(pID, new Entity(Type.PLAYER, pID, State.STANDING, pbAssetManager));
		}

		player = entities.get(id);
	}
	
	public void addPlayerData(String data) {
		String[] dataArray = data.split("/n");
		
//		String fogDataEntry = dataArray[dataArray.length - 1];  // 0: x, 1: y
//		dataArray[dataArray.length - 1] = null;
//		String[] fogData = fogDataEntry.split(",");
//		fog.setPosition(Float.parseFloat(fogData[0]), Float.parseFloat(fogData[1]));	
		
		for(String dataEntry : dataArray) {
			if(dataEntry != null) {  // 0: id, 1: x, 2: y, 3: state, 4: moveLeft, 5: moveRight, 6: jumped
				String[] playerData = dataEntry.split(",");
				Entity e = entities.get(playerData[0]);
				if(!State.valueOf(playerData[3]).equals(State.DEAD)) {
					float f1 = Float.parseFloat(playerData[1]);
					float f2 = Float.parseFloat(playerData[2]);
					
					if(e == null){
						continue;
					}
					
					e.setPosition(f1, f2);
					if((e.getState() == State.STANDING && State.valueOf(playerData[3]) == State.ASCENDING) || 
							(e.getState() == State.DESCENDING && State.valueOf(playerData[3]) == State.ASCENDING)) {
							SoundManager.getInstance().play(PitchBlackSound.JUMP);
					}
					
					e.setState(State.valueOf(playerData[3]));
					e.setMovement(Boolean.parseBoolean(playerData[4]), Boolean.parseBoolean(playerData[5]));
				} else {
					if(entities.containsKey(playerData[0])) {
						e.setState(State.valueOf(playerData[3]));
						entities.remove(playerData[0]);
					}
					
				}
			}
		}
	}
	
	public void addFogData(String fogData) {
		String[] data = fogData.split(",");
		fog.setPosition(Float.parseFloat(data[0]), Float.parseFloat(data[1]));
	}
	
	public void addMapData(String mapData) {
		String[] data = mapData.split("/n");
		gameMaps = new ArrayList<Integer>();
		for(String map : data) {
			gameMaps.add(Integer.parseInt(map));
		}
	}

	public void addMapToSequence(int mapNumber) {
		gameMaps.add(mapNumber);
	}
	
	public void addEntity(String id, Type type, State state) {
		entities.put(id, new Entity(type, id, state, pbAssetManager));
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void keyDown(int keycode) {
		if(player.getState() != State.DEAD) {
			client.keyDown(keycode);
		}
	}

	public void keyUp(int keycode) {
		if(player.getState() != State.DEAD) {
			client.keyUp(keycode);
		}
	}

	public void setID(String id) {
		this.id = id;
		
	}
}

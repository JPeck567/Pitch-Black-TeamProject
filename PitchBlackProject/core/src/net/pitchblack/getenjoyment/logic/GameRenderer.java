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

import net.pitchblack.getenjoyment.entities.Fog;
import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

// alternative is to use box2D, which seems better but harder? - https://www.gamedevelopment.blog/full-libgdx-game-tutorial-box2d/

public class GameRenderer {
	public static final float PPM = 32; // pixels per meter
	private final float MAP_WIDTH;
	private final float MAP_HEIGHT;
	//private final float CAMERA_YPOS_PX;
	private GameWorld gameWorld;
	
	private PBAssetManager pbAssetManager;
		
	// will need array list of string id's to sprites to update from server
	private HashMap<String, GraphicsEntity> entities;
	
	private Player player;  // replace w/ class for just pos + state
	private Fog fog;
	private Sprite playerSprite;
	private Sprite fogSprite;
		
	private OrthographicCamera camera;
	private Viewport viewport;

	//private ShapeRenderer shapeRenderer;  // draws lines and shapes easily
	

	private HashMap<Integer, TiledMap> mapMap;
	private ArrayList<Integer> gameMaps;
	private OrthogonalTiledMapRenderer mapRenderer;
	

	private SpriteBatch batcher;
	private Box2DDebugRenderer debugRenderer;


	public GameRenderer(GameWorld gameWorld, PBAssetManager pbAssetManager, Texture playerTexture) {
		this.gameWorld = gameWorld;
		this.pbAssetManager = pbAssetManager;
		
		this.camera = new OrthographicCamera();  // this camera allows a 3d plane to be projected onto a since 2d plane
		//camera.setToOrtho(true, Gdx.graphics.getWidth() / GameWorld.PPM , Gdx.graphics.getHeight() / GameWorld.PPM);  // to use orthographic projection (true), width and height - which are half of screen = scaled down 2x
		viewport = new ExtendViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM,  camera);



		
		// get all maps
		mapMap = pbAssetManager.getMaps();
		

		// map creation
		//Integer[] ints = {0, 0, 0, 0};
		//List<Integer> initialMaps = Arrays.asList(ints);
		//map = pbAssetManager.getAsset(PBAssetManager.map0);
		gameMaps = gameWorld.getMapSequence();
		
		TiledMap firstMap = mapMap.get(gameMaps.get(0));
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
		
		player = gameWorld.getPlayer("0");
		fog = gameWorld.getFog();
	
		this.playerSprite = new Sprite(playerTexture);
		this.playerSprite.setBounds(0, 0, playerTexture.getWidth() / PPM, playerTexture.getHeight() / PPM);
		
		this.fogSprite = new Sprite(pbAssetManager.getAsset(PBAssetManager.fogTexture));
		this.fogSprite.setBounds(0, 0, 192 / PPM, MAP_HEIGHT); // no / by ppm, as it is * by ppm too
		
		debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true); 
		
		// initial position
		//camera.position.set(viewport.getWorldWidth() / 2, (viewport.getWorldHeight() / 2), 0);// - (50 / PPM)
		//camera.update();
		
//		camera.position.set(camera.viewportHeight / 4, camera.viewportHeight / 4, 0);
//		camera.update();
//		mapRenderer.setView(camera);
//		batcher.setProjectionMatrix(camera.combined);

	}

	public void render(float delta) {
		// move player texture
		playerSprite.setPosition(player.getX(), player.getY());
		
		// control camera here
		camera.position.set(playerSprite.getX(), playerSprite.getY(), 0);
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

		
		
		playerSprite.setPosition(player.getX(), player.getY());
		fogSprite.setPosition(fog.getX(), fog.getY());


		// start rendering here

		
		// Black background is drawn which prevents flickering.
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Vector3 camVec = camera.position.cpy();
		
		for(int mapNumber : gameMaps) {
			if((int) mapRenderer.getMap().getProperties().get("mapNumber") != mapNumber) {
				// if the map loaded into the render is not the one needed to be rendered in this loop
				mapRenderer.setMap(mapMap.get(mapNumber));
			}
			mapRenderer.render();
			
			// bring forward to next map
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
		playerSprite.draw(batcher);
		fogSprite.draw(batcher);
		
		//batcher.draw(playerTexture, player.getX(), player.getY());
		// other player entities
		for(Vector2 vect2 : (gameWorld.getOtherPlayers()).values()) {
			if(vect2 != null) {
				batcher.draw(playerSprite, vect2.x, vect2.y);
			}	
		}
		
		batcher.end();
		
		debugRenderer.render(gameWorld.getWorld(), camera.combined);
	}
	
	public void addMapToSequence(int mapNumber) {
		gameMaps.add(mapNumber);
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public Viewport getViewport() {
		return viewport;
	}
}

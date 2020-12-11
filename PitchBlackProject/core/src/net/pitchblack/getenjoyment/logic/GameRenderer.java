package net.pitchblack.getenjoyment.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;

// alternative is to use box2D, which seems better but harder? - https://www.gamedevelopment.blog/full-libgdx-game-tutorial-box2d/

public class GameRenderer {
	public static final float PPM = 32; // pixels per meter
	private GameWorld gameWorld;
	
	private PBAssetManager pbAssetManager;
	
	// will need array list of string id's to sprites to update from server
	private Player player;  // replace w/ class for just pos + state
	private Sprite playerTexture;
	private Sprite fogTexture;
		
	private OrthographicCamera camera;
	private Viewport viewport;
	//private ShapeRenderer shapeRenderer;  // draws lines and shapes easily
	
	private TiledMap map;
	private ArrayList<Integer> maps;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	private SpriteBatch batcher;
	
	private Box2DDebugRenderer debugRenderer;

	public GameRenderer(GameWorld gameWorld, PBAssetManager pbAssetManager, Texture playerTexture, Texture fogTexture) {
		this.gameWorld = gameWorld;
		this.pbAssetManager = pbAssetManager;
		
		player = gameWorld.getPlayer("0");
		this.playerTexture = new Sprite(playerTexture);
		this.playerTexture.setBounds(0, 0, playerTexture.getWidth() / PPM, playerTexture.getHeight() / PPM);
		
		this.fogTexture = new Sprite(fogTexture);
		
		this.camera = new OrthographicCamera();  // this camera allows a 3d plane to be projected onto a since 2d plane
		//camera.setToOrtho(true, Gdx.graphics.getWidth() / GameWorld.PPM , Gdx.graphics.getHeight() / GameWorld.PPM);  // to use orthographic projection (true), width and height - which are half of screen = scaled down 2x
		viewport = new ExtendViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM,  camera);

		// initial position
		camera.position.set(viewport.getWorldHeight() / 2, 100, 0);
		camera.update();
		
		
		// map creation
		Integer[] ints = {1, 1, 1, 1};
		List<Integer> initialMaps = Arrays.asList(ints);
		map = pbAssetManager.get(PBAssetManager.map0);
		maps = new ArrayList<Integer>(initialMaps);
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / PPM); // map + scaling
		mapRenderer.setView(camera);
		
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(camera.combined);
		batcher.enableBlending();
		//this.shapeRenderer = new ShapeRenderer();
		//shapeRenderer.setProjectionMatrix(camera.combined);
		
		debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true); 
	}

	public void render(float delta) {
		//Gdx.app.log("GameRenderer", "render");
		
		// control camera here
		if(player.hasMoved()) {
			if(player.getX() > 0) {
				camera.position.set(player.getX(), camera.viewportHeight / 4, 0);
				camera.update();
				mapRenderer.setView(camera);
				batcher.setProjectionMatrix(camera.combined);
			}
		}
		
		playerTexture.setPosition(player.getX(), player.getY());
		
		
//		shapeRenderer.setProjectionMatrix(camera.combined);
//		
//		// start rendering here
//		
//		// Black background is drawn which prevents flickering.
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
//		for(int mapNumber : maps) {
//			if((int) mapRenderer.getMap().getProperties().get("mapNumber") != mapNumber) {
//				mapRenderer.setMap(pbAssetManager.get(PBAssetManager.map0));
//			}
//			mapRenderer.render();
//		}
		
		mapRenderer.render();
		
	
		batcher.begin();
		
		playerTexture.draw(batcher);
		
		fogTexture.draw(batcher);
		
		
		//batcher.draw(playerTexture, player.getX(), player.getY());
		// other player entities
		for(Vector2 vect2 : (gameWorld.getOtherPlayers()).values()) {
			if(vect2 != null) {
				batcher.draw(playerTexture, vect2.x, vect2.y);
			}	
		}
		
		batcher.end();
		
		debugRenderer.render(gameWorld.getWorld(), camera.combined);
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public Viewport getViewport() {
		return viewport;
	}
}

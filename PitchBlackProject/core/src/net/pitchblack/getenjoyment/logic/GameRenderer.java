package net.pitchblack.getenjoyment.logic;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.graphics.screens.GameScreen;
import net.pitchblack.getenjoyment.helpers.AssetLoader;

// alternative is to use box2D, which seems better but harder? - https://www.gamedevelopment.blog/full-libgdx-game-tutorial-box2d/

public class GameRenderer {
	private GameWorld gameWorld;
		
	private OrthographicCamera camera;
	//private ShapeRenderer shapeRenderer;  // draws lines and shapes easily
	
	//private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	private SpriteBatch batcher;
	
	
	
	public GameRenderer(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		
		this.camera = new OrthographicCamera();  // this camera allows a 3d plane to be projected onto a since 2d plane
		camera.setToOrtho(true, Gdx.graphics.getWidth() , Gdx.graphics.getHeight());  // to use orthographic projection (true), width and height - which are half of screen = scaled down 2x
    	
		mapRenderer = new OrthogonalTiledMapRenderer(gameWorld.getMap());
		mapRenderer.setView(camera);
		
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(camera.combined);
		batcher.enableBlending();
		//this.shapeRenderer = new ShapeRenderer();
		//shapeRenderer.setProjectionMatrix(camera.combined);
	}
	
	public void render(float delta) {
		//Gdx.app.log("GameRenderer", "render");
		
		mapRenderer.setView(camera);
		batcher.setProjectionMatrix(camera.combined);
		//shapeRenderer.setProjectionMatrix(camera.combined);
		
		// Black background is drawn which prevents flickering.
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Player player = gameWorld.getPlayer();
		
		mapRenderer.render();
		
		if(player.hasMoved()) {
			camera.position.set(player.getX(), player.getY(), 0);
			camera.update();
		}
				
		batcher.begin();
		
		
		
		player.draw(batcher);
		//batcher.draw(AssetLoader.player, player.getX(), player.getY());
		for(Vector2 vect2 : (gameWorld.getOtherPlayers()).values()) {
			if(vect2 != null) {
				batcher.draw(AssetLoader.player, vect2.x, vect2.y);
			}	
		}
		batcher.end();
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
}

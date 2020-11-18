package net.pitchblack.getenjoyment.logic;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import net.pitchblack.getenjoyment.entities.Player;
import net.pitchblack.getenjoyment.helpers.AssetLoader;

public class GameWorld {
	
	private TiledMap map;
	public static int TILE_DIM;
	private Player player;
	private HashMap<String, Vector2> otherPlayers;
	
	public GameWorld() {
		map = new TmxMapLoader().load("maps/map1.tmx");;
		TiledMapTileLayer collsionLayer = (TiledMapTileLayer) map.getLayers().get("collision");
		TILE_DIM = ((TiledMapTileLayer) map.getLayers().get("collision")).getTileHeight();
		player = new Player(collsionLayer);
		otherPlayers = new HashMap<String, Vector2>();
	}
	
	public void update(float delta) {
		player.update(delta);
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
	
	public Player getPlayer(){
		return player;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Vector2> getOtherPlayers(){
		return (Map<String, Vector2>) otherPlayers.clone();
	}

	

}
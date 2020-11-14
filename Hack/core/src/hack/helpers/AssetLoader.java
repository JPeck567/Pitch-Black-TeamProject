package hack.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class AssetLoader {
    public static Texture texture;
    public static TextureRegion player;
    //public static TiledMap map;
    //public static int TILE_DIM;  // height = width for tile
    //public static TiledMapTileLayer collisionLayer;

    
    public static void load() {
    	texture = new Texture(Gdx.files.internal("playerTexture/textureTransparentCrop.png"));
    	//texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    	
    	player = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
    	player.flip(false, true);
    }
    
    public static void dispose() {
    	texture.dispose();
    }
}

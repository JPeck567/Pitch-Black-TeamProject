package net.pitchblack.getenjoyment.helpers;

import java.nio.MappedByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PBAssetManager {
	public final AssetManager manager;
	
	// TODO: use texture atlas for skin + eventually player sprite sheets
    public static final AssetDescriptor<Skin> menuSkin = new AssetDescriptor<Skin>("skin/glassy-ui.json", Skin.class, new SkinLoader.SkinParameter("skin/glassy-ui.atlas"));
    public static final AssetDescriptor<Texture> playerTexture = new AssetDescriptor<Texture>("textures/player.png", Texture.class);
    public static final AssetDescriptor<Texture> fogTexture = new AssetDescriptor<Texture>("textures/fog.png", Texture.class);
    
    private static final String map0Path = "maps/map0.tmx";  // need to manually load parameters and class 
    public static final AssetDescriptor<TiledMap> map0 = new AssetDescriptor<TiledMap>(map0Path, TiledMap.class);
	
    private static final String[] mapArray = {map0Path};
    
    public PBAssetManager() {
    	manager = new AssetManager();
    }
        
    public void loadSkins() { // for ui
    	manager.load(menuSkin);
    	manager.finishLoading();
    }
    
    public void loadTextures() {  // for game
    	manager.load(playerTexture);
    	manager.load(fogTexture);
    	manager.finishLoading();
    }
    
    public void loadMaps() {
    	//Parameters mapParam = new Parameters();
    	//mapParam.flipY = false;  // needs to have y facing down
    	
    	manager.setLoader(TiledMap.class, new TmxMapLoader());
    	
    	for(String mapString : mapArray) {
    		manager.load(mapString, TiledMap.class);
    	}
    	manager.finishLoading();
    }
    
    public <T> T get(AssetDescriptor<T> desc) {
    	return manager.get(desc);
    }
    
    public void dispose() {
    	manager.dispose();
    }
}

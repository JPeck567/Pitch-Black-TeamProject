package net.pitchblack.getenjoyment.helpers;

import java.nio.MappedByteBuffer;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
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

public class PBAssetManager{  // can't make into singleton, as may corrupt textures in libGDX
	public final AssetManager manager;
	
	// TODO: use texture atlas for skin + eventually player sprite sheets
    public static final AssetDescriptor<Skin> menuSkin = new AssetDescriptor<Skin>("skin_2/flat-earth-ui.json", Skin.class, new SkinLoader.SkinParameter("skin_2/flat-earth-ui.atlas"));
    public static final AssetDescriptor<Texture> menuBackground = new AssetDescriptor<Texture>("background/background.png", Texture.class);
    public static final AssetDescriptor<Texture> playerTexture = new AssetDescriptor<Texture>("texture/player.png", Texture.class);
    public static final AssetDescriptor<Texture> fogTexture = new AssetDescriptor<Texture>("texture/fog.png", Texture.class);
    
    private static final String map0Path = "maps/map00.tmx";  // need to manually load parameters and class, if parameters are needed
    public static final AssetDescriptor<TiledMap> map0 = new AssetDescriptor<TiledMap>(map0Path, TiledMap.class);
    private static final String map1Path = "maps/map01.tmx";
    public static final AssetDescriptor<TiledMap> map1 = new AssetDescriptor<TiledMap>(map1Path, TiledMap.class);
    private static final String map2Path = "maps/map02.tmx";
    public static final AssetDescriptor<TiledMap> map2 = new AssetDescriptor<TiledMap>(map2Path, TiledMap.class);
	
    private static final String[] mapArray = {map0Path, map1Path, map2Path};

    public PBAssetManager() {
    	manager = new AssetManager();
    }
        
    public void loadMenuAssets() {
    	manager.load(menuSkin);
    	manager.load(menuBackground);
    	manager.finishLoading();
    }
    
    public void loadTextures() {
    	manager.load(playerTexture);
    	manager.load(fogTexture);
    	manager.finishLoading();
    }
    
    public void loadMaps() {
    	manager.setLoader(TiledMap.class, new TmxMapLoader());
    	
    	for(String mapString : mapArray) {
    		manager.load(mapString, TiledMap.class);
    	}
    	manager.finishLoading();
    }
    
    public <T> T getAsset(AssetDescriptor<T> desc) {
    	//return (T) Gdx.files.internal(desc.fileName); 
    	return manager.get(desc);
    }
    
    public HashMap<Integer, TiledMap> getMaps(){
    	HashMap<Integer, TiledMap> mapMap = new HashMap<Integer, TiledMap>();
    	for(int i = 0; i < mapArray.length; i++) {
    		mapMap.put(i, getMap(i)); 
    	}
    	
    	return mapMap;
    }
    
    private TiledMap getMap(int mapNum) {
    	switch (mapNum) {
			case 0:
				return getAsset(map0);
			case 1:
				return getAsset(map1);
			case 2:
				return getAsset(map2);
			default:
				System.out.println("Invalid Map!");
				return getAsset(map0);
    	}
    }
    
//    public void dispose() {
//    	dispose();
//    }
}

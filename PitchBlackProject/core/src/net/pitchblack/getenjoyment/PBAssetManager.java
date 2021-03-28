package net.pitchblack.getenjoyment;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PBAssetManager{  // can't make into singleton, as may corrupt textures in libGDX
	public final AssetManager manager;
	
	// TODO: use texture atlas for skin + eventually player sprite sheets
    public static final AssetDescriptor<Skin> screenSkin = new AssetDescriptor<Skin>("skin_2/flat-earth-ui.json", Skin.class, new SkinLoader.SkinParameter("skin_2/flat-earth-ui.atlas"));
    public static final AssetDescriptor<Texture> menuBackground = new AssetDescriptor<Texture>("background/background.png", Texture.class);
	public static final AssetDescriptor<Texture> menuBackgroundClear = new AssetDescriptor<Texture>("background/backgroundDark.jpg", Texture.class);
    public static final AssetDescriptor<Texture> gameOverBackground = new AssetDescriptor<Texture>("background/gameOverBackGround.png", Texture.class);
	public static final AssetDescriptor<Texture> winBackground = new AssetDescriptor<Texture>("background/winScreenBackground.png", Texture.class);
    public static final AssetDescriptor<Texture> playerTexture = new AssetDescriptor<Texture>("texture/player.png", Texture.class);
    public static final AssetDescriptor<Texture> fogTexture = new AssetDescriptor<Texture>("texture/fog.png", Texture.class);
    
    public static final AssetDescriptor<TiledMap> map0 = new AssetDescriptor<TiledMap>("maps/map00.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map1 = new AssetDescriptor<TiledMap>("maps/map01.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map2 = new AssetDescriptor<TiledMap>("maps/map02.tmx", TiledMap.class);
	public static final AssetDescriptor<TiledMap> map3 = new AssetDescriptor<TiledMap>("maps/map03.tmx", TiledMap.class);
	public static final AssetDescriptor<TiledMap> map4 = new AssetDescriptor<TiledMap>("maps/map04.tmx", TiledMap.class);
	public static final AssetDescriptor<TiledMap> map5 = new AssetDescriptor<TiledMap>("maps/map05.tmx", TiledMap.class);
    private static final AssetDescriptor[] mapArray = {map0, map1, map2, map3, map4, map5};

    public PBAssetManager() {
    	manager = new AssetManager();
    }
    public void loadMenuAssets() {
    	manager.load(screenSkin);
    	manager.load(menuBackground);
		manager.load(menuBackgroundClear);
    	manager.load(gameOverBackground);
    	manager.load(winBackground);
    	manager.finishLoading();
    }
    
    public void loadTextures() {
    	manager.load(playerTexture);
    	manager.load(fogTexture);
    	manager.finishLoading();
    }
    
    public void loadMaps() {
    	manager.setLoader(TiledMap.class, new TmxMapLoader());
    	
    	for(AssetDescriptor mapDesc : mapArray) {
    		manager.load(mapDesc);
    	}
    	manager.finishLoading();
    }
    
    public <T> T getAsset(AssetDescriptor<T> desc) {
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
            case 3:
                return getAsset(map3);
			case 4:
				return getAsset(map4);
			case 5:
				return getAsset(map5);
			default:
				System.out.println("Invalid Map!");
				return getAsset(map0);
    	}
    }
    
//    public void dispose() {
//    	dispose();
//    }
}

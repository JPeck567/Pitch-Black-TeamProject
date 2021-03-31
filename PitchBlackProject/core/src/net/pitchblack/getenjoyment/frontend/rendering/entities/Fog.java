package net.pitchblack.getenjoyment.frontend.rendering.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import net.pitchblack.getenjoyment.PBAssetManager;

public class Fog extends Entity {
    public Fog(PBAssetManager pbAssetManager) {
        super(SpriteType.FOG, pbAssetManager);
    }
}

package net.pitchblack.getenjoyment.frontend.rendering.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.rendering.screens.game.GameRenderer;

public class Player extends Entity{
    private final String name;
    private final int playerNum;
    private final BitmapFont font;
    private GlyphLayout glyphLayout;
    private EntityState entityState;  // either ascending, descending or dead
    private boolean movementLeft;
    private boolean movementRight;
    private boolean facingForward;

    public enum EntityState {
        ASCENDING,
        DESCENDING,
        STANDING,
        LEFT,
        RIGHT,
        DEAD;
    }

    public Player(String name, int playerNum, BitmapFont font, PBAssetManager pbAssetManager) {
        super(playerTypeByNumber(playerNum), pbAssetManager);
        this.name = name;
        this.playerNum = playerNum;
        this.font = font;
        glyphLayout = new GlyphLayout(); // finds width of text used to center it
        glyphLayout.setText(font, name);
        entityState = EntityState.STANDING;
        movementLeft = false;
        movementRight = false;
        facingForward = true;
    }

    private static SpriteType playerTypeByNumber(int num){
        switch (num){
            case 0: return SpriteType.PLAYER_ROGUE;
            case 1: return SpriteType.PLAYER_CLOWN;
            case 2: return SpriteType.PLAYER_ENGINEER;
            case 3: return SpriteType.PLAYER_NINJA;
            default: return SpriteType.PLAYER_ROGUE;
        }
    }

    public void draw(SpriteBatch batch) {
        if (entityState != EntityState.DEAD) {
            super.draw(batch);
        }
    }

    public void drawPlayerName(Batch batch, BitmapFont font, Vector3 offset){
        // coord is first moved by how far the game camera has moved then positioned by half width amount to 'center'
        // as getX() gets coord at top left corner
        // x moved back by half width of text effectively rendering text in the middle
        // as without text would render starting from middle
        // y coord is moved up by 1.625 units (32 * 1.625) renders above player
        float x = ((getX() - offset.x + (getWidth() / 2)) * GameRenderer.PPM) - glyphLayout.width / 2;
        float y = ((getY() - offset.y + 1.625f) * GameRenderer.PPM );
        font.draw(batch, name, x, y);
    }

    public boolean isDead() {
        return entityState == EntityState.DEAD;
    }

    public String getName(){
        return name;
    }

    public void setMovement(boolean left, boolean right) {
        movementLeft = left;
        movementRight = right;

        if(movementLeft && facingForward){
            setFlip(true, false);
            facingForward = false;
        } else if (movementRight && !facingForward){
            setFlip(false, false);
            facingForward = true;
        }
    }

    public EntityState getState() {
        return entityState;
    }

    public void setState(EntityState entityState) {
        this.entityState = entityState;
    }
}

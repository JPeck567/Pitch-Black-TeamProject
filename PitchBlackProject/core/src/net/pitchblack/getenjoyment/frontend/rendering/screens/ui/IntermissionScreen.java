package net.pitchblack.getenjoyment.frontend.rendering.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.rendering.PitchBlackGraphics;

// used by other classes as needed in their render methods
// done by calling parent.getIntermissionScreen().renderScreen(Title.enumTypeHere); in place of screens own render method
public class IntermissionScreen implements Screen {
    public enum Title {
        LOADING("Loading"),
        WAITING("Waiting for players to be ready");

        private String titleText;

        Title(String titleText) {
            this.titleText = titleText;
        }

        public String getTitleText() {
            return titleText;
        }
    }
    private PitchBlackGraphics parent;
    private Stage stage;
    private Table table;
    private Skin skin;
    private Skin fontSkin;
    private Label titleLabel;

    public IntermissionScreen(PitchBlackGraphics p) {
        parent = p;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("skin_2/flat-earth-ui.json"));
        fontSkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        BitmapFont gameTitle = new BitmapFont(Gdx.files.internal("font/title.fnt"));

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(gameTitle, null);
        titleLabel = new Label("LOADING", titleLabelStyle);

        table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(parent.pbAssetManager.getAsset(PBAssetManager.menuBackgroundClear))));
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);

        table.add(titleLabel).colspan(2);
    }

    public void renderScreen(float delta, Title loadingText) {
        titleLabel.setText(loadingText.getTitleText());
        render(delta);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

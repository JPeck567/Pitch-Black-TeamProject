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

public class CountdownScreen implements Screen {
    public enum Title {
        LOADING("Loading"),
        WAITING("Waiting for players");

        private String titleText;

        Title(String titleText){
            this.titleText = titleText;
        }

        public String getTitleText() {
            return titleText;
        }
    }
    private PitchBlackGraphics parent;
    private float millisecondsLapsed;
    private float millisecondsLeft;
    private Stage stage;
    private Table table;
    private Skin skin;
    private Skin fontSkin;
    private Label titleLabel;
    private Label countdownSecondsLabel;
    private Label colonLabel;
    private Label countdownMillisecondsLabel;

    public CountdownScreen(PitchBlackGraphics p) {
        parent = p;
        millisecondsLapsed = 0;
        millisecondsLeft = 0;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("skin_2/flat-earth-ui.json"));
        fontSkin = new Skin (Gdx.files.internal("skin/glassy-ui.json"));

        BitmapFont gameTitle = new BitmapFont(Gdx.files.internal("font/title.fnt"));

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(gameTitle, null);
        titleLabel = new Label("Get Ready! Starting in: ", titleLabelStyle);
        countdownSecondsLabel = new Label("", titleLabelStyle);
        colonLabel = new Label(":", titleLabelStyle);
        countdownMillisecondsLabel = new Label("", titleLabelStyle);

        table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(parent.pbAssetManager.getAsset(PBAssetManager.menuBackgroundClear))));
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);
        table.add(titleLabel).colspan(2);
        table.row().padBottom(25);
        table.add(countdownSecondsLabel).right().padRight(5);
        //table.add(colonLabel).left();
        table.add(countdownMillisecondsLabel).left();
    }

    public void setMillisecondsLeft(int millisecondsLeft){
        this.millisecondsLeft = millisecondsLeft; // to milliseconds
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        millisecondsLeft -= delta; // in seconds to convert to milliseconds
        if(millisecondsLeft >= 0){  // don't display under 0
            // % to get seconds as first char in string
            // displays seconds : milliseconds (to 2 s.f)
            countdownSecondsLabel.setText(Float.toString(millisecondsLeft).substring(0,1));
            countdownMillisecondsLabel.setText(Float.toString(millisecondsLeft).substring(2, 4));
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
    }
}


package net.pitchblack.getenjoyment.graphics.screens;

import java.awt.Font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class CreditsScreen implements Screen {
    private PitchBlackGraphics parent;
    private Stage stage;
    private Music music;

    private BitmapFont GameFont;
    private SpriteBatch batch;

    public CreditsScreen(PitchBlackGraphics p) {
        GameFont = new BitmapFont(Gdx.files.internal("skin/arial-15.fnt"));
        batch = new SpriteBatch();

        parent = p;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

    }


    @Override
    public void show() {
        // TODO Auto-generated method stub
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        GameFont.draw(batch, "Team 8", 400, 675);

        GameFont.draw(batch, "Game Graphics:", 50, 575);
        GameFont.draw(batch, "-Jorge, Nav, Sam, Faisal, Eryk", 50, 525);

        GameFont.draw(batch, "Game Server:", 50, 375);
        GameFont.draw(batch, "-Alex, Rayhaan, Ryan", 50, 325);

        GameFont.draw(batch, "Game Logic:", 50, 175);
        GameFont.draw(batch, "-Joe, Ryan, Hongyi Wang, Jorge", 50, 125);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }
}

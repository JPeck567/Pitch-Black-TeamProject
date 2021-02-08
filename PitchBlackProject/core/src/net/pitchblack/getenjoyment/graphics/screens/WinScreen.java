package net.pitchblack.getenjoyment.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.helpers.SoundManager;

public class WinScreen implements Screen {
	private PitchBlackGraphics parent;
	private Stage stage;
	
	public WinScreen (PitchBlackGraphics x) {
		parent = x;
		stage = new Stage(new ScreenViewport());
		final SoundManager sound = new SoundManager();
		Table table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background/WinScreenBackground.png"))));
		table.setFillParent(true);
		table.setDebug(false);
		
		stage.addActor(table);
		
		Skin skin = new Skin(Gdx.files.internal("skin 4/flat-earth-ui.json"));
		final TextButton backButton = new TextButton("Back", skin); // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
		
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.setScreen(new WelcomeScreen(parent));
				if(PreferencesManager.isSoundEnabled() == true) {
					sound.setVolume(PreferencesManager.getSoundVolume());
					sound.play( PitchBlackSound.CLICK );
				}
			}
		});
		
		stage.addActor(backButton);
		table.add(backButton);
	}
	
	
	
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
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
		stage.dispose();
	}

}

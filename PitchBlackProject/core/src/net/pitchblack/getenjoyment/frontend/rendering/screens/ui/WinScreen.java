package net.pitchblack.getenjoyment.frontend.rendering.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.pitchblack.getenjoyment.frontend.rendering.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.rendering.PitchBlackGraphics.Screens;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;

public class WinScreen implements Screen {
	private PitchBlackGraphics parent;
	private Stage stage;
	
	public WinScreen (PitchBlackGraphics p) {
		parent = p;
		stage = new Stage(new ScreenViewport());
		final SoundManager sound = new SoundManager();
		Table table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(parent.pbAssetManager.getAsset(PBAssetManager.winBackground))));
		table.setFillParent(true);
		table.setDebug(false);
		
		stage.addActor(table);

		Skin skin = parent.pbAssetManager.getAsset(PBAssetManager.screenSkin);
		final TextButton backButton = new TextButton("Back to Menu", skin); // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
		
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(Screens.MENU);
				if(PreferencesManager.isSoundEnabled()) {
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
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {	}

	@Override
	public void resume() {	}

	@Override
	public void hide() {	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

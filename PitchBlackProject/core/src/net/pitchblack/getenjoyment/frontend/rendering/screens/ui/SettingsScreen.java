package net.pitchblack.getenjoyment.frontend.rendering.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.rendering.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.helpers.MusicManager;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;
import net.pitchblack.getenjoyment.frontend.helpers.MusicManager.PitchBlackMusic;

public class SettingsScreen implements Screen  {

	private final PitchBlackGraphics parent;
	private Stage stage;

	public SettingsScreen(PitchBlackGraphics p) {
		this.parent = p;
		stage = new Stage(new ScreenViewport());
		final SoundManager sound = new SoundManager();
		final MusicManager music = new MusicManager();

		Table table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(p.pbAssetManager.getAsset(PBAssetManager.menuBackgroundClear))));
		table.setFillParent(true);
		table.setDebug(false);

		stage.addActor(table);
		Skin skin = p.pbAssetManager.getAsset(PBAssetManager.screenSkin);

		BitmapFont gameFont = new BitmapFont(Gdx.files.internal("font/game.fnt"));
		Label.LabelStyle labelStyle = new Label.LabelStyle(gameFont, null);
		BitmapFont gameTitle = new BitmapFont(Gdx.files.internal("font/title.fnt"));
		Label.LabelStyle labelStyle2 = new Label.LabelStyle(gameTitle, null);

		Label titleLabel = new Label("SETTINGS", labelStyle2);
		Label volumeMusicLabel = new Label( "Music Volume", labelStyle);
		Label volumeSoundLabel = new Label( "Sound Volume", labelStyle);
		Label musicOnOffLabel = new Label( "Music", labelStyle);
		Label soundOnOffLabel = new Label( "Sound", labelStyle);

		stage.addActor(titleLabel);
		stage.addActor(volumeMusicLabel);
		stage.addActor(volumeSoundLabel);
		stage.addActor(musicOnOffLabel);
		stage.addActor(soundOnOffLabel);

		//Sliders
		final Slider volumeMusicSlider = new Slider( 0f, 1f, 0.25f,false, skin );
		volumeMusicSlider.setValue(PreferencesManager.getMusicVolume());
		volumeMusicSlider.addListener( new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				music.setVolume(volumeMusicSlider.getValue());
				PreferencesManager.setMusicVolume(volumeMusicSlider.getValue());

			}
		});

		final Slider soundMusicSlider = new Slider(0f, 1f, 0.25f,false, skin );
		soundMusicSlider.setValue(PreferencesManager.getSoundVolume());
		soundMusicSlider.addListener( new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				sound.setVolume(soundMusicSlider.getValue());
				PreferencesManager.setSoundVolume(soundMusicSlider.getValue());
			}
		});

		//CheckBox
		final CheckBox musicCheckbox = new CheckBox(null, skin);
		musicCheckbox.setChecked( PreferencesManager.isMusicEnabled() );
		musicCheckbox.addListener( new EventListener() {
			@Override
			public boolean handle(Event event) {
				boolean enabled = musicCheckbox.isChecked();
				PreferencesManager.setMusicEnabled( enabled );
				PreferencesManager.isMusicEnabled();
				if (enabled == false) {
					PreferencesManager.setMusicEnabled(false);
					MusicManager.stop();
				} else {
					MusicManager.getInstance().play( PitchBlackMusic.MENU);
				}
				return false;
			}
		});

		final CheckBox soundEffectsCheckbox = new CheckBox(null, skin);
		soundEffectsCheckbox.setChecked( PreferencesManager.isSoundEnabled() );
		soundEffectsCheckbox.addListener( new EventListener() {
			@Override
			public boolean handle(Event event) {
				boolean enabled = soundEffectsCheckbox.isChecked();
				PreferencesManager.setSoundEnabled( enabled );
				PreferencesManager.isSoundEnabled();
				if(!enabled) {
					PreferencesManager.setSoundEnabled(false);
				}
				return false;
			}
		});

		// return to main screen button
		final TextButton backButton = new TextButton("Back", skin); // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(PitchBlackGraphics.Screens.MENU);
				if(PreferencesManager.isSoundEnabled() == true) {
					sound.setVolume(PreferencesManager.getSoundVolume());
					sound.play( PitchBlackSound.CLICK );
				}
			}
		});

		stage.addActor(musicCheckbox);
		stage.addActor(soundEffectsCheckbox);
		stage.addActor(backButton);

		table.add(titleLabel).colspan(2);
		table.row();
		table.add(volumeMusicLabel).left();
		table.add(volumeMusicSlider).padTop(5).padLeft(5);
		table.row();
		table.add(musicOnOffLabel).left();
		table.add(musicCheckbox);
		table.row();
		table.add(volumeSoundLabel).left();
		table.add(soundMusicSlider).padTop(5).padLeft(5);
		table.row();
		table.add(soundOnOffLabel).left();
		table.add(soundEffectsCheckbox);
		table.row();
		table.add(backButton).colspan(2).padTop(20);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
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

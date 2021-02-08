package net.pitchblack.getenjoyment.graphics.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics.Screens;
import net.pitchblack.getenjoyment.helpers.MusicManager;
import net.pitchblack.getenjoyment.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.helpers.SoundManager;
import net.pitchblack.getenjoyment.helpers.MusicManager.PitchBlackMusic;

public class SettingsScreen implements Screen  {
	
	private final PitchBlackGraphics parent;
	private Stage stage;
	
	public SettingsScreen(PitchBlackGraphics p) {
		this.parent = p;
		stage = new Stage(new ScreenViewport());
		final SoundManager sound = new SoundManager();
		final MusicManager music = new MusicManager();
		
		Table table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background/backgroundDark.jpg"))));
		table.setFillParent(true);
		table.setDebug(false);
		
		stage.addActor(table);
		Skin skin = new Skin(Gdx.files.internal("skin 4/flat-earth-ui.json"));
		Skin fontSkin = new Skin (Gdx.files.internal("skin/glassy-ui.json"));
		
		Label titleLabel = new Label( "SETTINGS", fontSkin);
		Label volumeMusicLabel = new Label( "Music Volume", fontSkin);
		Label volumeSoundLabel = new Label( "Sound Volume", fontSkin);
		Label musicOnOffLabel = new Label( "Music", fontSkin);
		Label soundOnOffLabel = new Label( "Sound", fontSkin);
			
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
			
			final Slider soundMusicSlider = new Slider( 0f, 1f, 0.25f,false, skin );
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
			       	if(enabled == false) {
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
					parent.changeScreen(Screens.MENU);
					if(PreferencesManager.isSoundEnabled() == true) {
						sound.setVolume(PreferencesManager.getSoundVolume());
						sound.play( PitchBlackSound.CLICK );
					}
				}
			});
			
			stage.addActor(musicCheckbox);
			stage.addActor(soundEffectsCheckbox);
			stage.addActor(backButton);

			table.row().pad(10, 0, 10, 0); 
			table.add(volumeMusicLabel).left();
			table.add(volumeMusicSlider);
			table.row();
			table.add(musicOnOffLabel).left();
			table.add(musicCheckbox);
			table.row().pad(10, 0, 10, 0); 
			table.add(volumeSoundLabel).left();
			table.add(soundMusicSlider);
			table.row();
			table.add(soundOnOffLabel).left();
			table.add(soundEffectsCheckbox);
			table.row().pad(10, 0, 10, 0); 
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

package net.pitchblack.getenjoyment.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.pitchblack.getenjoyment.helpers.*;
import net.pitchblack.getenjoyment.helpers.MusicManager.PitchBlackMusic;



import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics.Screens;

public class WelcomeScreen implements Screen {
	
	public PitchBlackGraphics parent;
	private Stage stage;

	SpriteBatch batch;
	Texture title;
	
	public WelcomeScreen(PitchBlackGraphics hGT) {
		parent = hGT;	
		stage = new Stage(new ScreenViewport());  // stage relates to a controller which will react to user inputs
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // makes stage to react to user input by checking each frame, as specified by the smallest time from 0.03s or deltaTime(?)
		stage.draw();  // draws items held within it, ie buttons

		Table table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background/background.png"))));
		table.setFillParent(true);
		table.setDebug(false);
		
		stage.addActor(table);// gives stage the things to draw
		
		final SoundManager sound = new SoundManager();
		
		if(PreferencesManager.isMusicEnabled() == true) {
			MusicManager.getInstance().play( PitchBlackMusic.MENU);
		}
	
	    Skin skin = new Skin(Gdx.files.internal("skin 4/flat-earth-ui.json"));

		TextButton newGame = new TextButton("New Game", skin);
		TextButton exit = new TextButton("Exit", skin);
		final TextButton settings = new TextButton("Settings", skin);
		TextButton lobby = new TextButton("Lobby", skin);
		TextButton credits = new TextButton("Credits", skin);

		stage.addActor(newGame);
		stage.addActor(exit);
		stage.addActor(settings);
		stage.addActor(lobby);
		stage.addActor(credits);

		table.row().pad(200, 0, 0, 0); 
		table.add(newGame).fillX().uniformX();  // adds newGame button and sets it to fill the x axis + to make it uniform w/ other cells
		table.row().pad(10, 0, 10, 0);  // next cells are added to a new row + padding between buttons (top, left, bottom, right)
		table.add(lobby).fillX().uniformX();
		table.row();
		table.add(settings).fillX().uniformX();
		table.row().pad(10, 0, 10, 0);
		table.add(credits).fillX().uniformX();
		table.row();
		table.add(exit).fillX().uniformX();

		exit.addListener(new ChangeListener() {  // adds a new listener for user input in the exit button
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();				
			}
		});
		
		newGame.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(Screens.GAME);
				if(PreferencesManager.isSoundEnabled() == true) {
					sound.setVolume(PreferencesManager.getSoundVolume());
					sound.play( PitchBlackSound.CLICK );
				}
			}
		});
		
		settings.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(Screens.CREDITS);
				if(PreferencesManager.isSoundEnabled() == true) {
					sound.setVolume(PreferencesManager.getSoundVolume());
					sound.play( PitchBlackSound.CLICK );
					}
			}
		});
		
		
//		credits.addListener(new ChangeListener() {
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				parent.setScreen(new CreditsScreen(parent));
//				if(PreferencesManager.isSoundEnabled() == true) {
//					sound.setVolume(PreferencesManager.getSoundVolume());
//					sound.play( PitchBlackSound.CLICK );
//					}
//			}
//		});
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		//batch = new SpriteBatch();
		//title = new Texture(Gdx.files.internal("title.png"));
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.act();
		stage.draw();
		
		//batch.begin();
		//batch.draw(title, -525 ,125);
		//batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);  // updates window size so elements can also be resized
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
		//dispose();

	}

	@Override
	public void dispose() {
		stage.dispose();
		//music.dispose();
		//buttonPress.dispose();
	}

}

package net.pitchblack.getenjoyment.frontend.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.game.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.helpers.MusicManager;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;
import net.pitchblack.getenjoyment.frontend.helpers.MusicManager.PitchBlackMusic;
import net.pitchblack.getenjoyment.frontend.client.Client;
import net.pitchblack.getenjoyment.frontend.client.Client.AccountState;

public class MenuScreen implements Screen {

    public PitchBlackGraphics parent;
    private Client client;
    private Stage stage;

    //private final TextButton newGame;
    private final TextButton exit;
    private final TextButton settings;
    private final TextButton lobby;
    private final TextButton credits;

    public MenuScreen(PitchBlackGraphics p, Client client) {
        this.parent = p;
        this.client = client;

        stage = new Stage(new ScreenViewport());  // stage relates to a controller which will react to user inputs
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // makes stage to react to user input by checking each frame, as specified by the smallest time from 0.03s or deltaTime(?)
        stage.draw();  // draws items held within it, ie buttons

        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(parent.pbAssetManager.getAsset(PBAssetManager.menuBackground))));
        table.setFillParent(true);
        table.setDebug(false);

        stage.addActor(table); // gives stage the things to draw

        final SoundManager sound = new SoundManager();

        if (PreferencesManager.isMusicEnabled()) {
            MusicManager.getInstance().play(PitchBlackMusic.MENU);
        }

        Skin skin = parent.pbAssetManager.getAsset(PBAssetManager.screenSkin);

        //newGame = new TextButton("New Game", skin);
        exit = new TextButton("Exit", skin);
        settings = new TextButton("Settings", skin);
        lobby = new TextButton("Lobby", skin);
        credits = new TextButton("Credits", skin);

        //stage.addActor(newGame);
        stage.addActor(exit);
        stage.addActor(settings);
        stage.addActor(lobby);
        stage.addActor(credits);

        table.row().pad(200, 0, 0, 0);
        table.add(lobby).fillX().uniformX();  // adds newGame button and sets it to fill the x axis + to make it uniform w/ other cells
        table.row().pad(10, 0, 10, 0);  // next cells are added to a new row + padding between buttons (top, left, bottom, right)
        table.add(settings).fillX().uniformX();
        table.row();
        table.add(credits).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(exit).fillX().uniformX();

        exit.addListener(new ChangeListener() {  // adds a new listener for user input in the exit button
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

//		newGame.addListener(new ChangeListener() {
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				parent.changeScreen(Screens.GAME);
//				if(PreferencesManager.isSoundEnabled() == true) {
//					sound.setVolume(PreferencesManager.getSoundVolume());
//					sound.play( PitchBlackSound.CLICK );
//				}
//			}
//		});

        lobby.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(PitchBlackGraphics.Screens.LOBBY);
                if (PreferencesManager.isSoundEnabled()) {
                    sound.setVolume(PreferencesManager.getSoundVolume());
                    sound.play(PitchBlackSound.CLICK);
                }
            }
        });

        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(PitchBlackGraphics.Screens.SETTINGS);
                if (PreferencesManager.isSoundEnabled()) {
                    sound.setVolume(PreferencesManager.getSoundVolume());
                    sound.play(PitchBlackSound.CLICK);
                }
            }
        });

		credits.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor){
                parent.changeScreen(PitchBlackGraphics.Screens.CREDITS);
                if (PreferencesManager.isSoundEnabled() == true) {
                    sound.setVolume(PreferencesManager.getSoundVolume());
                    sound.play(PitchBlackSound.CLICK);
                }
            }
        });
}
	
	private void setButtonTouchable(Touchable touchable) {
		//newGame.setTouchable(touchable);
		lobby.setTouchable(touchable);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(client.getAccountState() != AccountState.LOGGED_IN) {
			setButtonTouchable(Touchable.disabled);
		}
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);  // updates window size so elements can also be resized
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

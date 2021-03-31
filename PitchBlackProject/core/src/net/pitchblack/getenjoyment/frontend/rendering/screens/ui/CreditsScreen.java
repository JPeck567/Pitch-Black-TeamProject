package net.pitchblack.getenjoyment.frontend.rendering.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.pitchblack.getenjoyment.frontend.rendering.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;

public class CreditsScreen implements Screen {
	private PitchBlackGraphics parent;
	private Stage stage;
	private SoundManager sound;
	
	private Table table;
	private Skin skin;
	private Skin fontSkin;

	public CreditsScreen(PitchBlackGraphics p) {
		parent = p;
		stage = new Stage(new ScreenViewport());
		sound = new SoundManager();	
		skin = new Skin(Gdx.files.internal("skin_2/flat-earth-ui.json"));
		fontSkin = new Skin (Gdx.files.internal("skin/glassy-ui.json"));

		BitmapFont gameFont = new BitmapFont(Gdx.files.internal("font/game.fnt"));
		BitmapFont gameFont2 = new BitmapFont(Gdx.files.internal("font/sbold.fnt"));
		BitmapFont gameTitle = new BitmapFont(Gdx.files.internal("font/title.fnt"));
		Label.LabelStyle labelStyle = new Label.LabelStyle(gameFont, null);
		Label.LabelStyle labelStyle2 = new Label.LabelStyle(gameFont2, null);
		Label.LabelStyle labelStyle3 = new Label.LabelStyle(gameTitle, null);

		table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background/backgroundDark.jpg"))));
		table.setFillParent(true);
		table.setDebug(false);
		stage.addActor(table);	
		
		final TextButton backButton = new TextButton("Back", skin);
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
		
		stage.addActor(backButton);
		  
		Label credits = new Label("Credits", labelStyle3);
		stage.addActor(credits);
		
		Label graphics = new Label("Graphics:", labelStyle2);
		stage.addActor(graphics);
		Label graphicsP = new Label("Jorge, Nav, Sam", labelStyle);
		stage.addActor(graphicsP);
		
		Label sound = new Label("Sound:", labelStyle2);
		stage.addActor(sound);
		Label soundP = new Label("Nav", labelStyle);
		stage.addActor(soundP);		
		
		Label logic = new Label("Logic:", labelStyle2); 
		stage.addActor(logic);
		Label logicP = new Label("Jorge, Joe", labelStyle); 
		stage.addActor(logicP);
		
		Label networking = new Label("Networking:", labelStyle2); 
		stage.addActor(networking);
		Label networkingP = new Label("Jorge, Alex", labelStyle); 
		stage.addActor(networkingP);
		
		Label login = new Label("Login/Registration:", labelStyle2); 
		stage.addActor(login);
		Label loginP = new Label("Ryan, Rayhaan, Alex", labelStyle); 
		stage.addActor(loginP);
		
		Label website = new Label("Website:", labelStyle2); 
		stage.addActor(website);
		Label websiteP = new Label("Eryk, Joe", labelStyle); 
		stage.addActor(websiteP);
		
		Label testing = new Label("Testing:", labelStyle2); 
		stage.addActor(testing);
		Label testingP = new Label("Hongyi", labelStyle); 
		stage.addActor(testingP);
		
		Label deployment = new Label("Deployment:", labelStyle2); 
		stage.addActor(testing);
		Label deploymentP = new Label("Faisal", labelStyle); 
		stage.addActor(testingP);
		
		table.add(credits).colspan(2);
		table.row();
		table.add(graphics).align(Align.left).padTop(25);
		table.add(login).align(Align.left).padLeft(35).padTop(25);
		table.row();
		table.add(graphicsP).align(Align.left);
		table.add(loginP).align(Align.left).padLeft(35);
		table.row();
		table.add(sound).align(Align.left).padTop(5);
		table.add(website).align(Align.left).padLeft(35);
		table.row();
		table.add(soundP).align(Align.left);
		table.add(websiteP).align(Align.left).padLeft(35);
		table.row();
		table.add(logic).align(Align.left).padTop(5);
		table.add(testing).align(Align.left).padLeft(35);
		table.row();
		table.add(logicP).align(Align.left);
		table.add(testingP).align(Align.left).padLeft(35);
		table.row();
		table.add(networking).align(Align.left).padTop(5);
		table.add(deployment).align(Align.left).padLeft(35);
		table.row();
		table.add(networkingP).align(Align.left);
		table.add(deploymentP).align(Align.left).padLeft(35);
		table.row();
		table.add(backButton).colspan(2).padTop(25);

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
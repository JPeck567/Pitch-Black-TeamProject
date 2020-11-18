package net.pitchblack.getenjoyment.graphics.screens;

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

import net.pitchblack.getenjoyment.graphics.PitchBlackGraphicsTest;

public class WelcomeScreen implements Screen {
	
	private PitchBlackGraphicsTest parent;
	private Stage stage;

	public WelcomeScreen(PitchBlackGraphicsTest hGT) {
		parent = hGT;
		
		stage = new Stage(new ScreenViewport());  // stage relates to a controller which will react to user inputs
		Gdx.input.setInputProcessor(stage);  // sets the top-level framework to direct inputs to the stage for further processing
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));  // makes stage to react to user input by checking each frame, as specified by the smallest time from 0.03s or deltaTime(?)
		stage.draw();  // draws items held within it, ie buttons
	}

	@Override
	public void show() {  // called when this screen becomes the current screen
		// the creation of a table. this will contain elements, held in a spreadsheet type manner, which also include data on positioning for the display
		Table table = new Table();
		table.setFillParent(true);
		table.setDebug(true);
		
		stage.addActor(table);  // gives stage the things to draw
		
		Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
		 
		TextButton newGame = new TextButton("New Game", skin);
		TextButton exit = new TextButton("Exit", skin);
		
		table.add(newGame).fillX().uniformX();  // adds newGame button and sets it to fill the x axis + to make it uniform w/ other cells
		table.row().pad(10, 0, 10, 0);  // next cells are added to a new row + padding between buttons (top, left, bottom, right)
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
				parent.changeScreen(PitchBlackGraphicsTest.GAME);
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
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

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

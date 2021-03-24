package net.pitchblack.getenjoyment.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud implements Disposable {
	
	
	private Stage stage;
	private Viewport viewport;
	
	//to hold the time values
	private Integer timer;
	private float time;
	
	//variables to hold the label to be added onto the screen
	private Label timerLabel;
	private Label timeLabel;
	private Label blankLabel;
	
	public Hud(SpriteBatch sb) {
		timer = 0;
		time = 0;
		
		//setting the size of screen for the stage
		viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
		stage = new Stage(viewport, sb);
		
		//table is used to hold the labels, and is added tp the stage
		Table table = new Table();
		table.top();
		table.setFillParent(true);
		
		//sets the values of the labels
		timerLabel = new Label(String.format("%05d", timer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		timeLabel.scaleBy(1.5f, 1.5f);
		blankLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		
		//labels are added to the table to show on the screen
		//blank labels are added to sort the layout
		table.add(blankLabel).expandX().padTop(5);
		table.add(blankLabel).expandX().padTop(5);
		table.add(timeLabel).expandX().padTop(5);
		table.row();
		table.add(blankLabel).expandX();
		table.add(blankLabel).expandX();
		table.add(timerLabel).expandX();		
		stage.addActor(table);
	}

	public void draw(){
		stage.draw();
	}
	
	/*
	 * update the timer variable altering the time shown on the screen
	 */
	public void update(float delta) {
		time += delta;
		if(time > 1) {
			timer++;
			timerLabel.setText(String.format("%03d", timer));
		}	
	}
		
	@Override
	public void dispose() {
		stage.dispose();		
	}

	public Camera getCamera() {
		return stage.getCamera();
	}

	public Stage getStage() {
		return stage;
	}
}

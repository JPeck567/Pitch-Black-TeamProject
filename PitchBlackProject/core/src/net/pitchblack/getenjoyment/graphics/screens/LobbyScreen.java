package net.pitchblack.getenjoyment.graphics.screens;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.badlogic.gdx.utils.viewport.Viewport;
import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics.Screens;
import net.pitchblack.getenjoyment.helpers.PBAssetManager;
import net.pitchblack.getenjoyment.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.helpers.SoundManager;

public class LobbyScreen implements Screen {
	private PitchBlackGraphics parent;
	private Client client;
	private HashMap<String, RoomUIInformation> roomMap;
	private ArrayList<String> playersList;
	private RoomUIInformation roomViewed; // if 0, lobby

	private SoundManager sound;

	private Skin skin;
	private Skin fontSkin;

	private Viewport screenViewport;
	private Stage allRoomsStage;
	private Table allRoomsTable;
	private Stage roomStage;
	private Table roomTable;

	private Label roomTitle;
	private Label capacityTakenTitle;

	private TextArea currentPlayersInRoomTextArea;

	private TextButton backButton;
	private TextButton joinButton;

	public LobbyScreen(PitchBlackGraphics p, final Client client) {
		this.parent = p;
		this.client = client;
        roomMap = null;
		roomViewed = null;

		sound = SoundManager.getInstance();

		skin = parent.pbAssetManager.getAsset(PBAssetManager.screenSkin);
		fontSkin = new Skin (Gdx.files.internal("skin_2/flat-earth-ui.json"));

		screenViewport = new ScreenViewport();
		allRoomsStage = new Stage();
		allRoomsTable = new Table();
		roomStage = new Stage();
		roomTable = new Table();

		roomTitle = new Label("Room", skin,"title");
		capacityTakenTitle = new Label("Capacity", skin, "title");
        currentPlayersInRoomTextArea = null;

		backButton = new TextButton("Back", skin);
		backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(Screens.MENU);
                if(PreferencesManager.isSoundEnabled()) {
                    sound.setVolume(PreferencesManager.getSoundVolume());
                    sound.play(PitchBlackSound.CLICK);
                }
            }
        });

		joinButton = new TextButton("Join Room", skin);
        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                client.emitJoinRoomRequest(roomViewed.getRoomName());
            }
        });

		client.emitJoinLobby(); // to get polled with new players joining rooms
		client.emitGetRooms(); // Will populate rooms
	}

    private void createAllRoomsScene() {
        //Create Table
        allRoomsTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background/backgroundDark.jpg"))));
        allRoomsTable.setFillParent(true);
        allRoomsTable.setDebug(false);
        allRoomsStage.addActor(allRoomsTable);

        allRoomsTable.add(roomTitle);
		allRoomsTable.add(capacityTakenTitle);
        allRoomsTable.row();

        // all buttons + labels added to table and stage as actor
		// for each room it does to table the following
		// 		.add(roomTextButton);
		//		.add(roomInfo);
		//		.row()
        for(RoomUIInformation roomInfo : roomMap.values()){
			TextButton roomTextButton = roomInfo.getRoomTextButton();
			addListenerToViewRoomButton(roomTextButton, roomInfo.getRoomName());
            allRoomsStage.addActor(roomTextButton);

            allRoomsTable.add(roomTextButton).align(Align.left);
            allRoomsTable.add(roomInfo.getCapacityTakenLabel());
            allRoomsTable.row();
        }

        allRoomsStage.addActor(backButton);
        allRoomsTable.row();
        allRoomsTable.add(backButton);
    }

    private void createRoomTable(){
	    roomTable = new Table();

		roomTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background/backgroundDark.jpg"))));
		roomTable.setFillParent(true);
		roomTable.setDebug(false);
		roomStage.addActor(roomTable);
		roomStage.addActor(joinButton);

		roomTable.add(currentPlayersInRoomTextArea);
		roomTable.row();
		roomTable.add(joinButton);
	}

    private void addListenerToViewRoomButton(TextButton button, final String roomName) {
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(roomStage);
				roomViewed = roomMap.get(roomName);
				currentPlayersInRoomTextArea = roomViewed.getPlayersInRoomTextArea();
				createRoomTable();
			}
		});
	}

	public void addRoomData(HashMap<String, ArrayList<String>> roomUsersMap) {
		roomMap = new HashMap<String, RoomUIInformation>();
        for(String roomName : roomUsersMap.keySet()){
            roomMap.put(roomName, new RoomUIInformation(roomName, roomUsersMap.get(roomName), skin, fontSkin));
        }
        Gdx.app.postRunnable(new Runnable() {  // requires openGL context from libGDX render thread
            @Override
            public void run() {
                createAllRoomsScene();
            }
        });
	}

	public void joinRoomResponse(Boolean joined, String room, String message) {
		if(joined){
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public void addNewPlayer(String username, String room) {
		System.out.println(username + " joined room " + room);
		roomMap.get(room).addPlayer(username);
	}

	public boolean ready() {
		return true;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(allRoomsStage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(roomMap != null) {
            if (roomViewed == null) {  // meaning not viewing room
                allRoomsStage.act();
                allRoomsStage.draw();
            } else {  // meaning viewing room
                roomStage.act();
                roomStage.draw();
            }
        } else {
		    // show loading screen ?
        }
	}

	@Override
	public void resize(int width, int height) {
		allRoomsStage.getViewport().update(width, height, true);
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
		allRoomsStage.dispose();
	}
}

package net.pitchblack.getenjoyment.frontend.game.screens;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.pitchblack.getenjoyment.frontend.client.Client;
import net.pitchblack.getenjoyment.frontend.game.PitchBlackGraphics;
import net.pitchblack.getenjoyment.PBAssetManager;
import net.pitchblack.getenjoyment.frontend.helpers.PitchBlackSound;
import net.pitchblack.getenjoyment.frontend.helpers.PreferencesManager;
import net.pitchblack.getenjoyment.frontend.helpers.SoundManager;

public class LobbyScreen implements Screen {
	private PitchBlackGraphics parent;
	private Client client;
	private HashMap<String, RoomUIInformation> roomMap;
	private ArrayList<String> playersList;
	private boolean roomMapLoaded;
	private RoomUIInformation roomViewed; // if 0, lobby

	private SoundManager sound;

	private Skin skin;
	private Label.LabelStyle fontSkin;

	private Stage currentStage;
	private Stage allRoomsStage;
	private Table allRoomsTable;
	private Stage roomStage;
	private Table roomTable;

	private Label roomHeadingTitle;
	private Label capacityHeadingTitle;

	private Label currentPlayersInRoomTextArea;

    private TextButton backToMenuButton;
    private TextButton backToAllRoomsButton;
	private TextButton joinButton;

	public LobbyScreen(PitchBlackGraphics p, final Client client) {
		this.parent = p;
		this.client = client;
        roomMap = null;
        roomMapLoaded = false;
		roomViewed = null;

		sound = SoundManager.getInstance();

		skin = parent.pbAssetManager.getAsset(PBAssetManager.screenSkin);
		fontSkin = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("font/game.fnt")), null);

		allRoomsStage = new Stage();
		allRoomsTable = new Table();
		roomStage = new Stage();
		roomTable = new Table();

		currentStage = allRoomsStage;

		roomHeadingTitle = new Label("Room", skin,"title");
		capacityHeadingTitle = new Label("Capacity", skin, "title");
        currentPlayersInRoomTextArea = new Label("Placeholder as room not selected", fontSkin);

        backToMenuButton = new TextButton("Back", skin);
        backToAllRoomsButton = new TextButton("Back", skin);
        joinButton = new TextButton("Join Room", skin);
        setupButtonListeners();

        client.emitJoinLobby();
        client.emitGetRooms();
	}

    private void setupButtonListeners() {
        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
				if(PreferencesManager.isSoundEnabled()) {
					sound.setVolume(PreferencesManager.getSoundVolume());
					sound.play(PitchBlackSound.CLICK);
				}
				parent.changeScreen(PitchBlackGraphics.Screens.MENU);
			}
        });

        backToAllRoomsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentStage = allRoomsStage;
				roomViewed = null;
				Gdx.input.setInputProcessor(currentStage);
			}
		});

        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	joinButton.setTouchable(Touchable.disabled);
                client.emitJoinRoomRequest(roomViewed.getRoomName());
            }
        });
    }

    public void restartLobby(){
	    //roomMapLoaded = false;
        roomViewed = null;
        currentStage = allRoomsStage;
        backToAllRoomsButton.setVisible(true);
        joinButton.setTouchable(Touchable.enabled);
        client.emitGetRooms();
    }

    private void createAllRoomsScene() {
        //Create Table
        allRoomsTable.setBackground(new TextureRegionDrawable(new TextureRegion(parent.pbAssetManager.getAsset(PBAssetManager.menuBackgroundClear))));
        allRoomsTable.setFillParent(true);
        allRoomsTable.setDebug(false);
        allRoomsStage.addActor(allRoomsTable);
        allRoomsStage.addActor(backToMenuButton);

        allRoomsTable.add(roomHeadingTitle).padRight(60);
		allRoomsTable.add(capacityHeadingTitle);
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

        allRoomsTable.row();
        allRoomsTable.add(backToMenuButton).center();
    }

    private void createRoomScene(){
		roomTable.setBackground(new TextureRegionDrawable(new TextureRegion(parent.pbAssetManager.getAsset(PBAssetManager.menuBackgroundClear))));
		roomTable.setFillParent(true);
		roomTable.setDebug(false);

		roomStage.addActor(currentPlayersInRoomTextArea);
		roomStage.addActor(roomTable);
		roomStage.addActor(joinButton);

		roomTable.add(currentPlayersInRoomTextArea).colspan(2).padBottom(25);
		//roomTable.add(currentPlayersInRoomTextArea).pad(0, 30, 0, 0);
		roomTable.row();
		roomTable.add(backToAllRoomsButton).padRight(40);
		roomTable.add(joinButton);
	}

    private void addListenerToViewRoomButton(TextButton button, final String roomName) {
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			    RoomUIInformation roomUI = roomMap.get(roomName);
			    roomViewed = roomUI;
                currentStage = roomStage;
                updateRoomTextArea();
                Gdx.input.setInputProcessor(currentStage);
			}
		});
	}

	private void updateRoomTextArea() {
		if(roomViewed != null) {
			Cell c = roomTable.getCell(currentPlayersInRoomTextArea);
			currentPlayersInRoomTextArea = roomViewed.getPlayersInRoomTextArea();
			c.setActor(currentPlayersInRoomTextArea);
		}
	}

	// sets up room map with players in room
	public void addRoomData(HashMap<String, ArrayList<String>> roomUsersMap) {
		if(roomMap == null){ // if not loaded before, make new object. implies scenes not created before
		    roomMap = new HashMap<String, RoomUIInformation>();
            for(String roomName : roomUsersMap.keySet()){
                roomMap.put(roomName, new RoomUIInformation(roomName, roomUsersMap.get(roomName), skin,  fontSkin));
            }

            Gdx.app.postRunnable(new Runnable() {  // requires openGL context from libGDX render thread
                @Override
                public void run() {  // create scenes required for lobby view
                    createAllRoomsScene();
                    createRoomScene();
                    roomMapLoaded = true;
                }
            });
        } else {  // if loaded previously
            for(String roomName : roomUsersMap.keySet()){
                roomMap.get(roomName).addAllPlayers(roomUsersMap.get(roomName));
            }
            roomMapLoaded = true;
        }
	}

	public void joinRoomResponse(Boolean joined, String room, String message) {
		if(joined){
			RoomUIInformation roomInfo = roomMap.get(room);
			backToAllRoomsButton.setVisible(false);  // no going back now
			addNewPlayer(client.getUsername(), room);
			updateRoomTextArea();
		} else {
			joinButton.setTouchable(Touchable.enabled);
			System.out.println(message);
		}
	}

	public void addNewPlayer(String username, String room) {
		System.out.println(username + " joined room " + room);
		roomMap.get(room).addPlayer(username);
	}

    public void removePlayer(String username, String room) {
        System.out.println(username + " left room " + room);
        roomMap.get(room).removePlayer(username);
    }

	public void setRoomInSession(String room) {
		roomMap.get(room).setToInSession();
	}

    public void resetRoom(String room) {
        roomMap.get(room).resetRoom();
    }

	public boolean ready() {
		return true;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(currentStage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);  // clears screen each frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(roomMapLoaded) {
			if(roomViewed == null){
				allRoomsStage.act();
				allRoomsStage.draw();
			} else {
				if(roomViewed.isInSession()){
					if(joinButton.isVisible()) joinButton.setVisible(false);
				} else {
					if(!joinButton.isVisible()) joinButton.setVisible(true);
				}
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
		roomStage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {	}

	@Override
	public void resume() {	}

	@Override
	public void hide() {	}

	@Override
	public void dispose() {
		allRoomsStage.dispose();
		roomStage.dispose();
	}
}

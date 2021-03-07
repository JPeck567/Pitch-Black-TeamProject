package net.pitchblack.getenjoyment.graphics.screens;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Screen;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;

public class LobbyScreen implements Screen {
	private PitchBlackGraphics parent;
	private Client client;
	private HashMap<String, Object> roomMap;

	public LobbyScreen(PitchBlackGraphics parent, Client client) {
		this.parent = parent; // TODO: if leaving lobby, let client know
		this.client = client;
		roomMap = new HashMap<String, Object>();
		
		client.setLobbyScreen(this);

		//client.emitGetRooms();
		client.emitJoinRoomRequest("1");
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		if (client.isInLobby()) {
			// render screen
		} else {
			// render room
		}

	}

	public GameScreen getGameScreen() {
		return parent.getGameScreen();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
	
	public void addRoomData(HashMap<String, Object> roomUsersMap) {
		roomMap = roomUsersMap;
		System.out.println(roomMap.toString());
	}
	
	public void joinSuccess(String room, String message) {
		System.out.println(room + message);
	}
	
	public void addNewPlayer(String username, String room) {
		
	}

	public boolean ready() {
		return true;
	}
}

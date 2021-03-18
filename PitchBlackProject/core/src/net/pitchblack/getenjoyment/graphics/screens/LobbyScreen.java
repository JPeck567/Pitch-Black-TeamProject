package net.pitchblack.getenjoyment.graphics.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.client.Client.ClientState;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;

public class LobbyScreen implements Screen {
	private PitchBlackGraphics parent;
	private Client client;
	private HashMap<String, Object> roomMap;

	public LobbyScreen(PitchBlackGraphics parent, Client client) {
		this.parent = parent; // TODO: if leaving lobby, let client know
		this.client = client;
		roomMap = new HashMap<String, Object>();

		client.emitGetRooms();

		client.emitJoinRoomRequest("1");
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {

//		if (client.isInLobby()) {
//			// render screen
//		} else {
//			// render room
//		}

	}

	public void setupGameScreen(final String playerData, final String fogData, final String mapData) {

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
	
	public void joinRoomResponse(Boolean joined, String room, String message) {
		if(joined){
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}
	
	public void addNewPlayer(String username, String room) {
		
	}

	public boolean ready() {
		return true;
	}



}

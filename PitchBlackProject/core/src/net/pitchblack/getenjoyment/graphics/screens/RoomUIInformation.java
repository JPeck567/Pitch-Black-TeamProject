package net.pitchblack.getenjoyment.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.ArrayList;

public class RoomUIInformation {;
    private final String roomName;
    private final int capacity;
    private boolean sessionInProgress;
    private final ArrayList<String> playersInRoom;

    private final TextButton roomTextButton;
    private final Label capacityTakenLabel;
    private final TextArea playersInRoomTextArea;

    public RoomUIInformation(String roomName, ArrayList<String> playersInRoom, Skin skin, Skin fontSkin){
        this.roomName = roomName;
        this.capacity = 4; // hard coded in, but should be dynamic and client should send the capacity set by game instance
        sessionInProgress = false;
        this.playersInRoom = playersInRoom;

        roomTextButton = new TextButton("Room " + roomName,  skin);
        capacityTakenLabel = new Label("", skin);
        playersInRoomTextArea = new TextArea("", fontSkin);
        playersInRoomTextArea.setDisabled(true);  // so can't write in it
        playersInRoomTextArea.setScale(5);

        updateUIComponents();
    }

    private void updateUIComponents(){
        updatePlayerInRoomTextArea();
        updateCapacityTakenLabel();
    }

    private void updateCapacityTakenLabel(){
        int size = playersInRoom.size();

        if(size < capacity) {
            capacityTakenLabel.setText(size + " / " + capacity);
            roomTextButton.setTouchable(Touchable.enabled);
        } else {
            capacityTakenLabel.setText("Room is full!");
            roomTextButton.setTouchable(Touchable.disabled);
        }
    }

    private void updatePlayerInRoomTextArea() {
        int playersInRoomSize = playersInRoom.size();

        if(playersInRoomSize == 0) {  // no players in room
            playersInRoomTextArea.setText("There are no players in room " + roomName);
        } else {  // update text
            playersInRoomTextArea.setText(getPlayersInRoomString());
        }
    }

    private String getPlayersInRoomString(){
        StringBuilder sb = new StringBuilder();
        for(String playerName : playersInRoom){
            sb.append(playerName).append("\n");
        }
        return sb.toString();
    }

    public void addPlayer(String username){
        playersInRoom.add(username);
        updateUIComponents();
    }

    public void addAllPlayers(ArrayList<String> players){
        playersInRoom.clear();
        playersInRoom.addAll(players);
        updateUIComponents();
    }

    public void removePlayer(String username) {
        playersInRoom.remove(username);
        updateUIComponents();
    }

    public void resetRoom() {
        playersInRoom.clear();
        updateUIComponents();
    }

    public String getRoomName() {
        return roomName;
    }

    public TextButton getRoomTextButton(){
        return roomTextButton;
    }

    public Label getCapacityTakenLabel(){
        return capacityTakenLabel;
    }

    public TextArea getPlayersInRoomTextArea(){
        return playersInRoomTextArea;
    }
}

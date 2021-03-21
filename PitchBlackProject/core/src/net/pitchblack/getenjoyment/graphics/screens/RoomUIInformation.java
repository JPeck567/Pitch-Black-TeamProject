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
    private final ArrayList<String> playersInRoom;

    private final TextButton roomTextButton;
    private final Label capacityTakenLabel;
    private final TextArea playersInRoomTextArea;

    public RoomUIInformation(String roomName, ArrayList<String> playersInRoom, Skin skin, Skin fontSkin){
        this.roomName = roomName;
        this.capacity = 4; // hard coded in, but should be dynamic and client should send the capacity set by game instance
        this.playersInRoom = playersInRoom;

        roomTextButton = new TextButton("",  skin);
        updateRoomTextButton();

        capacityTakenLabel = new Label("", skin);
        updateCapacityTakenLabel();

        playersInRoomTextArea = new TextArea("", fontSkin);
        updatePlayerInRoomTextArea();
    }

    private void updateRoomTextButton(){
        roomTextButton.setText("Room " + roomName);
    }

    private void updateCapacityTakenLabel(){
        int size = playersInRoom.size();

        if(size == capacity) {
            roomTextButton.setTouchable(Touchable.disabled);
            capacityTakenLabel.setText( size + " / " + capacity);
        } else {
            capacityTakenLabel.setText("Room is full!");
        }
    }

    private void updatePlayerInRoomTextArea() {
        if(playersInRoomTextArea.getLines() < playersInRoom.size()){  // if text area out of date
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
        updateRoomTextButton();
        updateCapacityTakenLabel();
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

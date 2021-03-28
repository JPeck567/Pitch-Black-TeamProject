package net.pitchblack.getenjoyment.frontend.helpers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public enum PitchBlackSound {
    CLICK( "sound/Menu_buttons.wav" ),
    JUMP( "sound/Jump.wav" );
    
    private final String fileName;

    private PitchBlackSound( String fileName ){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }
}

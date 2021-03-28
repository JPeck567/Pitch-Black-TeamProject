package net.pitchblack.getenjoyment.frontend.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import net.pitchblack.getenjoyment.frontend.game.PitchBlackGraphics;
import net.pitchblack.getenjoyment.frontend.helpers.LRUCache.CacheEntryRemovedListener;


public class SoundManager implements CacheEntryRemovedListener<PitchBlackSound,Sound>, Disposable{   
	private static volatile SoundManager instance = null;
    private float volume = 1f;
    private boolean enabled = true;
    private final LRUCache<PitchBlackSound, Sound> soundCache;    
    
    private static PitchBlackSound soundBeingPlayed;

    public SoundManager(){
    	soundCache = new LRUCache<PitchBlackSound, Sound>( 10 );
        soundCache.setEntryRemovedListener( this );
    }
    
    public final static SoundManager getInstance() {
    	if (instance == null) {
            synchronized(SoundManager.class) {
              if (instance == null) {
                instance = new SoundManager();
              }
            }
         }                
        return instance;
    }

    public void play(PitchBlackSound sound ){
        if( ! enabled ) return;

        Sound soundToPlay = soundCache.get( sound );
        if( soundToPlay == null ) {
            FileHandle soundFile = Gdx.files.internal( sound.getFileName() );
            soundToPlay = Gdx.audio.newSound( soundFile );
            soundCache.add( sound, soundToPlay );
        }

        Gdx.app.log( PitchBlackGraphics.LOG, "Playing sound: " + sound.name() );
        soundToPlay.play(volume);
    }
    
    public void setVolume(float volume ){
        Gdx.app.log( PitchBlackGraphics.LOG, "Adjusting sound volume to: " + volume );

        if( volume < 0 || volume > 1f ) {
            throw new IllegalArgumentException( "The volume must be inside the range: [0,1]" );
        }
        this.volume = volume;
    }

    public void setEnabled(boolean enabled ){
        this.enabled = enabled;
    }   

    @Override
    public void notifyEntryRemoved( PitchBlackSound key,Sound value ){
        Gdx.app.log( PitchBlackGraphics.LOG, "Disposing sound: " + key.name() );
        value.dispose();
    }

    public void dispose(){
        Gdx.app.log( PitchBlackGraphics.LOG, "Disposing sound manager" );
        for( Sound sound : soundCache.retrieveAll() ) {
            sound.stop();
            sound.dispose();
        }
    }
}

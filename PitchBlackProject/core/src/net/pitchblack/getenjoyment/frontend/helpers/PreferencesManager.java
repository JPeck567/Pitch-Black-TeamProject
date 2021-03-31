package net.pitchblack.getenjoyment.frontend.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager
{
    // constants
    private static final String PREF_MUSIC_VOLUME = "music.volume";
    private static final String PREF_SOUND_VOLUME = "sound.volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREFS_NAME = "pitchBlack";
    private static volatile PreferencesManager instance = null;

    private PreferencesManager(){
    }
    
    public final static PreferencesManager getInstance() {
    	if (instance == null) {
            synchronized(PreferencesManager.class) {
              if (instance == null) {
                instance = new PreferencesManager();
              }
            }
         }                
        return instance;
    }

    public static Preferences getPrefs(){
        return Gdx.app.getPreferences( PREFS_NAME );
    }

    public static boolean isSoundEnabled(){
        return getPrefs().getBoolean( PREF_SOUND_ENABLED, true );
    }

    public static void setSoundEnabled(boolean soundEffectsEnabled ){
        getPrefs().putBoolean( PREF_SOUND_ENABLED, soundEffectsEnabled );
        getPrefs().flush();
    }

    public static boolean isMusicEnabled(){
        return getPrefs().getBoolean( PREF_MUSIC_ENABLED, true );
    }

    public static void setMusicEnabled(boolean musicEnabled ){
        getPrefs().putBoolean( PREF_MUSIC_ENABLED, musicEnabled );
        getPrefs().flush();
    }

    public static float getMusicVolume(){
        return getPrefs().getFloat( PREF_MUSIC_VOLUME, 0.5f );
    }
    
    public static float getSoundVolume(){
        return getPrefs().getFloat( PREF_SOUND_VOLUME, 0.5f );
    }

    public static void setMusicVolume(float volume ){
        getPrefs().putFloat( PREF_MUSIC_VOLUME, volume );
        getPrefs().flush();
    }
    
    public static void setSoundVolume(float volume ){
        getPrefs().putFloat( PREF_SOUND_VOLUME, volume );
        getPrefs().flush();
    }
}
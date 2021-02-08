package net.pitchblack.getenjoyment.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;

public class MusicManager implements Disposable {
	public enum PitchBlackMusic {
		MENU("sound/Menu_music.mp3"), GAME("sound/Game_music.mp3");

		private String fileName;
		private Music musicResource;

		private PitchBlackMusic(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}

		public Music getMusicResource() {
			return musicResource;
		}

		public void setMusicResource(Music musicBeingPlayed) {
			this.musicResource = musicBeingPlayed;
		}
	}

	private static volatile MusicManager instance = null;

	private static PitchBlackMusic musicBeingPlayed;

	private float volume = 1f;

	private boolean enabled = true;

	public MusicManager() {
	}

	public final static MusicManager getInstance() {
		if (instance == null) {
			synchronized (MusicManager.class) {
				if (instance == null) {
					instance = new MusicManager();
				}
			}
		}
		return instance;
	}

	public void play(PitchBlackMusic music) {
		if (!enabled)
			return;

		if (musicBeingPlayed == music)
			return;

		Gdx.app.log(PitchBlackGraphics.LOG, "Playing music: " + music.name());

		stop();

		FileHandle musicFile = Gdx.files.internal(music.getFileName());
		Music musicResource = Gdx.audio.newMusic(musicFile);
		musicResource.setVolume(volume);
		musicResource.setLooping(true);
		musicResource.play();

		musicBeingPlayed = music;
		musicBeingPlayed.setMusicResource(musicResource);
	}

	public static void stop() {
		if (musicBeingPlayed != null) {
			Gdx.app.log(PitchBlackGraphics.LOG, "Stopping current music");
			Music musicResource = musicBeingPlayed.getMusicResource();
			musicResource.stop();
			musicResource.dispose();
			musicBeingPlayed = null;
		}
	}

	public void setVolume(float volume) {
		Gdx.app.log(PitchBlackGraphics.LOG, "Adjusting music volume to: " + volume);

		if (volume < 0 || volume > 1f) {
			throw new IllegalArgumentException("The volume must be inside the range: [0,1]");
		}
		this.volume = volume;

		if (musicBeingPlayed != null) {
			musicBeingPlayed.getMusicResource().setVolume(volume);
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (!enabled)
			stop();
	}
	
	@Override
	public void dispose() {
		Gdx.app.log(PitchBlackGraphics.LOG, "Disposing music manager");
		stop();
	}
}
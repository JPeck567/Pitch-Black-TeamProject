package net.pitchblack.getenjoyment.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.graphics.GL20;
import net.pitchblack.getenjoyment.client.GameInstancesClient;
import net.pitchblack.getenjoyment.graphics.PitchBlackGame;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;

import static org.mockito.Mockito.mock;

public class ServerGameLauncher {
	public static void main (String[] args) {
		// I have to make a headless application in order to provide opengl context so I can use the asset manager
		// I have been to hell and back spending 10 hours straight figuring this out :((

		HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
//		HeadlessNativesLoader.load();
		Gdx.gl = mock(GL20.class);
		new HeadlessApplication(new GameInstancesClient(), config);

//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.title = "Pitch Black";
//		config.width = 1080;
//		config.height = 720;
//
//		new LwjglApplication(new GameInstancesClient(), config);
	}
}
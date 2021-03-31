package net.pitchblack.getenjoyment.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import net.pitchblack.getenjoyment.backend.client.GameInstancesClient;

import static org.mockito.Mockito.mock;

public class ServerGameLauncher {
	public static void main (String[] args) {
		// I have to make an application for GameInstancesClient in order to provide context
        // for libGDX so I can use asset manager. This is in the form of a headless application.
        // This requires mocking of openGL for texture loading from asset manager and other associated gl methods
		// fyi: I have been to hell and back figuring this out :((

		HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
//		HeadlessNativesLoader.load();
		Gdx.gl = mock(GL20.class);  // provide gl context via mock
		new HeadlessApplication(new GameInstancesClient(), config);

//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.title = "Pitch Black";
//		config.width = 1080;
//		config.height = 720;
//		new LwjglApplication(new GameInstancesClient(), config);
	}
}
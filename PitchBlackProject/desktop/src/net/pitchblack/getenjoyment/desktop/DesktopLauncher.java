package net.pitchblack.getenjoyment.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.pitchblack.getenjoyment.frontend.rendering.PitchBlackGraphics;

public class DesktopLauncher {
	public static void main(String... args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Pitch Black Client";
		config.width = 1280;
		config.height = 720;
		String serverURL = "http://localhost:8081";

		try{
			serverURL = args[0];
		} catch (ArrayIndexOutOfBoundsException e){ System.out.println("No URL was provided, will run on localhost"); }

		System.out.println("Connecting to: " + serverURL);
		new LwjglApplication(new PitchBlackGraphics(serverURL), config);
	}
}
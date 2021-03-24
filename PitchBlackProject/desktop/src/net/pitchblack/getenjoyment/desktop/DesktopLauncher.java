package net.pitchblack.getenjoyment.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator;

    public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Pitch Black";
		config.width = 1080;
		config.height = 720;
		
		new LwjglApplication(new PitchBlackGraphics(), config);
	}
}
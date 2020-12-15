package net.pitchblack.getenjoyment.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.pitchblack.getenjoyment.client.ClientGame;
import net.pitchblack.getenjoyment.graphics.PitchBlackGame;

public class ServerClientLauncher {
	public static void main (String[] args) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "Pitch Black Game";
			//config.width = 1080;
			//config.height = 720;
			new LwjglApplication(new PitchBlackGame(), config);
		
		}
	}

package hack.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import hack.graphics.HackGraphicsTest;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "hackGraphicsTest";;
		config.width = 1080;
		config.height = 720;
		new LwjglApplication(new HackGraphicsTest(), config);
	
	}
}

package net.pitchblack.getenjoyment.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.pitchblack.getenjoyment.PitchBlack;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                //return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new PitchBlackGraphics();
        }
}
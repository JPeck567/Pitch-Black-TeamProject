package net.pitchblack.getenjoyment.graphics.screens;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;

import net.pitchblack.getenjoyment.client.Client;
import net.pitchblack.getenjoyment.client.Login;
import net.pitchblack.getenjoyment.client.LoginOptions;
import net.pitchblack.getenjoyment.client.Registration;
import net.pitchblack.getenjoyment.client.Client.AccountState;
import net.pitchblack.getenjoyment.client.Client.ClientState;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics;
import net.pitchblack.getenjoyment.graphics.PitchBlackGraphics.Screens;

public class LoginInitiator {
	private PitchBlackGraphics parent;
	private Client client;
	private LoginOptions optionWindow;
	private Registration registrationWindow;
	private Login loginWindow;
	private JFrame activeWindow;
	
	public enum WindowType {
		OPTIONS,
		LOGIN,
		REGISTRATION
	}
	
	public LoginInitiator(PitchBlackGraphics p, Client client) {
		parent = p;
		this.client = client;
		//client.beginConnection();
		registrationWindow = new Registration(client, this);
		loginWindow = new Login(client, this);
		optionWindow = new LoginOptions(this);
	}
	
	public void setWindow(WindowType windowType) {
		if(activeWindow != null) {
			activeWindow.setVisible(false);
		}
		
		switch(windowType) {
			case OPTIONS:
				activeWindow = optionWindow;
				break;
			case LOGIN:
				activeWindow = loginWindow;
				break;
			case REGISTRATION:
				activeWindow = registrationWindow;
				break;
		}
		activeWindow.setVisible(true);
	}
	
	public void loginResponse(boolean res, String message) {
		JOptionPane.showMessageDialog(activeWindow, message);
		
		if(res) {  // if correct login
			activeWindow.setVisible(false);
			disposeWindows();
			//send an action to happen in the rendering thread to process after rendering. without, will execute in the jframe thread, where no reference to parent, which is a render class
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					if(client.getAccountState() == AccountState.LOGGED_IN) {
						//client.endConnection();
						parent.changeScreen(Screens.MENU);
						client.setClientState(ClientState.IDLE);
					}
				}
			});
		} else {  // wrong login, go back to options
			setWindow(WindowType.OPTIONS);
		}
	}
	
	public void registrationResponse(boolean res, String message) {
		JOptionPane.showMessageDialog(activeWindow, message);
		
		if(res) {  // if true, swap windows	
			setWindow(WindowType.OPTIONS);
		}
	}
	
	// make jframe to show login or registraion window
	
	private void disposeWindows() {
		optionWindow.dispose();
		loginWindow.dispose();
		registrationWindow.dispose();
		activeWindow.dispose();
	}
}

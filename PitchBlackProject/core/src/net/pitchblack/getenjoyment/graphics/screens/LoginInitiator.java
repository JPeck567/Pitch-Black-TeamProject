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
		client.beginConnection();
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
	
	public void loginResponse(boolean res) {
		System.out.println(res);
		client.endConnection();
		
		if(res) {
			JOptionPane.showMessageDialog(activeWindow, "Login Successful");
			activeWindow.setVisible(false);
			dispose();
			// post a Runnable to the rendering thread that processes the result
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					if(client.accountState == AccountState.LOGGED_IN) {
						parent.changeScreen(Screens.MENU); // parent.getScreen().show();
					}
				}
			});
		} else {
			// failed and go back to option screen
			JOptionPane.showMessageDialog(activeWindow, "Login Unsuccessful");
			setWindow(WindowType.OPTIONS);
		}
	}
	
	public void registrationResponse(boolean res) {
		System.out.println(res);
		client.endConnection();
		
		if(res) {
			JOptionPane.showMessageDialog(activeWindow, "Registration Successful");
			setWindow(WindowType.OPTIONS);
		} else {
			JOptionPane.showMessageDialog(activeWindow, "Registration Unsuccessful");
		}
	}
	
	// make jframe to show login or registraion window
	
	private void dispose() {
		optionWindow.dispose();
		loginWindow.dispose();
		registrationWindow.dispose();
		activeWindow.dispose();
	}
	
	public static void main(String args[]) {
		//PitchBlackGraphics parent = new PitchBlackGraphics();
		Client c = new Client();
		LoginInitiator lgn = new LoginInitiator(null,c);
		//lgn.setWindow(false);  // login
	}


}

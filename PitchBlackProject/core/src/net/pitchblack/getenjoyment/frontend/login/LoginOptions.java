package net.pitchblack.getenjoyment.frontend.login;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.pitchblack.getenjoyment.frontend.game.screens.LoginInitiator;
import net.pitchblack.getenjoyment.frontend.game.screens.LoginInitiator.WindowType;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginOptions extends JFrame {

	private JPanel contentPane;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					LoginOptions frame = new LoginOptions(nulll);
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public LoginOptions(final LoginInitiator loginInitiator) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 649, 398);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel titleTxt = new JLabel("Pitch Black");
		titleTxt.setForeground(Color.WHITE);
		titleTxt.setFont(new Font("Algerian", Font.PLAIN, 60));
		titleTxt.setBackground(Color.DARK_GRAY);
		titleTxt.setHorizontalAlignment(SwingConstants.CENTER);
		titleTxt.setBounds(115, 51, 387, 94);
		contentPane.add(titleTxt);
		
		JButton loginBtn = new JButton("Login");
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginInitiator.setWindow(WindowType.LOGIN);
			}
		});
		loginBtn.setFont(new Font("Tahoma", Font.PLAIN, 30));
		loginBtn.setForeground(Color.BLACK);
		loginBtn.setBounds(37, 213, 210, 94);
		contentPane.add(loginBtn);
		
		
		JButton registerBtn = new JButton("Register");
		registerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginInitiator.setWindow(WindowType.REGISTRATION);
			}
		});
		registerBtn.setFont(new Font("Tahoma", Font.PLAIN, 30));
		registerBtn.setForeground(Color.BLACK);
		registerBtn.setBounds(374, 213, 210, 94);
		contentPane.add(registerBtn);
		
		JLabel orTxt = new JLabel("OR");
		orTxt.setHorizontalAlignment(SwingConstants.CENTER);
		orTxt.setFont(new Font("Algerian", Font.PLAIN, 25));
		orTxt.setForeground(Color.WHITE);
		orTxt.setBackground(Color.DARK_GRAY);
		orTxt.setBounds(278, 247, 69, 30);
		contentPane.add(orTxt);
	}
}

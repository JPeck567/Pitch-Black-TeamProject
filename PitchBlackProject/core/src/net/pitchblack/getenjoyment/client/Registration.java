package net.pitchblack.getenjoyment.client;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JPasswordField;

public class Registration extends JFrame {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private JPanel contentPane;
	private JPasswordField password;
/**
	/**
	 * Launch the application.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Registration frame = new Registration(new Client());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	Class.forName("com.mysql.jdbc.Driver");
	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gamedatabase","root","");
	PreparedStatement ps = conn.prepareStatement("insert into users(email,username,password) values(?,?,?);");
	int x = ps.executeUpdate();
	if(x > 0) {
		System.out.println("Registration successful");
		JOptionPane.showMessageDialog(contentPane, "Registration successful");
	}else {
		System.out.println("Registration Failed");
	}
*/

	public static boolean validateEmail(String emailStr) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr).find();
	}
	
	/**
	 * Create the frame.
	 */
	public Registration(final Client client, final LoginInitiator loginInitiator) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 355, 459);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		final JTextArea email = new JTextArea();
		email.setBounds(20, 117, 298, 29);
		contentPane.add(email);
		
		final JTextArea username = new JTextArea();
		username.setBounds(20, 200, 298, 29);
		contentPane.add(username);
		
		JLabel lblUsername = new JLabel("Email");
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUsername.setBounds(20, 92, 86, 14);
		contentPane.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Username");
		lblPassword.setForeground(Color.WHITE);
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassword.setBounds(20, 175, 86, 14);
		contentPane.add(lblPassword);
		
		JLabel lblPassword_1 = new JLabel("Password");
		lblPassword_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassword_1.setForeground(Color.WHITE);
		lblPassword_1.setBounds(20, 259, 86, 14);
		contentPane.add(lblPassword_1);
		
		JButton btnNewButton = new JButton("Register");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String emailText = email.getText();
					if ((!emailText.equals("") && !username.getText().equals("")) && !password.getText().equals("")) {
						if(!validateEmail(emailText)) {
							JOptionPane.showMessageDialog(contentPane, "Invalid email, please try again");
						} else {
							client.emitSendRegistration(email.getText(), username.getText(), password.getText(), loginInitiator);
						}
					} else {
						JOptionPane.showMessageDialog(contentPane, "Please ensure all fields are not empty");
					}
				} catch(Exception e1) {
					System.out.println(e1);
					JOptionPane.showMessageDialog(contentPane, "An error has occured - registration Failed");
				}
			}
		});
		btnNewButton.setBounds(20, 359, 89, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Sign Up");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Algerian", Font.PLAIN, 30));
		lblNewLabel.setBounds(0, 35, 357, 46);
		contentPane.add(lblNewLabel);
		
		password = new JPasswordField();
		password.setBounds(20, 284, 298, 29);
		contentPane.add(password);
	}
}

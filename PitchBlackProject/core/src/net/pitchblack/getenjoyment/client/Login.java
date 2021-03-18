package net.pitchblack.getenjoyment.client;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.pitchblack.getenjoyment.graphics.screens.LoginInitiator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.awt.Color;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField username;
	private JPasswordField password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Login frame = new Login(null, null);
		frame.setVisible(true);
	}

	/**
	 * Create the frame.
	 * @param client 
	 */
	public Login(final Client client, final LoginInitiator loginInit) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 355, 382);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Login");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Algerian", Font.PLAIN, 30));
		lblNewLabel.setBounds(10, 31, 317, 38);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Username");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(10, 91, 79, 14);
		contentPane.add(lblNewLabel_1);
		
		username = new JTextField();
		username.setBounds(10, 116, 317, 29);
		contentPane.add(username);
		username.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Password");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setBounds(10, 178, 317, 14);
		contentPane.add(lblNewLabel_2);
		
		password = new JPasswordField();
		password.setBounds(10, 203, 317, 29);
		contentPane.add(password);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.emitSendLogin(username.getText(), password.getText(), loginInit);
			}
		});
		btnLogin.setBounds(10, 279, 89, 23);
		contentPane.add(btnLogin);
	}
}
//					Class.forName("com.mysql.jdbc.Driver");
//					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gamedatabase","root","");
//					Statement stmt = con.createStatement();
//					String sql = "Select * from users where username= '"+username.getText()+"' and Password='"+password.getText().toString()+"'";
//					System.out.println(sql);
//					ResultSet rs = stmt.executeQuery(sql);
//					if(rs.next())
//						JOptionPane.showMessageDialog(null,"Login Successful");
//					else
//						JOptionPane.showMessageDialog(null,"Incorrect Username and Password");
//				    con.close();
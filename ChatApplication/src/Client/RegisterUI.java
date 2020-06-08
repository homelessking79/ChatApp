package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class RegisterUI extends JFrame {
	private int port = 123;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterUI regUI = new RegisterUI("localhost");
					regUI.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public RegisterUI(String IP) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("Create New User");
		label.setForeground(Color.BLACK);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.PLAIN, 30));
		
		label.setBounds(100, 10, 250, 30);
		getContentPane().add(label);
		
		JLabel label_1 = new JLabel("User Name:");
		label_1.setBounds(20, 50, 100, 20);
		getContentPane().add(label_1);
		
		usernameField = new JTextField();
		usernameField.setBounds(200, 50, 200, 20);
		getContentPane().add(usernameField);
		
		JLabel label_2 = new JLabel("Password:");
		label_2.setBounds(20, 100, 100, 20);
		getContentPane().add(label_2);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(200, 100, 200, 20);
		getContentPane().add(passwordField);
		
		JLabel label_3 = new JLabel("Confirm Password:");
		label_3.setBounds(20, 150, 150, 20);
		getContentPane().add(label_3);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(200, 150, 200, 20);
		getContentPane().add(passwordField_1);
		
		JButton button = new JButton("Create");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				registerUser();
				
			}

			private void registerUser() {
				// TODO Auto-generated method stub
				String user = usernameField.getText();
				String pass = new String(passwordField.getPassword());
				String pass_confirm = new String(passwordField_1.getPassword());
				if(!pass.equals(pass_confirm)){
					JOptionPane.showMessageDialog(null, "Password mismatch!");
				}
				else {
					Socket socket;
					try {
						socket = new Socket(IP, port);
						PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
			            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
						String info = "register:" + user + "," + pass;
						pr.println(info);
						String msg = in.readLine();
						JOptionPane.showMessageDialog(null, msg);
						if(msg.equals("Successfully Registered!")) {
							ChatUI chatUI = new ChatUI(socket, in, pr, user);
							chatUI.setVisible(true);
							dispose();
						}
					} catch (UnknownHostException ex) {
						ex.printStackTrace();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		
		button.setBounds(20, 200, 100, 30);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("Clear");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				usernameField.setText("");
				passwordField.setText("");
				passwordField_1.setText("");
			}
		});
		button_1.setBounds(300, 200, 100, 30);
		getContentPane().add(button_1);
	}
}
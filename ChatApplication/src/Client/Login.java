package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login extends JFrame {

	private Socket socket;
	private PrintWriter pr;
	private BufferedReader in;
	private static String IP;

	private JTextField txtUsername;
	private JPasswordField pwdPassword;
	
	private static JFrame frame1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login_IP();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void login_IP() {
		frame1 = new JFrame();
		frame1.setResizable(false);
		frame1.setBounds(300, 300, 330, 150);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.getContentPane().setLayout(null);
		
		JLabel lblHiWelcomeConnect = new JLabel("Enter IP");
		lblHiWelcomeConnect.setBounds(10, 11, 258, 14);
		frame1.getContentPane().add(lblHiWelcomeConnect);
		
		JTextField getIPTextField = new JTextField();
		getIPTextField.addKeyListener(new KeyAdapter() {
			boolean pressed = false;
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(!pressed) {
					if(arg0.getKeyCode() != KeyEvent.VK_ENTER) {
						pressed = true;
					}
				}
				else {
					if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						IP = getIPTextField.getText();
						Login frame_login = new Login();
						frame_login.setVisible(true);
						frame1.dispose();
					}
				}
			}
		});
		getIPTextField.setBounds(70, 10, 200, 20);
		frame1.getContentPane().add(getIPTextField);
		
		JButton getIPBtn = new JButton("GET IP");
		frame1.setVisible(true);
		
		getIPBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IP = getIPTextField.getText();
				frame1.dispose();
				Login frame_login = new Login();
				frame_login.setVisible(true);
			}
		});
		getIPBtn.setBounds(190, 50, 80, 20);
		frame1.getContentPane().add(getIPBtn);
	}
	
	public Login() {		
		try {
			this.socket = new Socket(IP, 123);
			this.pr = new PrintWriter(socket.getOutputStream(), true);
	        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		
		setResizable(false);
		setBounds(100, 700, 448, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("LOGIN");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login();
			}
		});
		btnNewButton.setBounds(10,150,100,20);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		getContentPane().add(btnNewButton);
		
		
		JButton btnNewButton_1 = new JButton("REGISTER");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RegisterUI registerUI = new RegisterUI(IP);
				registerUI.setVisible(true);
				dispose();
				
			}
		});
		btnNewButton_1.setBounds(300,150,100,20);
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		getContentPane().add(btnNewButton_1);
		
		JLabel inputUserName = new JLabel("Username: ");
		inputUserName.setBounds(10, 60, 100, 20);
		getContentPane().add(inputUserName);
		
		txtUsername = new JTextField();
		txtUsername.addKeyListener(new KeyAdapter() {
			boolean pressed = false;
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(!pressed) {
					if(arg0.getKeyCode() != KeyEvent.VK_TAB) {
						txtUsername.setText("");
						txtUsername.setForeground(Color.BLACK);
						pressed = true;
					}
				}
			}
		});
		txtUsername.setForeground(Color.LIGHT_GRAY);
		txtUsername.addMouseListener(new MouseAdapter() {
			boolean pressed = false;
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!pressed) {
					if(arg0.getButton() == MouseEvent.BUTTON1) {
						txtUsername.setText("");
						txtUsername.setForeground(Color.BLACK);
						pressed = true;
					}
				}
			}
		});
		txtUsername.setText("Username");
		txtUsername.setBounds(150, 60, 250, 20);
		getContentPane().add(txtUsername);
		
		JLabel inputPassword = new JLabel("Password: ");
		inputPassword.setBounds(10, 100, 100, 20);
		getContentPane().add(inputPassword);
	
		pwdPassword = new JPasswordField();
		pwdPassword.addKeyListener(new KeyAdapter() {
			boolean pressed = false;
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(!pressed) {
					if(arg0.getKeyCode() != KeyEvent.VK_ENTER) {
						pwdPassword.setText("");
						pwdPassword.setForeground(Color.BLACK);
						pwdPassword.setEchoChar('*');
						pressed = true;
					}
				}
				else {
					if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						login();
					}
				}
			}
		});
		pwdPassword.addMouseListener(new MouseAdapter() {
			boolean pressed = false;
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!pressed) {
				if(arg0.getButton() == MouseEvent.BUTTON1) {
					pwdPassword.setText("");
					pwdPassword.setForeground(Color.BLACK);
					pwdPassword.setEchoChar('*');
					pressed = true;
					}
				}
			}
		});
		pwdPassword.setForeground(Color.LIGHT_GRAY);
		pwdPassword.setSelectionColor(Color.MAGENTA);
		pwdPassword.setText("Password");
		pwdPassword.setEchoChar((char) 0);  
		
		pwdPassword.setBounds(150, 100, 250, 20);
		getContentPane().add(pwdPassword);
		JLabel lblLogIn = new JLabel("Login Screen");
		lblLogIn.setForeground(Color.BLUE);
		lblLogIn.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogIn.setHorizontalTextPosition(SwingConstants.CENTER);
		lblLogIn.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		lblLogIn.setBounds(150,10,150,50);
		getContentPane().add(lblLogIn);	
	}
	
	private void login() {
		String user = txtUsername.getText();
		String pass = new String(pwdPassword.getPassword());
		try {
			String info = "login:" + user + "," + pass;
			pr.println(info);
			String msg = in.readLine();
			JOptionPane.showMessageDialog(null, msg);
			if(msg.equals("Logged in!")) {
				ChatUI chatUI = new ChatUI(socket, in, pr, user);
				chatUI.setVisible(true);
				dispose();
			}
		} catch (UnknownHostException ex) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
		} catch (IOException ex) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
		}
	}
}


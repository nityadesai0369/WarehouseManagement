package UC1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import javax.swing.*;

public class LoginPage extends JFrame implements ActionListener {

	JFrame frame = new JFrame();
	JButton loginButton = new JButton("Login");
	JButton resetButton = new JButton("Reset");
	JButton backButton = new JButton("Back");
	JTextField userIDField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	JLabel userIdLabel = new JLabel("Username");
	JLabel userPasswordLabel = new JLabel("Password");
	JLabel startLabel = new JLabel("");
	JLabel messageLabel = new JLabel("");

	private SimpleHttpServer httpServer;

	public LoginPage() {
		super("Login Page");

		userIdLabel.setBounds(50,100,75,25);
		userPasswordLabel.setBounds(50,150,75,25);

		startLabel.setBounds(125,50,250,35);
		startLabel.setFont(new Font(null,Font.CENTER_BASELINE,25));
		startLabel.setText("LOGIN PAGE");

		messageLabel.setBounds(50,250,250,35);
		messageLabel.setFont(new Font(null,Font.CENTER_BASELINE,12));

		userIDField.setBounds(125,100,200,25);
		userPasswordField.setBounds(125,150,200,25);

		backButton.setBounds(0,0,80,20);
		backButton.setFocusable(false);
		backButton.addActionListener(this);

		loginButton.setBounds(125,200,100,25);
		loginButton.setFocusable(false);
		loginButton.addActionListener(this);

		resetButton.setBounds(225,200,100,25);
		resetButton.setFocusable(false);
		resetButton.addActionListener(this);

		frame.add(startLabel);
		frame.add(userIdLabel);
		frame.add(userPasswordLabel);
		frame.add(messageLabel);
		frame.add(userIDField);
		frame.add(userPasswordField);
		frame.add(loginButton);
		frame.add(resetButton);
		frame.add(backButton);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420,420);
		frame.setLayout(null);
		frame.setVisible(true);


        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // Terminate the program
            }
        });

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			frame.dispose();
			WelcomePage welc = new WelcomePage();
		}

		if (e.getSource() == resetButton) {
			userIDField.setText("");
			userPasswordField.setText("");
		}

		if (e.getSource() == loginButton) {
			String userID = userIDField.getText();
			String pass = String.valueOf(userPasswordField.getPassword());

			if (SQLiteJDBC.selectCred(userID, pass)) {
				int port = SimpleHttpServer.findAvailablePort();

				httpServer = new SimpleHttpServer();
				try {
					httpServer.start(port);
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				frame.dispose();
				adminUI UI = null;
				try {
					UI = adminUI.getInstance();
				} catch (SQLException ex) {
					throw new RuntimeException(ex);
				}
				UI.setSize(800, 600);
				UI.setVisible(true);

			} else {
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Wrong UserID or Password, Try again");
				userIDField.setText("");
				userPasswordField.setText("");
			}
		}
	}


}

package UC1;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class WelcomePage implements ActionListener{
	
	JFrame frame = new JFrame();
	JLabel messageLabel = new JLabel("");
	JLabel messageLabel2 = new JLabel("");
	JButton adminButton = new JButton("Admin");
	JButton clientButton = new JButton("Client");
	
    
	public WelcomePage()
	{
		messageLabel.setBounds(150,100,150,25);
		messageLabel.setText("Welcome!");
		messageLabel.setFont(new Font(null,Font.CENTER_BASELINE,25));
		messageLabel.setForeground(Color.green);
		frame.add(messageLabel);
		
		messageLabel2.setBounds(150,200,150,25);
		messageLabel2.setFont(new Font(null,Font.CENTER_BASELINE,15));
		messageLabel2.setForeground(Color.green);
		frame.add(messageLabel2);
		
		adminButton.setBounds(130,150,75,25);
		adminButton.addActionListener(this);
		adminButton.setFocusable(false);
		frame.add(adminButton);
		
		clientButton.setBounds(210,150,75,25);
		clientButton.addActionListener(this);
		clientButton.setFocusable(false);
		frame.add(clientButton);
		
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
	
	
	public boolean isVisible()
	{
		if(frame.isVisible() == true)
		{
			return true;
		}
		
		return false;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == adminButton)
		{
			frame.dispose();
			LoginPage loginPage = new LoginPage();
		}
		
		if(e.getSource() == clientButton)
		{
			int port = SimpleHttpServer.findAvailablePort();

			SimpleHttpServer httpServer = new SimpleHttpServer();
			try {
				httpServer.start(port);
			} catch (IOException ex) {
				ex.printStackTrace();  // Handle the exception appropriately
			}

			frame.dispose();
			productOrdering productOrder = productOrdering.getInstance();
			productOrder.setSize(900, 600);
		    productOrder.pack();
		    productOrder.setVisible(true);
		}
		
	}
}

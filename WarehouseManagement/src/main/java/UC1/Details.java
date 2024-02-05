package UC1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static UC1.SQLiteJDBC.c;

public class Details extends JFrame {

    public Details() {
        super("Product Details");

        try {
            // Get product details from the database
            //SQLiteJDBC.connect();
            String selectProductsSQL = "SELECT NAME, UNIT_PRICE, CURRENT_STOCK_QUANTITY FROM PRODUCTS";
            PreparedStatement pstmt = c.prepareStatement(selectProductsSQL);
            ResultSet resultSet = pstmt.executeQuery();

            // Create a panel to hold all product panels
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            // Create labels for each product and add them to the main panel
            while (resultSet.next()) {
                String productName = resultSet.getString("NAME");
                double unitPrice = resultSet.getDouble("UNIT_PRICE");
                int currentStockQuantity = resultSet.getInt("CURRENT_STOCK_QUANTITY");

                JLabel nameLabel = new JLabel("Product Name: " + productName);
                JLabel priceLabel = new JLabel("Price: $" + unitPrice);
                JLabel stockLabel = new JLabel("Stock Available: " + currentStockQuantity);

                // Create a panel for each product and add labels to it
                JPanel productPanel = new JPanel();
                productPanel.setLayout(new GridLayout(5, 10));
                productPanel.add(nameLabel);
                productPanel.add(priceLabel);
                productPanel.add(stockLabel);

                // Add the product panel to the main panel
                mainPanel.add(productPanel);
            }

            // Add the "Back" button to the top of the frame
            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Close the current frame (Details) and open the productOrdering frame
                    dispose();
                    productOrdering productOrder = productOrdering.getInstance();
                    productOrder.setSize(900, 600);
                    productOrder.pack();
                    productOrder.setVisible(true);
                }
            });
            getContentPane().add(backButton, BorderLayout.NORTH);

            // Add the main panel to the frame
            getContentPane().add(mainPanel);

            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving product details.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            //SQLiteJDBC.disconnect();
        }

        // Set frame properties
        setSize(500, 500);
        setLocationRelativeTo(null); // Center the frame on the screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose the frame when closed
        setVisible(true);
    }
}


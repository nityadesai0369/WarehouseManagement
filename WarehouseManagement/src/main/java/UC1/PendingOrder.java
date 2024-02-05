    package UC1;

    import UC1.SQLiteJDBC;
    import UC1.adminUI;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.sql.SQLException;
    import java.util.List;

    public class PendingOrder extends JFrame {

        private JTextArea messageTextArea;

        public PendingOrder() {
            super("Pending Orders");

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel("Pending Orders");
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(titleLabel);

            messageTextArea = new JTextArea();
            messageTextArea.setEditable(false);
            messageTextArea.setLineWrap(true);
            messageTextArea.setPreferredSize(new Dimension(3, 5)); // Set a smaller size
            JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
            panel.add(messageScrollPane);


            try {
                List<String> pendingOrders = SQLiteJDBC.getProductsAndQuantitiesWithPendingStatus();

                for (String productDetails : pendingOrders) {
                    JLabel orderDetailsLabel = new JLabel(productDetails);
                    orderDetailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(orderDetailsLabel);

                    // Add space between order details and buttons
                    panel.add(Box.createRigidArea(new Dimension(0, 10)));

                    JButton confirmOrderButton = new JButton("Confirm Order");
                    confirmOrderButton.addActionListener(new ConfirmOrderActionListener(productDetails));
                    confirmOrderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(confirmOrderButton);

                    // Add space between buttons
                    panel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //BACK BUTTON
            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Navigate back to the adminUI page
                    try {
                        adminUI adminUI = new adminUI();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    dispose(); // Close the current PendingOrder window
                }
            });
            backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(backButton);

            JScrollPane scrollPane = new JScrollPane(panel);
            getContentPane().add(scrollPane);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 400);
            setLocationRelativeTo(null); // Center the frame on the screen
            setVisible(true);
        }

        private class ConfirmOrderActionListener implements ActionListener {
            private String productDetails;

            public ConfirmOrderActionListener(String productDetails) {
                this.productDetails = productDetails;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Split productDetails into product name and quantity
                String[] parts = productDetails.split(" : ");
                String product = parts[0];
                String quantityString = parts[1].split(" ")[0]; // Extract the quantity part and remove "units"
                int quantity = Integer.parseInt(quantityString);

                try {
                    int currentStock = SQLiteJDBC.getCurrentStock(product);

                    if (quantity <= currentStock) {
                        // Confirm the order
                        SQLiteJDBC.updateProduct(product, quantity);
                        // status change
                        SQLiteJDBC.changeStatus(product);
                        // Append a message to the JTextArea indicating confirmation
                        String confirmationMessage = "Confirmed order for: " + productDetails + "\n";
                        messageTextArea.append(confirmationMessage);
                    } else {
                        // Set text in message that restocking is needed to confirm the order
                        String restockMessage = "Please restock to confirm order for: " + productDetails + "\n";
                        messageTextArea.append(restockMessage);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

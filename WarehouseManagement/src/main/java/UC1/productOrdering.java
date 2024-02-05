package UC1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class productOrdering extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    public JComboBox<String> productList;
    public JComboBox<String> quantityList;
    public JComboBox<String> codeList;
    private String theProduct;
    private String theQuantity;
    public JTextArea orderDetails;
    public String productReport = null;
    private String quantityReport = null;
    private String timeReport = null;
    private static productOrdering instance;

    private SimpleHttpServer httpServer;

    //Singleton
    public static productOrdering getInstance() {
        if (instance == null)
            instance = new productOrdering();

        return instance;
    }

    public productOrdering() {
        super("Product Ordering Client");

        //Divide the UI
        JPanel east = new JPanel();
        JPanel west = new JPanel();
        west.setLayout(new GridLayout(3, 0));

        //Back Button
        JButton backButton = new JButton("Back");
        backButton.setActionCommand("addProduct");
        backButton.addActionListener(e -> {
            //SQLiteJDBC.disconnect();
            WelcomePage welc = new WelcomePage();
            dispose(); // Close the current adminUI window
        });


        //Different Labels
        JLabel step1 = new JLabel("Step1 Choose Product");
        JLabel step2 = new JLabel("Step2 Choose Quantity");
        JLabel step3 = new JLabel("Step3 Select Discounts if applicable: ");
        JLabel step4 = new JLabel("Step4 Order your Product: ");
        JLabel chooseProductLabel = new JLabel(": ");

        //Product list
        Vector<String> productNames = new Vector<String>();
        productList = new JComboBox<String>(productNames);
        productNames.add("iPhone 14");
        productNames.add("iPhone 14 Plus");
        productNames.add("iPhone 14 Pro");
        productNames.add("iPhone 14 Pro Max");
        productNames.add("Airpods Gen 2");

        //Details Button
        JButton detailsButton = new JButton("Details");
        detailsButton.setActionCommand("showDetails");
        detailsButton.addActionListener(this);

        //Choose Button
        JButton addProduct = new JButton("Choose");
        addProduct.setActionCommand("addProduct");
        addProduct.addActionListener(this);

        //Qty list
        JLabel qty = new JLabel(": ");
        Vector<String> quantity = new Vector<String>();
        quantityList = new JComboBox<String>(quantity);
        for (int i = 1; i <= 300; i = i + 1) {
            quantity.add("" + i);
        }


        //Qty Button
        JButton addQuantity = new JButton("Choose");
        addQuantity.setActionCommand("addQuantity");
        addQuantity.addActionListener(this);
        quantityList.setActionCommand("addQuantity");

        //Discount codes list
        Vector<String> code = new Vector<String>();
        codeList = new JComboBox<String>(code);
        code.add("1");
        code.add("2");

        //Discount Confirm Button
        JButton confirmCode = new JButton("Confirm Code");
        confirmCode.addActionListener(this);
        confirmCode.setActionCommand("confirm code");

        //Available codes button
        JButton discountCode = new JButton("Available CODES");
        discountCode.setActionCommand("discount");
        discountCode.addActionListener(this);
        codeList.setActionCommand("discount");

        //Confirm order button
        JButton placeOrder = new JButton("Confirm");
        placeOrder.setActionCommand("Your Order has been Placed");
        placeOrder.addActionListener(this);

        //Placement
        JPanel north = new JPanel();
        north.add(backButton);
        north.add(step1);
        north.add(chooseProductLabel);
        north.add(productList);
        north.add(addProduct);
        north.add(detailsButton);
        north.add(step2);
        north.add(qty);
        north.add(quantityList);
        north.add(addQuantity);
        north.add(step3);
        north.add(codeList);
        north.add(confirmCode);
        north.add(discountCode);
        north.add(step4);
        north.add(placeOrder);

        //Order details Label
        JLabel orderDetailsLabel = new JLabel("Order Details: ");
        orderDetails = new JTextArea(30, 170);
        JScrollPane orderDetailsScrollPane = new JScrollPane(orderDetails);
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.add(orderDetailsLabel);
        east.add(orderDetailsScrollPane);

        getContentPane().add(north, BorderLayout.NORTH);
        getContentPane().add(east, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.print(command);

        if ("showDetails".equals(command)) {
            Details det = new Details();
        }

        if ("discount".equals(command)) {
            Discounts dis = new Discounts();
        }

        if ("confirm code".equals(command)) {
            int selectedCode = Integer.parseInt(codeList.getSelectedItem().toString());

            if (theProduct != null && theQuantity != null) {
                int quantity = Integer.parseInt(theQuantity);

                if ((theProduct.startsWith("iPhone") && (quantity < 10 || selectedCode != 1))
                        || (theProduct.startsWith("Airpods") && (quantity < 15 || selectedCode != 2))) {
                    orderDetails.setText("See the policy for discount eligibility.\n");
                } else {
                    try {
                        if (selectedCode == 1 && theProduct.startsWith("iPhone") && quantity >= 10) {
                            applyDiscountToProduct(theProduct, 0.10, quantity); // 10% discount for iPhones
                        } else if (selectedCode == 2 && theProduct.startsWith("Airpods") && quantity >= 15) {
                            applyDiscountToProduct(theProduct, 0.10, quantity); // 10% discount for Airpods
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                orderDetails.setText("Error: Product or Quantity is null.\n");
            }
        }


        if ("addProduct".equals(command)) {
            if (productList.getSelectedItem() == null) {
                orderDetails.setText("Please select an item");
            } else {
                theProduct = productList.getSelectedItem().toString();
                double thePrice;
                try {
                    thePrice = SQLiteJDBC.getUnitPrice(theProduct);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                productReport = "Product : " + theProduct + "\n";
                productReport = productReport + "Price : " + thePrice + "\n";
                orderDetails.setText(productReport);
                productList.setSelectedIndex(-1);

            }
        } else if ("addQuantity".equals(command)) {
            double thePrice;
            try {
                thePrice = SQLiteJDBC.getUnitPrice(theProduct);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            if (theProduct == null) {
                orderDetails.setText("Please select an item");
            } else {
                theQuantity = quantityList.getSelectedItem().toString();
                double totalPrice = thePrice * Double.parseDouble(theQuantity);
                quantityReport = "Quantity : " + theQuantity + "\n";
                quantityReport = quantityReport + "Total Price: " + totalPrice + "\n";
                timeReport = "Client Time Stamp : " + LocalDateTime.now().toString() + "\n";
                orderDetails.setText(productReport + quantityReport + timeReport + "\n");
                quantityList.setSelectedIndex(0);
            }
        } else if ("Your Order has been Placed".equals(command)) {

            if (theProduct == null) {
                orderDetails.setText("Please follow previous steps");
            } else {
                String status = "";
                int currentStock;
                int orderID = 0;
                int maxQty = 0;
                String statusPrint = "";
                try {
                    maxQty = SQLiteJDBC.getMaxStock(theProduct);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    orderID = SQLiteJDBC.getLastOrderId() + 1;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    currentStock = SQLiteJDBC.getCurrentStock(theProduct);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if (Integer.parseInt(theQuantity) <= currentStock) {
                    status = "completed";
                    statusPrint = "has confirmed and placed";

                }
                else{
                    status = "pending";
                    statusPrint = "is pending and will be place as soon as we have the required quantites";
                }

                if(Integer.parseInt(theQuantity) <= maxQty)
                {
                    orderDetails.setText("Your Order of " + theQuantity + " " + theProduct + " " + statusPrint);
                    SQLiteJDBC.updateProduct(theProduct, Integer.parseInt(theQuantity));
                    SQLiteJDBC.updateOrder(orderID, theProduct, Integer.parseInt(theQuantity), status, LocalDateTime.now());
                }
                else
                {
                    orderDetails.setText("Please see the Max Stock for this product");
                }
            }
        }


    }


    private void applyDiscountToProduct(String productName, double discountPercentage, int quantity) throws SQLException {
        double originalPrice = SQLiteJDBC.getUnitPrice(productName);
        double discountedPrice = originalPrice - (originalPrice * discountPercentage);
        double totalPrice = discountedPrice * quantity;

        orderDetails.append("Discount Applied: " + (discountPercentage * 100) + "% off " + productName + " Price\n");
        orderDetails.append("Discounted Price: $" + discountedPrice + " per item\n");
        orderDetails.append("Quantity Ordered: " + quantity + "\n");
        orderDetails.append("Total Price after Discount: $" + totalPrice + "\n");
    }

}

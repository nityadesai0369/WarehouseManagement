package UC1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Discounts extends JFrame {
    // Follows Singelton
    private static Discounts instance;

    Discounts() {
        super("Discount Information");

        JTextArea discountText = new JTextArea();
        discountText.setEditable(false);
        discountText.append("Discount Code 1:\nBuy 10 iPhones and get 15% off.\n\n");
        discountText.append("Discount Code 2:\nBuy 15 Airpods Gen 2 and get 15% off.");

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            productOrdering productOrder = productOrdering.getInstance();
            productOrder.setSize(900, 600);
            productOrder.pack();
            productOrder.setVisible(true);
        });
        getContentPane().add(backButton, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(discountText));

        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static Discounts getInstance() {
        if (instance == null) {
            instance = new Discounts();
        }
        return instance;
    }

}


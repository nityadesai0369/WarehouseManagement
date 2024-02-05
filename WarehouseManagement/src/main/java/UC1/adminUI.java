package UC1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;


public class adminUI extends JFrame {
    private static adminUI instance;


    private static final long serialVersionUID = 1L;
    //private static Map<String, Integer> productData;
    private static LastOrderedProduct theLastOrder;

    private SimpleHttpServer httpServer;

    public static adminUI getInstance() throws SQLException {
        if (instance == null)
            instance = new adminUI();

        return instance;
    }

    public adminUI() throws SQLException {


        super("Warehouse Server UI");
        updateData();

        //productData = SQLiteJDBC.findAvailableProductsAndQuantitiesFromDatabase();
        theLastOrder = LastOrderedProduct.getInstance().findLastOrder();

        JPanel west = new JPanel();
        west.setLayout(new GridLayout(2, 0));

        JPanel east = new JPanel();
        east.setLayout(new GridLayout(2, 0));

        JPanel north = new JPanel();
        north.setLayout(new GridLayout(2, 0));

        getContentPane().add(west, BorderLayout.WEST);
        getContentPane().add(east, BorderLayout.EAST);
        getContentPane().add(north, BorderLayout.NORTH);
        createCharts(west, east, north);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //SQLiteJDBC.disconnect();

                System.exit(0); // Terminate the program
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                super.windowGainedFocus(e);
                try {
                    createCharts(west, east, north);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    public void createCharts(JPanel west, JPanel east, JPanel south) throws SQLException {
        createBar(west);
        createReport(east);
        createRestock(south);

    }

    private void createRestock(JPanel north) {
        JButton backButton = new JButton("Back");
        backButton.setBounds(0, 0, 80, 20);
        backButton.addActionListener(e -> {

            LoginPage login = new LoginPage();
            dispose(); // Close the current adminUI window
        });
        backButton.setFocusable(false);
        north.add(backButton);


        JLabel restockLabel = new JLabel("\nRestock Products: ");
        north.add(restockLabel);

        // Assuming you have a list of product names
        List<String> productNames = Arrays.asList("iPhone 14", "iPhone 14 Plus", "iPhone 14 Pro", "iPhone 14 Pro Max", "Airpods Gen 2");

        // Create a button for each product
        for (String productName : productNames) {
            JButton restockButton = new JButton("Restock " + productName);
            restockButton.addActionListener(e -> initiateRestock(productName));
            north.add(restockButton);
        }

        JButton completePendingOrderButton = new JButton("Complete Pending Orders");
        completePendingOrderButton.addActionListener(e -> completePendingOrders());
        north.add(completePendingOrderButton);
    }

    private void completePendingOrders() {
        PendingOrder pend = new PendingOrder();

    }

    public void createReport(JPanel west) throws SQLException {
        JTextArea report = new JTextArea();
        report.setEditable(false);
        report.setPreferredSize(new Dimension(400, 500));
        report.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        report.setBackground(Color.white);
        JScrollPane outputScrollPane = new JScrollPane(report);
        west.add(outputScrollPane);
        String reportMessage1, reportMessage2, reportMessage3;


        reportMessage1 = "Last Order\n" + "==========================\n" + "\t";
        reportMessage1 = reportMessage1 + "Product: " + theLastOrder.getProductName() + "\n"
                + "\tQuantity:" + theLastOrder.getQuantity() + "\n"
                + "\tTimeStamp:" + theLastOrder.getDate() + "\n";

        reportMessage3 = "Pending Orders\n" + "==============================\n";
        try {
            List pendingQuantities = SQLiteJDBC.getProductsAndQuantitiesWithPendingStatus();

            for (int i = 0; i < pendingQuantities.size(); i++) {
                reportMessage3 = reportMessage3 + "\n \t" + pendingQuantities.get(i);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's requirements
        }

        reportMessage2 = "\nCurrent Product Quantity in Warehouse\n" + "==============================\n";
        HashMap<String, Integer> productData = SQLiteJDBC.findAvailableProductsAndQuantitiesFromDatabase();

        for (Map.Entry<String, Integer> entry : productData.entrySet()) {
            reportMessage2 = reportMessage2 + entry.getKey();
            reportMessage2 = reportMessage2 + "\n \t Quantity ==> " + entry.getValue() + "/" + AvailableProducts.getmax(entry.getKey()) + "\n";
            reportMessage2 = reportMessage2 + "\t Restocking Amounts ==>" + SQLiteJDBC.getRestockSchedule(entry.getKey()) + "\n";
            reportMessage2 = reportMessage2 + "\t Low Stock. == >" + SQLiteJDBC.checkRestockRequirement(entry.getKey()) + "\n";
        }
        report.setText(reportMessage1 + reportMessage3 + reportMessage2);
    }


    public void createBar(JPanel west) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashMap<String, Integer> productData = SQLiteJDBC.findAvailableProductsAndQuantitiesFromDatabase();
        Map<String, Integer> orderedProductData = new LinkedHashMap<>(productData);

        // Move "Airpods Gen 2" to the end
        if (orderedProductData.containsKey("Airpods Gen 2")) {
            Integer airpodsQuantity = orderedProductData.remove("Airpods Gen 2");
            orderedProductData.put("Airpods Gen 2", airpodsQuantity);
        }

        // Add entries to the dataset
        for (Map.Entry<String, Integer> entry : orderedProductData.entrySet()) {
            dataset.setValue(entry.getValue(), entry.getKey(), "");
        }

        for (Map.Entry<String, Integer> entry : productData.entrySet()) {
            dataset.setValue(entry.getValue(), entry.getKey(), "");

        }

        CategoryPlot plot = new CategoryPlot();
        BarRenderer barrenderer1 = new BarRenderer();
        BarRenderer barrenderer2 = new BarRenderer();

        plot.setDataset(0, dataset);
        plot.setRenderer(0, barrenderer1);
        CategoryAxis domainAxis = new CategoryAxis("");
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(new NumberAxis(""));

        plot.setRenderer(1, barrenderer2);

        plot.mapDatasetToRangeAxis(0, 0);// 1st dataset to 1st y-axis

        JFreeChart barChart = new JFreeChart("Warehouse Product Monitor System",
                new Font("Serif", Font.BOLD, 18), plot, true);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        west.add(chartPanel);
    }

    private void initiateRestock(String productName) {
        try {
            SQLiteJDBC.restockProducts(productName);

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQLException appropriately
        }
    }

    private void updateData() throws SQLException {

        //productData = AvailableProducts.getInstance().findAvailableProductsAndQuantitiesFromDatabase();
        theLastOrder = LastOrderedProduct.getInstance().findLastOrder();
    }
}

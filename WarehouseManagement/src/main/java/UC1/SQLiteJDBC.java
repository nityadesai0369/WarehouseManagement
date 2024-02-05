package UC1;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteJDBC {
    static Connection c = null;
    private static Statement stmt = null;

    //SETS UP A CONNECTION
    public static void connect() {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:warehouse.db");

            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String createCredentialsTable = "CREATE TABLE IF NOT EXISTS CREDENTIALS " +
                    "(ID CHAR(200) PRIMARY KEY     NOT NULL," +
                    " PASSWORD  TEXT    NOT NULL)";

            String createProductsTable = "CREATE TABLE IF NOT EXISTS PRODUCTS " +
                    "(PRODUCT_ID INT PRIMARY KEY NOT NULL," +
                    " NAME TEXT NOT NULL," +
                    " CURRENT_STOCK_QUANTITY INT NOT NULL," +
                    " UNIT_PRICE REAL NOT NULL," +
                    " TARGET_MAX_STOCK_QUANTITY INT NOT NULL," +
                    " TARGET_MIN_STOCK_QUANTITY INT NOT NULL," +
                    " RESTOCK_SCHEDULE INT NOT NULL," +
                    " DISCOUNT_STRATEGY_ID INT NOT NULL)";

            String createOrdersTable = "CREATE TABLE IF NOT EXISTS ORDERS" +
                    "(ORDER_ID INT PRIMARY KEY NOT NULL," +
                    " NAME TEXT NOT NULL," +
                    " QUANTITY INT NOT NULL," +
                    " STATUS TEXT NOT NULL," +
                    " ORDER_DATE TIMESTAMP NOT NULL)";

            stmt.executeUpdate(createCredentialsTable);
            stmt.executeUpdate(createProductsTable);
            stmt.executeUpdate(createOrdersTable);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    //disconnects the database
    public static void disconnect() {
        try {
            c.close();
            System.out.println("database closed successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }


    //insert to table credentials
    public static void insert() {
        //connect();

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:warehouse.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String insertCred = "INSERT or IGNORE INTO CREDENTIALS (ID,PASSWORD)" + "VALUES ('nitya','nitya');";
            stmt.executeUpdate(insertCred);

            insertCred = "INSERT or IGNORE INTO CREDENTIALS (ID,PASSWORD)" + "VALUES ('Justine6','warehousemanagementEECS3311');";
            stmt.executeUpdate(insertCred);

            insertCred = "INSERT or IGNORE INTO CREDENTIALS (ID,PASSWORD)" + "VALUES ('Zenith8','warehousemanagementEECS3311');";
            stmt.executeUpdate(insertCred);

            insertCred = "INSERT or IGNORE INTO CREDENTIALS (ID,PASSWORD)" + "VALUES ('Jay23','warehousemanagementEECS3311');";
            stmt.executeUpdate(insertCred);

            insertCred = "INSERT or IGNORE INTO CREDENTIALS (ID,PASSWORD)" + "VALUES ('Nitya36','warehousemanagementEECS3311');";
            stmt.executeUpdate(insertCred);

            stmt.close();
            c.commit();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Records created successfully");

    }

    //INSERT TO TABLE PRODUCTS
    public static void insertProducts() {
        try {

            // Insert products
            insertProduct(1, "iPhone 14", 100, 999.99, 200, 50, 30, 1);
            insertProduct(2, "iPhone 14 Plus", 120, 1199.99, 220, 60, 20, 2);
            insertProduct(3, "iPhone 14 Pro", 80, 1399.99, 180, 40, 25, 3);
            insertProduct(4, "iPhone 14 Pro Max", 90, 1599.99, 190, 45, 10, 4);
            insertProduct(5, "Airpods Gen 2", 200, 199.99, 300, 100, 35, 5);

            System.out.println("Products inserted successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    //DATA TO BE INSERTED IN THE PRODUCTS TABLE
    private static void insertProduct(

            int productId,
            String name,
            int currentStockQuantity,
            double unitPrice,
            int targetMaxStockQuantity,
            int targetMinStockQuantity,
            int restockSchedule,
            int discountStrategyId
    ) {
        try {
            // Prepare the SQL statement for product insertion
            String insertProductSQL = "INSERT INTO PRODUCTS (PRODUCT_ID, NAME, CURRENT_STOCK_QUANTITY, " +
                    "UNIT_PRICE, TARGET_MAX_STOCK_QUANTITY, TARGET_MIN_STOCK_QUANTITY, RESTOCK_SCHEDULE, " +
                    "DISCOUNT_STRATEGY_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            // Create a prepared statement
            PreparedStatement pstmt = c.prepareStatement(insertProductSQL);
            pstmt.setInt(1, productId);
            pstmt.setString(2, name);
            pstmt.setInt(3, currentStockQuantity);
            pstmt.setDouble(4, unitPrice);
            pstmt.setInt(5, targetMaxStockQuantity);
            pstmt.setInt(6, targetMinStockQuantity);
            pstmt.setInt(7, restockSchedule);
            pstmt.setInt(8, discountStrategyId);

            // Execute the update
            pstmt.executeUpdate();

            // Close the prepared statement
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //UPDATE ORDER TABLE IN DATABASE WHENEVER SOMEONE ORDERS SOMETHING
    public static void updateOrder(int orderId, String productName, int qty, String status, LocalDateTime orderDateTime) {
        try {
            //connect();

            // Disable auto-commit
            c.setAutoCommit(false);

            // Prepare the SQL statement for updating order information
            String updateOrderSQL = "INSERT INTO ORDERS (ORDER_ID, NAME, QUANTITY, STATUS, ORDER_DATE) VALUES (?, ?, ?, ?, ?)";

            // Create a prepared statement
            PreparedStatement pstmt = c.prepareStatement(updateOrderSQL);
            pstmt.setInt(1, orderId);
            pstmt.setString(2, productName);
            pstmt.setInt(3, qty);
            pstmt.setString(4, status);

            // Convert LocalDateTime to java.sql.Date
            java.sql.Date sqlDate = java.sql.Date.valueOf(orderDateTime.toLocalDate());

            pstmt.setDate(5, sqlDate);

            // Execute the update
            pstmt.executeUpdate();

            // Commit changes
            c.commit();

            // Close the prepared statement
            pstmt.close();

            // Enable auto-commit again
            c.setAutoCommit(true);

            System.out.println("Order updated successfully");
        } catch (Exception e) {
            // Rollback in case of an exception
            try {
                if (c != null) {
                    c.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    //SELECTING CREDENTIALS FROM TABLE CREDENTIALS
    public static boolean selectCred(String ID, String pass) {
        try {
            //connect();

            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:warehouse.db");

            String selectQuery = "SELECT * FROM CREDENTIALS WHERE ID = ? AND PASSWORD = ?";
            try (PreparedStatement pstmt = c.prepareStatement(selectQuery)) {
                pstmt.setString(1, ID);
                pstmt.setString(2, pass);

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    // Check if the result set has any rows
                    return resultSet.next();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }


    //UPDATE THE STOCK QTY
    static void updateProduct(String selectedItem, int numberOfProductsOrdered) {
        try {
            //connect();
            // Prepare the SQL statement for updating product information
            String updateProductSQL = "UPDATE PRODUCTS SET CURRENT_STOCK_QUANTITY = CURRENT_STOCK_QUANTITY - ? " +
                    "WHERE NAME = ?";

            // Create a prepared statement
            PreparedStatement pstmt = c.prepareStatement(updateProductSQL);
            pstmt.setInt(1, numberOfProductsOrdered);
            pstmt.setString(2, selectedItem);

            // Execute the update
            pstmt.executeUpdate();

            // Close the prepared statement
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Records updated successfully");
    }

    //GETS CURRENT STOCK
    static int getCurrentStock(String productName) throws SQLException {
        //connect();

        Statement stmt = c.createStatement();
        String query = "SELECT CURRENT_STOCK_QUANTITY FROM PRODUCTS WHERE NAME = '" + productName + "'";
        ResultSet rs = stmt.executeQuery(query);
        int currentStock = 0;

        if (rs.next()) {
            currentStock = rs.getInt("CURRENT_STOCK_QUANTITY");
        }

        rs.close();
        stmt.close();
        return currentStock;
    }

    //GETS MAX STOCK
    static int getMaxStock(String productName) throws SQLException {
        //connect();
        Statement stmt = c.createStatement();
        String query = "SELECT TARGET_MAX_STOCK_QUANTITY FROM PRODUCTS WHERE NAME = '" + productName + "'";
        ResultSet rs = stmt.executeQuery(query);
        int maxStock = 0;

        if (rs.next()) {
            maxStock = rs.getInt("TARGET_MAX_STOCK_QUANTITY");
        }

        rs.close();
        stmt.close();
        return maxStock;
    }

    //GETS PRICES
    public static double getUnitPrice(String productName) throws SQLException {
        //connect();
        Statement stmt = c.createStatement();
        String query = "SELECT UNIT_PRICE FROM PRODUCTS WHERE NAME = '" + productName + "'";
        ResultSet rs = stmt.executeQuery(query);
        double unitPrice = 0;

        if (rs.next()) {
            unitPrice = rs.getDouble("UNIT_PRICE");
        }

        rs.close();
        stmt.close();
        return unitPrice;
    }

    //GETS RESTOCK SCHEDULE
    static int getRestockSchedule(String productName) throws SQLException {
        //connect();
        Statement stmt = c.createStatement();
        String query = "SELECT RESTOCK_SCHEDULE FROM PRODUCTS WHERE NAME = '" + productName + "'";
        ResultSet rs = stmt.executeQuery(query);
        int restockSchedule = 0;

        if (rs.next()) {
            restockSchedule = rs.getInt("RESTOCK_SCHEDULE");
        }

        rs.close();
        stmt.close();
        return restockSchedule;
    }

    //RESTOCKS THE PRODUCT (MAKING SURE NOT EXCEEDS THE MAX STOCK)
    public static void restockProducts(String productName) throws SQLException {
        //connect();

        int currentStock = getCurrentStock(productName);
        int maxStock = getMaxStock(productName);
        int restockSchedule = getRestockSchedule(productName);

        int quantityToRestock;

        if (currentStock + restockSchedule <= maxStock) {
            quantityToRestock = restockSchedule;
        } else {
            quantityToRestock = maxStock - currentStock;
        }

        if (quantityToRestock > 0) {
            System.out.println("Restocking Operation for Product " + productName + " initiated");
            performRestocking(productName, currentStock, quantityToRestock);
            System.out.println("Restocking Operation for Product " + productName + " completed");
        } else {
            System.out.println("No restocking needed for Product " + productName);
        }
    }

    //THE ACTUAL MATH FOR RESTOCKING
    private static void performRestocking(String productName, int currentStock, int productsToRestock) throws SQLException {

        // Perform the restocking operation in the database
        Statement stmt = c.createStatement();
        String updateQuery = "UPDATE PRODUCTS SET CURRENT_STOCK_QUANTITY = " + (currentStock + productsToRestock) +
                " WHERE NAME = '" + productName + "'";
        stmt.executeUpdate(updateQuery);
        stmt.close();
    }

    //CHANGING THE STATUS
    public static void changeStatus(String productName) throws SQLException {

        String updateStatusSQL = "UPDATE ORDERS SET STATUS = 'completed' WHERE NAME = ?";
        PreparedStatement pstmt = c.prepareStatement(updateStatusSQL);
        pstmt.setString(1,productName);
        // Execute the update
        pstmt.executeUpdate();

        // Close the prepared statement
        pstmt.close();


    }

    //GETS LAST ORDER ID
    public static int getLastOrderId() throws SQLException {
        Statement stmt = c.createStatement();
        String query = "SELECT MAX(ORDER_ID) AS MAX_ORDER_ID FROM ORDERS";
        ResultSet rs = stmt.executeQuery(query);
        int lastOrderId = 0;

        if (rs.next()) {
            lastOrderId = rs.getInt("MAX_ORDER_ID");
        }

        rs.close();
        stmt.close();
        return lastOrderId;
    }

    //GETS LAST PRODUCT NAME
    public static String getLastProductName() throws SQLException {

        Statement stmt = c.createStatement();
        String query = "SELECT NAME FROM ORDERS WHERE ORDER_ID = (SELECT MAX(ORDER_ID) FROM ORDERS)";
        ResultSet rs = stmt.executeQuery(query);

        String lastProductName = null;

        if (rs.next()) {
            lastProductName = rs.getString("NAME");
        }

        rs.close();
        stmt.close();
        return lastProductName;
    }

    //GETS LAST QUANTITY
    public static int getLastQuantity() throws SQLException {

        Statement stmt = c.createStatement();
        String query = "SELECT QUANTITY FROM ORDERS WHERE ORDER_ID = (SELECT MAX(ORDER_ID) FROM ORDERS)";
        ResultSet rs = stmt.executeQuery(query);

        int lastQuantity = 0;

        if (rs.next()) {
            lastQuantity = rs.getInt("QUANTITY");
        }

        rs.close();
        stmt.close();
        return lastQuantity;
    }


    //GETS DETAILS OF PRODUCTS WITH STATUS PENDING
    public static List<String> getProductsAndQuantitiesWithPendingStatus() throws SQLException {

        List<String> productsAndQuantitiesWithPendingStatus = new ArrayList<>();

        try {
            // Prepare the SQL statement for fetching product information
            String selectProductsSQL = "SELECT NAME, QUANTITY FROM ORDERS WHERE STATUS = 'pending'";

            // Create a prepared statement
            PreparedStatement pstmt = c.prepareStatement(selectProductsSQL);

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();

            // Populate the list with product name and quantity pairs
            while (resultSet.next()) {
                String productName = resultSet.getString("NAME");
                int quantity = resultSet.getInt("QUANTITY");

                String productAndQuantity = productName + " : " + quantity + " units";
                productsAndQuantitiesWithPendingStatus.add(productAndQuantity);
            }

            // Close the result set, prepared statement, and connection
            resultSet.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsAndQuantitiesWithPendingStatus;
    }

    //AVAILABLE PRODUCTS FROM DATABASE  PRODUCTS
    public static HashMap<String, Integer> findAvailableProductsAndQuantitiesFromDatabase() {
        HashMap<String, Integer> availableProductList = new HashMap<String, Integer>();
        try {


            // Prepare the SQL statement for fetching product information
            String selectProductsSQL = "SELECT NAME, CURRENT_STOCK_QUANTITY FROM PRODUCTS";

            // Create a prepared statement
            PreparedStatement pstmt = c.prepareStatement(selectProductsSQL);

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();


            // Populate the availableProductList from the database results
            while (resultSet.next()) {
                String productName = resultSet.getString("NAME");
                int stockQuantity = resultSet.getInt("CURRENT_STOCK_QUANTITY");
                availableProductList.put(productName, stockQuantity);
            }

            // Close the result set, prepared statement, and connection
            resultSet.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableProductList;
    }


    //CHECKS IF RESTOCKING IS REQUIRED
    public static String checkRestockRequirement(String productName) {

        try {

            // Prepare the SQL statement to fetch restock information
            String selectRestockSQL = "SELECT TARGET_MIN_STOCK_QUANTITY, CURRENT_STOCK_QUANTITY FROM PRODUCTS WHERE NAME = ?";
            PreparedStatement pstmtRestock = c.prepareStatement(selectRestockSQL);
            pstmtRestock.setString(1, productName);

            // Execute the query to get the minimum stock quantity for the product
            ResultSet resultSetRestock = pstmtRestock.executeQuery();
            int minStockQuantity = resultSetRestock.getInt("TARGET_MIN_STOCK_QUANTITY");
            int currentStockQuantity = resultSetRestock.getInt("CURRENT_STOCK_QUANTITY");

            // Close the result set and prepared statement
            resultSetRestock.close();
            pstmtRestock.close();

            // Check if restocking is required
            if (currentStockQuantity <= minStockQuantity) {
                return "YES ";
            } else {
                return "No";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error checking restock requirement";
        }
    }
}

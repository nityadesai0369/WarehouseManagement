package UC1;

import java.sql.*;
import java.util.HashMap;


public class AvailableProducts {
	private static AvailableProducts instance = null;
	
	private static HashMap<String, Integer> availableProductList = new HashMap<String, Integer>();

	public HashMap<String, Integer> getAvailableProductList() {
		return availableProductList;
	}

	public static AvailableProducts getInstance() {
		if (instance == null)
			instance = new AvailableProducts();
		

		return instance;
	}

	public static int getmax(String productName) throws SQLException {
		return SQLiteJDBC.getMaxStock(productName);
	}

}

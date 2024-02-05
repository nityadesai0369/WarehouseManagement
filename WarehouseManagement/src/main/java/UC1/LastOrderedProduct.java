package UC1;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class LastOrderedProduct {

	private static LastOrderedProduct instance = null;
	private String productName;
	private int quantity;
	private LocalDateTime date;

	//Singleton Pattern
	private LastOrderedProduct() {
	}

	public static LastOrderedProduct getInstance() {
		if (instance == null)
			instance = new LastOrderedProduct();

		return instance;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public LastOrderedProduct findLastOrder() throws SQLException {
		productName = SQLiteJDBC.getLastProductName();
		quantity = SQLiteJDBC.getLastQuantity();
		date = LocalDateTime.now();

		return this;
	}
}

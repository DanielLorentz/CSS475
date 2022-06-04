package CSS475;

import java.sql.SQLException;
import java.time.LocalDate;

public class Tester {

	public Tester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws SQLException {
		WarehouseInventory wi = new WarehouseInventory();
		// Test each method
		System.out.println(wi.warehouseCheckInventory(0, 10));
		String barcode = "";
		String locationID = "";
		System.out.println(wi.warehouseUpdateLocation(barcode, locationID));
		System.out.println(wi.warehouseExpired());
		LocalDate date = LocalDate.now();
		System.out.println(wi.warehouseExpireSoon(date));
	}
}

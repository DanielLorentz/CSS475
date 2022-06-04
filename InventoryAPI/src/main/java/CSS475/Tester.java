package CSS475;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class Tester {
	public static WarehouseInventory wi = new WarehouseInventory();
	public Tester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws SQLException {
		
		// Test each method
//		testCheckInventory();
		testUpdateLocation();
//		testWarehouseExpired();
//		testWarehouseExpireSoon();
	}
	
	public static void testCheckInventory() {
		System.out.println(wi.warehouseCheckInventory(0, 10));
	}
	
	public static void testUpdateLocation() {
		String barcode = "31068494";
		// DB, SB, NS, CS, FS, SH, GB
		String locationID = "GB";
//		System.out.println(wi.warehouseUpdateLocation(barcode, locationID));
		System.out.println(wi.validLocationID(locationID));
	}
	
	public static void testWarehouseExpired() {
		System.out.println(wi.warehouseExpired());
	}
	
	public static void testWarehouseExpireSoon() {
		Date date = Date.valueOf("2022-05-15");
		System.out.println(date);
		System.out.println(wi.warehouseExpireSoon(date));
	}
}

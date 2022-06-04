package CSS475;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class WarehouseInventory {
	static Connection conn;
	static final String DB_Address = "73.59.46.239";
	static final String USER = "inventory";
	static final String PASS = "12345678";
	static final boolean debug = false;

	public WarehouseInventory() {
		
	}
	
	private boolean connect() {
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://" + DB_Address + ":5432/project", USER, PASS);
			println("Connection Established.");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void commit() {
		try {
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void rollback() {
		try {
			conn.rollback();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ArrayList<String>> warehouseCheckInventory(int start, int end) {
		if (!connect()) return null;
		ArrayList<ArrayList<String>> listOfItem = new ArrayList<>();
		String query = "SELECT *\r\n" + "FROM WarehouseItem\r\n" + 
				"WHERE LocationID != 'SH' AND LocationID != 'GB'\r\n" + 
				"ORDER BY ID\r\n" + 
				"LIMIT ?\r\n" + 
				"OFFSET ?\r\n;";

		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			// set autocommit to false (transaction)
			conn.setAutoCommit(false);
			stmt.setInt(1, end - start);
			stmt.setInt(2, start);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ArrayList<String> row = new ArrayList<>();
				int restaurantID = rs.getInt("Id");
				String orderid = rs.getString("orderid");
				String barcode = rs.getString("barcode");
				String locationid = rs.getString("locationid");
				String catalogid = rs.getString("catalogid");
				String arrivaldate = rs.getString("arrivaldate");
				String expirationdate = rs.getString("expirationdate");
				println("RestaurantID = " + restaurantID);
				println("OrderID = " + orderid);
				println("Barcode = " + barcode);
				println("LocationID = " + locationid);
				println("CatalogID = " + catalogid);
				println("ArrivalDate = " + arrivaldate);
				println("ExpirationDate = " + expirationdate);
				println();
				row.add(barcode);
				row.add(locationid);
				listOfItem.add(row);
			}
			
			// commit changes. If reached this part no error occurred in insert
			commit();
			return listOfItem;
		}
		// error occurred in insert, rollback transaction and return false
		catch (SQLException e) {
			rollback();
			return null;
		}
	}

	/**
	 * 
	 * @param barcode
	 * @param locationID - Two char.
	 * @return True if updated successfully
	 */
	public boolean warehouseUpdateLocation(String barcode, String locationID) {
		if (!validLocationID(locationID)) return false;
		if (!connect()) return false;
		String update = "UPDATE WarehouseItem\r\n"
				+ "SET LocationID = ?\r\n"
				+ "WHERE Barcode = ?;";

		try (PreparedStatement stmt = conn.prepareStatement(update);) {
			// set autocommit to false (transaction)
			conn.setAutoCommit(false);
			stmt.setString(1, locationID);
			stmt.setString(2, barcode);
			int rs = stmt.executeUpdate();
			println("Lines updated = " + rs);
			// commit changes. If reached this part no error occurred in insert
			commit();
			return true;
		}
		// error occurred in insert, rollback transaction and return false
		catch (SQLException e) {
			rollback();
			return false;
		}
	}
	
	private boolean validLocationID(String locationID) {
		if (!connect()) return false;
		String query = "SELECT FROM WarehouseLocation\r\n"
				+ "WHERE ID = ?\r\n;";

		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			// set autocommit to false (transaction)
			conn.setAutoCommit(false);
			stmt.setString(1, locationID);
			ResultSet rs = stmt.executeQuery();
			// commit changes. If reached this part no error occurred in insert
			commit();
			return rs.isBeforeFirst();
		}
		// error occurred in insert, rollback transaction and return false
		catch (SQLException e) {
			rollback();
			return false;
		}
	}

	public ArrayList<ArrayList<String>> warehouseExpired() {
		Date date = Date.valueOf(LocalDate.now());
		if (!connect()) return null;
		ArrayList<ArrayList<String>> listOfItem = new ArrayList<>();
		String query = "SELECT *\r\n"
				+ "FROM WarehouseItem\r\n"
				+ "WHERE LocationID != 'SH' AND LocationID != 'GB' AND ExpirationDate < ?\r\n"
				+ "ORDER BY expirationDate DESC;";

		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			// set autocommit to false (transaction)
			conn.setAutoCommit(false);
			stmt.setDate(1, date);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ArrayList<String> row = new ArrayList<>();
				int restaurantID = rs.getInt("Id");
				String orderid = rs.getString("orderid");
				String barcode = rs.getString("barcode");
				String locationid = rs.getString("locationid");
				String catalogid = rs.getString("catalogid");
				String arrivaldate = rs.getString("arrivaldate");
				String expirationdate = rs.getString("expirationdate");
				println("RestaurantID = " + restaurantID);
				println("OrderID = " + orderid);
				println("Barcode = " + barcode);
				println("LocationID = " + locationid);
				println("CatalogID = " + catalogid);
				println("ArrivalDate = " + arrivaldate);
				println("ExpirationDate = " + expirationdate);
				println();
				row.add(barcode);
				row.add(expirationdate);
				listOfItem.add(row);
			}
			
			// commit changes. If reached this part no error occurred in insert
			commit();
			return listOfItem;
		}
		// error occurred in insert, rollback transaction and return false
		catch (SQLException e) {
			rollback();
			return null;
		}
	}

	public ArrayList<ArrayList<String>> warehouseExpireSoon(Date date) {
		if (!connect()) return null;
		ArrayList<ArrayList<String>> listOfItem = new ArrayList<>();
		String query = "SELECT *\r\n"
				+ "FROM WarehouseItem\r\n"
				+ "WHERE ExpirationDate < ? AND LocationID != 'SH' AND LocationID != 'GB'\r\n"
				+ "ORDER BY expirationDate DESC;";

		try (PreparedStatement stmt = conn.prepareStatement(query);) {
			// set autocommit to false (transaction)
			conn.setAutoCommit(false);
			stmt.setDate(1, date);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ArrayList<String> row = new ArrayList<>();
				int restaurantID = rs.getInt("Id");
				String orderid = rs.getString("orderid");
				String barcode = rs.getString("barcode");
				String locationid = rs.getString("locationid");
				String catalogid = rs.getString("catalogid");
				String arrivaldate = rs.getString("arrivaldate");
				String expirationdate = rs.getString("expirationdate");
				println("RestaurantID = " + restaurantID);
				println("OrderID = " + orderid);
				println("Barcode = " + barcode);
				println("LocationID = " + locationid);
				println("CatalogID = " + catalogid);
				println("ArrivalDate = " + arrivaldate);
				println("ExpirationDate = " + expirationdate);
				println();
				row.add(barcode);
				row.add(expirationdate);
				listOfItem.add(row);
			}
			
			// commit changes. If reached this part no error occurred in insert
			commit();
			return listOfItem;
		}
		// error occurred in insert, rollback transaction and return false
		catch (SQLException e) {
			rollback();
			return null;
		}
	}

	private void println(String in) {
		if (debug) System.out.println(in);
	}
	
	private void println() {
		if (debug) System.out.println();
	}
}

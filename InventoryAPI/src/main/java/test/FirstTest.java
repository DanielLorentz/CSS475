package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirstTest {
    static final String DB_Address = "jdbc:postgresql://73.59.46.239:5432/project";
    static final String USER = "inventory";
    static final String PASS = "12345678";

    
    public static void main (String[] args) throws ClassNotFoundException {
        // open connection
        Class.forName("org.postgresql.Driver");
        test();
        System.out.println("exit test method.");
        boolean result = WarehouseRemoveExpired();
        System.out.println(result);
    }
    
    public static void test() {
    	System.out.println("entered test method.");
    	try(
                Connection conn = DriverManager.getConnection(DB_Address, USER, PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM warehouseLocation"); ){
            //extract data
            while (rs.next()) {
                System.out.print("ID = " + rs.getString("id"));
                System.out.print(", Name = " + rs.getString("name"));
                System.out.print(", Description = " + rs.getString("description"));
                System.out.println();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        
        }
    }
    
    /* WarehouseAddInventory
     * @param catalogID = int PK of catalog
     * @param barcode = string(10) barcode id
     * @param expirationDate = string date in "year-month-day" format
     * Adds item to warehouseInventory. ArrivalDate set to 'now', locationID set to 'incoming bay'
     * @returns bool. true if successful
     * Author: Daniel Lorentz
     */
    public static boolean WarehouseAddItem(int catalogID, String barcode, String exirationDate){
    	// construct CRUD insert
    	String update = "INSERT INTO warehouseItem (barcode, locationID, catalogID, arrivalDate, expirationDate)"
    			+ "VALUES ('" + barcode +"', 'DB', '" + catalogID + "', now()::TimeStamp(0), '" + exirationDate + "')"; 
    	// create connection to DB. Has it's own try block to facilitate transactions
    	try(Connection conn = DriverManager.getConnection(DB_Address, USER, PASS);){
    		// generate statement 
    		try(Statement stmt = conn.createStatement();){
    			// set autocommit to false (transaction)
	    		conn.setAutoCommit(false);
	    		//execute insert. Note: JBDC uses the same method call for updates and inserts
	    		stmt.executeUpdate(update); 
	    		
	            // commit changes. If reached this part no error occurred in insert
	            conn.commit();
	            conn.close();
	            //return true
	            return true;
	        // error occurred in insert, rollback transaction and return false
	        } catch (SQLException e) {
				conn.rollback();
				conn.close();
				return false;
	        }
    	// error occurred while establishing connection, return false
    	} catch (SQLException ex) {
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    /* AddTrackingNumber
     * @param OrderID = int id of order
     * @param TrackingNumber = String tracking number from third party shipping vendor
     * Updates an order to include it's tracking number
     * @returns bool. true if successful
     * Author: Daniel Lorentz
     */
    public static boolean AddTrackingNumber(int catalogID, String trackingNumber){
    	String update = "UPDATE order_table SET tracking_number = '" + trackingNumber + "', status = 'SH' WHERE id = " + catalogID;
    	// create connection to DB. Has it's own try block to facilitate transactions
    	try(Connection conn = DriverManager.getConnection(DB_Address, USER, PASS);){
    		// generate statement 
    		try(Statement stmt = conn.createStatement();){
    			// set autocommit to false (transaction)
	    		conn.setAutoCommit(false);
	    		//execute insert. Note: JBDC uses the same method call for updates and inserts
	    		stmt.executeUpdate(update); 
	    		
	            // commit changes. If reached this part no error occurred in insert
	            conn.commit();
	            conn.close();
	            //return true
	            return true;
	        // error occurred in insert, rollback transaction and return false
	        } catch (SQLException e) {
				conn.rollback();
				conn.close();
				return false;
	        }
    	// error occurred while establishing connection, return false
    	} catch (SQLException ex) {
    		ex.printStackTrace();
    		return false;
    	}
    
    }
    
    //TODO Remove all test code before final push
    /* WarehouseRemoveExpired
     * Updates all warehouseItem where experation date < "now" to locaitonID disposed
     * @returns bool. true if successful
     * Author: Daniel Lorentz
     */
    public static boolean WarehouseRemoveExpired(){
    	String update = "UPDATE warehouseItem SET locationID = 'GB' WHERE warehouseItem.expirationDate <= now() AND locationID != 'SH' AND locationID != 'GB'";
    	String testQuery = "SELECT * FROM warehouseItem WHERE warehouseItem.expirationDate <= 'now' AND (locationID != 'SH' AND locationID != 'GB') LIMIT 10";
    	// create connection to DB. Has it's own try block to facilitate transactions
    	try(Connection conn = DriverManager.getConnection(DB_Address, USER, PASS);){
    		// generate statement 
    		try(Statement stmt = conn.createStatement();){
    			// set autocommit to false (transaction)
	    		conn.setAutoCommit(false);
	    		
	    			// TESTINT QUERY!!!!
	    			System.out.println("first test query");
	    			ResultSet rs = stmt.executeQuery(testQuery);
	    			while(rs.next()) {
	    				System.out.print("id = " + rs.getString("id"));
	    				System.out.print(", catalogID = " + rs.getInt("catalogID"));
	    				System.out.print(", expirationDate = " + rs.getDate("expirationDate"));
	    				System.out.print(", locationID = " + rs.getString("locationID"));
	    				System.out.println();
	    			}
	    		//execute insert. Note: JBDC uses the same method call for updates and inserts
	    		System.out.println("update");
	    		stmt.executeUpdate(update); 
	    		
	    			//TESTING QUERY!!!!!
		    		System.out.println("second test query");
		    		rs = stmt.executeQuery(testQuery);
	    			while(rs.next()) {
	    				System.out.print("id = " + rs.getString("id"));
	    				System.out.print(", catalogID = " + rs.getInt("catalogID"));
	    				System.out.print(", expirationDate = " + rs.getDate("expirationDate"));
	    				System.out.print(", locationID = " + rs.getString("locationID"));
	    				System.out.println();
	    			}
	            // commit changes. If reached this part no error occurred in insert
	            //conn.commit();
	    		conn.rollback(); //TODO:: comment this out and return to commit. set up this way for testing due to annoyance in resetting all table data
	    		//return true
	    		conn.close();
	            return true;
	        // error occurred in insert, rollback transaction and return false
	        } catch (SQLException e) {
	        	e.printStackTrace();
				conn.rollback();
				conn.close();
				return false;
	        }
    	// error occurred while establishing connection, return false
    	} catch (SQLException ex) {
    		ex.printStackTrace();
    		return false;
    	}
    
    }
    
}

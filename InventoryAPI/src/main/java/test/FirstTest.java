package test;
//
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import CSS475.WarehouseInventory;

public class FirstTest {
    static final String DB_Address = "jdbc:postgresql://73.59.46.239:5432/project";
    static final String USER = "inventory";
    static final String PASS = "12345678";
    static final WarehouseInventory wi = new WarehouseInventory();

    //
    public static void main (String[] args) throws ClassNotFoundException { 
        // open connection
        Class.forName("org.postgresql.Driver");
        
        TestRestaurantInventory restJ = new TestRestaurantInventory();
        test();
        System.out.println("exit test method.");

        
        
        ArrayList<String> res = new ArrayList<String>();
        // input vars
        int restaurantId, orderId, catalogId, quantity;
        String name, description, status, startDate, trackingNumber, expirationDate, barcode, locationId;
        
        // create scanner for UI input
        Scanner in = new Scanner(System.in);
        int nextInput = 20;
        while(nextInput != 0) {
        	try {
        	switch (nextInput) {
        		// AddCatalogItem
        		case 1: 	System.out.print("Catalog item name: ");
        					name = in.next();
        					System.out.print("Catalog item description: ");
        					description = in.next();
        					if(AddCatalogItem(name, description)) {
        						System.out.println("Item added to catalog");
        					} else {
        						System.out.println("Item not added, please check inputs and try again");
        					}
	        				break;
        		// MakeOrder
        		case 2: 	System.out.print("RestaurantID: ");
        					restaurantId = in.nextInt();
        					int result = makeOrder(restaurantId);
        					if (result > -1) {
        						System.out.println("Order number = " + result);
        					} else {
        						System.out.println("Order not made, please check inputs and try again");
        					}
	        				break;
        		// AddToOrder
        		case 3: 	System.out.print("OrderID: ");
        					orderId = in.nextInt();
        					System.out.print("CatalogID: ");
        					catalogId = in.nextInt();
        					System.out.print("Quantity: ");
        					quantity = in.nextInt();
        					if(addToOrder(orderId, catalogId, quantity)) {
        						System.out.println("Items added to order");
        					} else {
        						System.out.println("Items not added to order");
        					}
	        				break;
        		// OrderStatus
	        	case 4: 	System.out.print("OrderID: ");
	        				orderId = in.nextInt();
	        				orderStatus(orderId);
	        				break;
        		// UpdateStatus
        		case 5:		System.out.print("RestaurantID: ");
        					restaurantId = in.nextInt();
        					System.out.print("Barcode (int 10): ");
        					barcode = in.next();
        					System.out.print("Status (Char 2): ");
        					status = in.next();
        					status.toUpperCase();
        					if(updateStatus(restaurantId, barcode, status)) {
        						System.out.println("Item status updated");
        					} else {
        						System.out.println("Item status not updated, please check inputs and try again");
        					}
	        				break;
        		// UpdateLocation
        		case 6:		System.out.print("LocationID (Char 2):");
        					locationId = in.next();
        					locationId.toUpperCase();
        					System.out.print("Barcode (int 10): ");
        					barcode = in.next();
        					updateLocation(locationId, barcode);
        					System.out.println("Item location updated");
	        				break;
        		// UpdateRestaurantItem
        		case 7:		System.out.print("Barcode (int 10): ");
        					barcode = in.next();
        					System.out.print("ExpirationDate ('YYYY-MM-DD'): ");
        					expirationDate = in.next();
        					if(updateRestaurantItem(barcode, expirationDate)) {
        						System.out.println("Item updated");
        					} else {
        						System.out.println("Item not updated, please check inputs");
        					}
	        				break;
        		// RestaurantExpired
	        	case 8:		System.out.print("RestaurantID: ");
	        				restaurantId = in.nextInt();
	        				restaurantExpired(restaurantId);
	        				break;
        		// RestaurantExpireSoon
        		case 9:		System.out.print("RestaurantID: ");
        					restaurantId = in.nextInt();
        					System.out.print("Date ('YYYY-MM-DD'): ");
        					expirationDate = in.next();
        					res = TestRestaurantInventory.RestaurantExpireSoon(restaurantId, expirationDate);
        					System.out.println(res);
        					break;
        		// RestaurantCheckInventory
        		case 10:	System.out.print("RestaurantID: ");
        					restaurantId = in.nextInt();
        					res = TestRestaurantInventory.RestaurantCheckInventory(restaurantId);
        					System.out.println(res);
        					break;
        		// RestaurantUsageReport
        		case 11:	System.out.print("RestaurantID: ");
        					restaurantId = in.nextInt();
        					System.out.print("Start date ('YYYY-MM-DD'): ");
        					startDate = in.next();
        					System.out.print("End date ('YYYY-MM-DD'): ");
        					expirationDate = in.next();
        					res = TestRestaurantInventory.RestaurantUsageReport(restaurantId, startDate, expirationDate);
        					System.out.println(res);
        					break;
        		// RestaurantRemoveExpired
        		case 12:	System.out.print("RestaurantID: ");
        					restaurantId = in.nextInt();
        					res = TestRestaurantInventory.RestaurantRemoveExpired(restaurantId);
        					System.out.print(res);
        					break;
        		// WarehouseCheckInventory
        		case 13:	System.out.println("Number of items to display: ");
        					int toDisplay = in.nextInt();
        					for (ArrayList<String> i : wi.warehouseCheckInventory(0, toDisplay)) {
        						System.out.println(i);
        					}
        					break;
        		// WarehouseUpdateLocation
        		case 14:	System.out.println("Barcode (int 10): ");
        					barcode = in.next();
        					System.out.println("New LocationID: ");
        					String newLocationID = in.next().toUpperCase();
        					boolean valid = wi.validLocationID(newLocationID);
        					while(!valid) {
        						System.out.println("Invalid LocationID. Try again.");
        						System.out.println("New LocationID: ");
            					newLocationID = in.next();
            					valid = wi.validLocationID(newLocationID);
        					}
        					if (wi.warehouseUpdateLocation(barcode, newLocationID)) {
        						System.out.println("Item location successfully updated.");
        					} else {
        						System.out.println("Item not found. Failed to update item location.");
        					}
        					break;
        		// WarehouseExpired
        		case 15:	
        					for (ArrayList<String> i : wi.warehouseExpired()) {
        						System.out.println(i);
        					}
        					break;
        		// WarehouseExpireSoon
        		case 16:	System.out.println("Date item Expires before (YYYY-MM-DD): ");
        					String expireBefore = in.next();
        					for (ArrayList<String> i : wi.warehouseExpireSoon(Date.valueOf(expireBefore))) {
        						System.out.println(i);
        					}
        					break;
        		// WarehouseAddInventory
        		case 17:	System.out.print("CatalogID:");
        					catalogId = in.nextInt();
        					System.out.print("Barcode (int 10): ");
        					barcode = in.next();
        					System.out.print("ExpirationDate ('YYYY-MM-DD'): ");
        					expirationDate = in.next();
        					if(warehouseAddItem(catalogId, barcode, expirationDate)) {
        						System.out.println("Item added to warehouse inventory");
        					} else {
        						System.out.println("Item not added to warehouse inventory, please check inputs and try again");
        					}
        					break;
        		// AddTrackingNumber
        		case 18:	System.out.print("OrderID: ");
        					orderId = in.nextInt();
        					System.out.print("TrackingNumber: ");
        					trackingNumber = in.next();
        					if(addTrackingNumber(orderId, trackingNumber)) {
        						System.out.println("Tracking number added to order");
        					} else {
        						System.out.println("An error occured while adding the tracking number, please check your inputs and try again");
        					}
        					break;
        		// WarehouseRemoveExpired
        		case 19:	if(warehouseRemoveExpired()) {
        						System.out.println("LocationID for all expired items in the warehouse changed to garbage");
        					} else {
        						System.out.println("An error occured, please try again");
        					}
        					break;
        		// Options
        		case 20:	System.out.println("Please enter the number for one of the following options and then give the arguments in order:");
        					System.out.println("      0: quit demo");
        					System.out.println("      1: addCatalogItem(name, description)");
        					System.out.println("      2: makeOrder(restaurantID)");
        					System.out.println("      3: addToOrder(orderID, catalogID, quantity)");
        					System.out.println("      4: orderStatus(orderID)");
        					System.out.println("      5: updateStatus(restaurantID, barcode, (char(2)) status)");
        					System.out.println("      6: updateLocation((char(2)) ID, barcode)");
        					System.out.println("      7: updateRestaurantItem(barcode, expirationDate)");
        					System.out.println("      8: restaurantExpired(restaurantID)");
        					System.out.println("      9: restaurantExpireSoon(restaurantID, date)");
        					System.out.println("     10: restaurantCheckInventory(restaurantID)");
        					System.out.println("     11: restaurantUsageReport(restaurantID, startDate, endDate");
        					System.out.println("     12: restaurantRemoveExpired(restaurantID)");
        					System.out.println("     13: warehouseCheckInventory()");
        					System.out.println("     14: warehouseUpdateLocation(barcode, (char(2)) locationID)");
        					System.out.println("     15: warehouseExpired()");
        					System.out.println("     16: warehouseExpireSoon(date)");
        					System.out.println("     17: warehouseAddInventory(catalogID, barcode, expirationDate)");
        					System.out.println("     18: addTrackingNumber(orderID, trackingNumber)");
        					System.out.println("     19: warehouseRemoveExpired()");
        					System.out.println("     20: review available options");
        					break;
        	}
        	nextInput = in.nextInt();
        	
        }catch (InputMismatchException ei) {
        	nextInput = in.nextInt();
        }
        }
        
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
    public static boolean warehouseAddItem(int catalogID, String barcode, String exirationDate){
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
    public static boolean addTrackingNumber(int catalogID, String trackingNumber){
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
    public static boolean warehouseRemoveExpired(){
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

/**
 * @param orderID
 * OrderStatus( int OrderID)
 * Description: OrderID is the id given from making the order.
 * Returns a table of the order’s ID, 
 * date made, third-party tracking number, and status.
 * 
 * */
public static void orderStatus(int orderID){
    	try(
                Connection conn = DriverManager.getConnection(DB_Address, USER, PASS);
                Statement stmt = conn.createStatement();
    			//query to get the status of a particular order
                ResultSet rs = stmt.executeQuery("select order_table.id, order_table.date, "
                		+ "tracking_number, description from order_table                                                                         join order_status on (order_status.id = order_table.status) \r\n"
                		+ "Where order_table.id = ' " + orderID + " ' "
                				+ "order by order_table.date limit 10;\r\n"
                		+ ""); ){
            //extract data
            while (rs.next()) {
                //displaying all the order status
            	System.out.print("ID = " + rs.getString("id"));
                System.out.print(", Date = " + rs.getString("date"));
                System.out.print(", Tracking Number = " + rs.getString("tracking_number"));
                System.out.print(", Description = " + rs.getString("description"));

                System.out.println();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        
        }
    }


/* UpdateStatus
 * brief:Updates the item with barcode to the new status. 
 * Returns ‘true’ if successful.
 * @param RestaurantID = int id of restaurantItem
 * @param  Barcode = String unique for every item 
 * @param status = char denoting the status of current order

 */
public static boolean updateStatus(int resturantID, String barcode, String status){
	String update = "UPDATE RestaurantItem SET status = ' " + status + " 'WHERE Barcode = '" + barcode + "' AND restaurantID =  '" + resturantID + "'";
	 //= '" + trackingNumber + "'
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


/* UpdateLoacation
 * brief  Updates the location of the item. Returns the new location 
 * when the items are moved to a new location. 
 * @param resturantLocationID = int id of restaurantItem
 * @param  Barcode = String unique for every item 
 * @param status = char denoting the status of current order
 */
public static void updateLocation(String resturantLocationID, String barcode){
	String update = "UPDATE RestaurantItem SET locationID = '" + resturantLocationID + " 'Where Barcode = ' "  + resturantLocationID + " '";

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
            //return resturantLocationID;
            
        // error occurred in insert, rollback transaction and return false
        } catch (SQLException e) {
			conn.rollback();
			conn.close();
			
        }
	// error occurred while establishing connection, return false
	} catch (SQLException ex) {
		ex.printStackTrace();
		
	}

}


/* UpdateRestaurantItem
 * brief updates the items ordered into RestaurantItem
 *  return true if successful. 
 * @param expirationDate = date expirationDate of a restaurantItem
 * @param  Barcode = String unique for every item 
 */
public static boolean updateRestaurantItem(String barcode, String expirationDate){
	String update = "Update RestaurantItem SET expirationDate =  '" + expirationDate + "' WHERE Barcode = " + barcode;
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

public static void restaurantExpired( int restaurantID)  {
	try(
            Connection conn = DriverManager.getConnection(DB_Address, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Select* \r\n"
            		+ "From restaurantItem \r\n"
            		+ "Join restaurantLocation on (restaurantLocation.id = RestaurantItem.locationId)\r\n"
            		+ "Join restaurantStatus on (restaurantStatus.id = restaurantItem.status)\r\n"
            		+ "Where (RestaurantItem.locationId is not null) and (RestaurantItem.id =  '" + restaurantID+ "') "
            		+ "and (RestaurantItem.expirationDate <=  now()::TimeStamp(0));"); ){
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

/* AddCatalogItem
 * @param catalogID = int PK of catalog
 * @param catalogName = name of catalog
 * @param catalogDescription = a description of the catalog
 * @returns boolean. true if successful

 */
public static boolean AddCatalogItem(String catalogName, String catalogDescription){
	// construct CRUD insert
	String update = "INSERT INTO catalog (name, description)"
			+ " VALUES ( '" + catalogName +"', '" + catalogDescription +"')"; 
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


/**
 * Description: Restaurant creates an order with its restaurantID. 
 * Returns the order id if successfully placed. 
 * */
public static int makeOrder( int restaurantID) {
	String update = "INSERT INTO Order_table (restaurantID, date, status)"
			+ "VALUES ('" + restaurantID + "', now()::TimeStamp(0), 'PL')" ;
	String testQuery = "SELECT id,  date,  tracking_number, status, restaurantID "
			+ "FROM order_table "
			+ "Where restaurantID = ' " + restaurantID + "' "
			+ "Order by id desc "
			+ "Limit 10";
	String resultQuery = "SELECT id FROM order_table WHERE restaurantID = '" + restaurantID + "' ORDER BY id DESC LIMIT 1";
	int returnOrderNumber = -1;
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
    				System.out.print(", date = " + rs.getDate("date"));
    				System.out.print(", tracking_number = " + rs.getString("tracking_number"));
    				System.out.print(", status = " + rs.getString("status"));
    				System.out.print(", restaurantID = " + rs.getString("restaurantID"));
    				System.out.println();
    			}
    		//execute insert. Note: JBDC uses the same method call for updates and inserts
    		System.out.println("update");
    		stmt.executeUpdate(update); 
    		rs = stmt.executeQuery(resultQuery);
    		rs.next();
    		returnOrderNumber = rs.getInt("id");
    		
    			//TESTING QUERY!!!!!
	    		System.out.println("second test query");
	    		rs = stmt.executeQuery(testQuery);
	    		while(rs.next()) {
    				System.out.print("id = " + rs.getString("id"));
    				System.out.print(", date = " + rs.getDate("date"));
    				System.out.print(", tracking_number = " + rs.getString("tracking_number"));
    				System.out.print(", status = " + rs.getString("status"));
    				System.out.print(", restaurantID = " + rs.getString("restaurantID"));
    				System.out.println();
    			}
    			
            // commit changes. If reached this part no error occurred in insert
            //conn.commit();
    		conn.rollback(); //TODO:: comment this out and return to commit. set up this way for testing due to annoyance in resetting all table data
    		//return true
    		conn.close();
            return returnOrderNumber;
        // error occurred in insert, rollback transaction and return false
        } catch (SQLException e) {
        	e.printStackTrace();
			conn.rollback();
			conn.close();
			return returnOrderNumber;
        }
	// error occurred while establishing connection, return false
	} catch (SQLException ex) {
		ex.printStackTrace();
		return returnOrderNumber;
	}

}

/**
 * Description: Adds quantity number of an item to an order that has not been shipped. 
 * Returns true if successful and the number of items added, otherwise false if not successful. 
 * */
public static boolean addToOrder( int orderID, int catalogID, int quantity) {
	String update = "UPDATE warehouseItem  "
			+ "SET  orderid = ' " + orderID +  "'WHERE warehouseItem.id = \r\n"
			+ "(Select id From warehouseItem  Where (orderID is null) and (locationid is not null) and (warehouseitem.catalogid = ' " + catalogID + "')\r\n"
			+ "Limit 1);\r\n"
			+ "" ;
	
	
	String testQuery = "Select id, barcode, catalogid, arrivaldate, expirationDate "
			+ "From warehouseItem \r\n"
			+ "Where (orderID is null) and (locationid is not null) and (warehouseitem.catalogid = CatalogID)\r\n"
			+ "Limit 1;\r\n"
			+ "";
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
    				System.out.print(", barcode = " + rs.getString("barcode"));
    				System.out.print(", catalogID = " + rs.getInt("catalogID"));
    				System.out.print(", arrivaldate = " + rs.getDate("arrivaldate"));
    				System.out.print(", expirationDate = " + rs.getDate("expirationDate"));
    				
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
    				System.out.print(", barcode = " + rs.getString("barcode"));
    				System.out.print(", catalogID = " + rs.getInt("catalogID"));
    				System.out.print(", arrivaldate = " + rs.getDate("arrivaldate"));
    				System.out.print(", expirationDate = " + rs.getDate("expirationDate"));
    				
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
//end
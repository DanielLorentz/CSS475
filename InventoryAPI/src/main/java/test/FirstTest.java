package test;
//
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirstTest {
    static final String DB_Address = "jdbc:postgresql://73.59.46.239:5432/project";
    static final String USER = "inventory";
    static final String PASS = "12345678";

    //
    public static void main (String[] args) throws ClassNotFoundException { 
        // open connection
        Class.forName("org.postgresql.Driver");
        test();
        System.out.println("exit test method.");
        boolean result = WarehouseRemoveExpired();
        System.out.println(result);
        
        System.out.println("getting the order status: for ID 10");
        orderStatus(10);
        
        System.out.println("checking expired orders with resturant id = 5");
        restaurantExpired(5);
        
        //System.out.println("Restaurant creates an order with restaurantID = 5");
        //boolean result2 = makeOrder(5);
        
        //System.out.println(result2); 
        //-- getting an error when I tried to run the query, 
        //some of the fields are becoming null for some reason. 
        //not sure how to fix it...
        
        System.out.println("adding an order of id = , catalogid = 3, and quantity of 10");
        boolean result3 = addToOrder(2, 3, 10);
        System.out.println(result3);
        
        System.out.println("checking the status of an order, given orderid = 5");
        orderStatus(5);
        
        
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
public static boolean updateStatus(int resturantID, String barcode, char status){
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
public static void updateLocation(char resturantLocationID, String barcode){
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
public static boolean UpdateRestaurantItem(String barcode, String expirationDate){
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
public static boolean makeOrder( int restaurantID) {
	String update = "INSERT Order_table"
			+ "VALUES ('" + restaurantID + "')" ;
	String testQuery = "Select restaurantItem.id,  arrivaldate,  expirationdate, barcode\r\n"
			+ "from order_table \r\n"
			+ "Join restaurantItem on (order_table.id = restaurantItem.id )\r\n"
			+ "Where restaurantItem.id = ' " + restaurantID + "'\r\n"
			+ "Order by restaurantItem.id desc\r\n"
			+ "Limit 10;\r\n"
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
    				System.out.print(", arrivaldate = " + rs.getDate("arrivaldate"));
    				System.out.print(", expirationDate = " + rs.getDate("expirationDate"));
    				System.out.print(", barcode = " + rs.getString("barcode"));
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
    				System.out.print(", arrivaldate = " + rs.getDate("arrivaldate"));
    				System.out.print(", expirationDate = " + rs.getDate("expirationDate"));
    				System.out.print(", barcode = " + rs.getString("barcode"));
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
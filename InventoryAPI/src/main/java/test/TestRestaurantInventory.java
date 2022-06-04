package test;
import java.sql.*;
import java.util.ArrayList;

public class TestRestaurantInventory {
    static final String DB_Address = "73.59.46.239";
    static final String USER = "inventory";
    static final String PASS = "12345678";

    public static void main( String args[] ) throws SQLException {
        
    	//Test each method
    	//System.out.println(RestaurantExpireSoon(1, "2022-01-01"));
    	//System.out.println(RestaurantCheckInventory(2));
    	//System.out.println(RestaurantUsageReport(3,"2021-01-01","2021-12-01"));
    	System.out.println(RestaurantRemoveExpired(2));
    }

     /**
     * Check all items will be expired soon
     * @param RestaurantID
     * @param date
     * @return  A table of all items with that restaurantID, 
     * an expiration date before ‘date’, 
     * and a status that is not “disposed” or “consumed”
     * @author Jenny Phan
     */
    public static ArrayList<String> RestaurantExpireSoon(int RestaurantID, String date){
    	ArrayList<String> listOfItem = new ArrayList<String>();
 
        String update = "SELECT Restaurant.id, Catalog.name, ExpirationDate, "
        				+ "RestaurantStatus.name AS Status\n" +
            "FROM RestaurantItem\n" +
            "JOIN Restaurant ON (Restaurant.id = RestaurantItem.restaurantId)\n" +
            "JOIN RestaurantStatus ON (RestaurantStatus.id = RestaurantItem.status)\n" +
            "JOIN Catalog ON (Catalog.id = RestaurantItem.catalogId)\n" +
            "WHERE Restaurant.id = " + RestaurantID + "\n" +
            "AND RestaurantItem.expirationdate > '" + date + "'\n" +
            "AND RestaurantStatus.id NOT IN ('DS', 'FU')\n" +
            "ORDER BY expirationDate, name;";
        // create connection to DB. Has it's own try block to facilitate transactions
        try(Connection conn = DriverManager.getConnection("jdbc:postgresql://" + 
        DB_Address + ":5432/project", USER, PASS);){
            // generate statement
            try(Statement stmt = conn.createStatement();){
                // set autocommit to false (transaction)
                conn.setAutoCommit(false);
        
                ResultSet rs = stmt.executeQuery(update);
                while ( rs.next() ) {
	            	int restaurantID = rs.getInt("Id");
	            	String name = rs.getString("Name");
	            	String expirationDate = rs.getString("ExpirationDate");
	            	String status = rs.getString("Status");
	            	System.out.println("RestaurantID = " + restaurantID );
	                System.out.println( "Name = " + name );
	                System.out.println( "Expiration Date = " + expirationDate );
	                System.out.println( "Status = " + status );
	                System.out.println();
                listOfItem.add(name);
                    }
                // commit changes. If reached this part no error occurred in insert
                conn.commit();
                conn.close();
                return listOfItem;
                }
                // error occurred in insert, rollback transaction and return false
            catch (SQLException e) {
                e.printStackTrace();
                conn.rollback();
                conn.close();
                return listOfItem;
                }
            // error occurred while establishing connection
           }
        catch (SQLException ex) {
            ex.printStackTrace();
            return listOfItem;
            }
    }

    /**
     * Check all items in the Restaurant Inventory
     * @param RestaurantID
     * @return A table of all items in the RestaurantItem table 
     * with that restaurantID and with any status other than “disposed” or “consumed”
     * @author Jenny Phan
     */
    public static ArrayList<String> RestaurantCheckInventory( int RestaurantID)
    {
    	ArrayList<String> inventory = new ArrayList<String>();
    	String update = "SELECT Restaurant.id, Catalog.name, "
    	 		+ "RestaurantStatus.name AS Status, COUNT(RestaurantItem.id) AS Num_item\r\n"
    	 		+ "FROM RestaurantItem\r\n"
    	 		+ "JOIN Restaurant ON (Restaurant.id = RestaurantItem.restaurantId)\r\n"
    	 		+ "JOIN RestaurantStatus ON (RestaurantStatus.id = RestaurantItem.status)\r\n"
    	 		+ "JOIN Catalog ON (Catalog.id = RestaurantItem.catalogId)\r\n"
    	 		+ "WHERE Restaurant.id = " + RestaurantID + "\n"
    	 		+ "AND RestaurantStatus.id NOT IN ('DS', 'FU')\n"
    	 		+ "GROUP BY RestaurantItem.status, Restaurant.id, "
    	 		+ "Catalog.name, RestaurantStatus.name\r\n"
    	 		+ "ORDER BY name;";
         // create connection to DB. Has it's own try block to facilitate transactions
         try(Connection conn = DriverManager.getConnection("jdbc:postgresql://" + 
         DB_Address + ":5432/project", USER, PASS);){
             // generate statement
             try(Statement stmt = conn.createStatement();){
                 // set autocommit to false (transaction)
                 conn.setAutoCommit(false);
                 
                 ResultSet rs = stmt.executeQuery(update);
                 while ( rs.next() ) {
					int restaurantID = rs.getInt("Id");
					String name = rs.getString("Name");
					String status = rs.getString("Status");
					int numItem = rs.getInt("Num_item");
					System.out.println("RestaurantID = " + restaurantID );
					System.out.println( "Name = " + name );
					System.out.println( "Quantity = " + numItem );
					System.out.println( "Status = " + status );
					System.out.println();
                    inventory.add(name);
                    }
                 // commit changes. If reached this part no error occurred in insert
                 conn.commit();
                 conn.close();
                 return inventory;
                 }
                 // error occurred in insert, rollback transaction and return false
             catch (SQLException e) {
            	 e.printStackTrace();
                 conn.rollback();
                 conn.close();
                 return inventory;
                 }
             // error occurred while establishing connection
         } catch (SQLException ex) {
             ex.printStackTrace();
             return inventory;
         }
    }

    /**
     * Creates a report of all items that entered the restaurant between 
     * startDate and endDate and have a status of “disposed” or “consumed”.
     * @param RestaurantID
     * @param startDate
     * @param endDate
     * @return a table of all items that entered in the restaurant between the date
     * @author Jenny Phan
     */
    public static ArrayList<String> RestaurantUsageReport( int RestaurantID, 
    		String startDate, String endDate){
    	ArrayList<String> report = new ArrayList<String>();
    	String update = "SELECT Restaurant.id, Catalog.name, RestaurantStatus.name AS Status, "
    			+ "COUNT(RestaurantItem.id) AS Num_item\r\n"
    			+ "FROM RestaurantItem\n"
    			+ "JOIN Restaurant ON (Restaurant.id = RestaurantItem.restaurantId)\n"
    			+ "JOIN RestaurantStatus ON (RestaurantStatus.id = RestaurantItem.status)\n"
    			+ "JOIN Catalog ON (Catalog.id = RestaurantItem.catalogId)\r\n"
    			+ "WHERE Restaurant.id =" + RestaurantID + "\n"
    			+ "AND RestaurantItem.expirationdate < '" + endDate + "'\n"
    			+ "AND RestaurantItem.expirationdate > '" + startDate + "'\n"
    			+ "AND RestaurantStatus.id IN ('DS', 'FU')\n"
    			+ "GROUP BY Restaurant.id, Catalog.name, RestaurantStatus.name\n"
    			+ "ORDER BY name;";
         // create connection to DB. Has it's own try block to facilitate transactions
         try(Connection conn = DriverManager.getConnection("jdbc:postgresql://" + 
         DB_Address + ":5432/project", USER, PASS);){
             // generate statement
             try(Statement stmt = conn.createStatement();){
                 // set autocommit to false (transaction)
                 conn.setAutoCommit(false);
                 
                 ResultSet rs = stmt.executeQuery(update);
                 while ( rs.next() ) {
                 	int restaurantID = rs.getInt("Id");
                 	String name = rs.getString("Name");
                 	String status = rs.getString("Status");
                 	int numItem = rs.getInt("Num_item");
                 	System.out.println("RestaurantID = " + restaurantID );
                     System.out.println( "Name = " + name );
                     System.out.println( "Quantity = " + numItem );
                     System.out.println( "Status = " + status );
                     System.out.println();
                 report.add(name);
                 }
                 // commit changes. If reached this part no error occurred in insert
                 conn.commit();
                 conn.close();
                 return report;}
                 // error occurred in insert, rollback transaction 
             catch (SQLException e) {
                 conn.rollback();
                 conn.close();
                 return report;
                 }
             // error occurred while establishing connection
         } catch (SQLException ex) {
             ex.printStackTrace();
             return report;
         }
    }

    /**
     *  Sets the status of all items with an expiration date before “now” 
     *  to status “discarded”
     *  @param RestauranID
     *  @param date
     *  @return all expired item with "discarded" status
     *  @author Jenny Phan
     */
    public static ArrayList<String> RestaurantRemoveExpired (int RestaurantID) /*throws SQLException*/
    {
    	ArrayList<String> updatedList = new ArrayList<String>();
        String update = "UPDATE RestaurantItem " +
                "SET Status = 'DS' WHERE ExpirationDate < 'now' " + 
                "AND RestaurantID = '" + RestaurantID + "';";
        String testQuery = "SELECT * FROM RestaurantItem WHERE RestaurantID = " + RestaurantID + ";";
        Connection conn = null;
        // connect with DB
        try
        {
            conn = DriverManager.getConnection("jdbc:postgresql://" + 
        DB_Address + ":5432/project", USER, PASS);
            try (Statement stmt = conn.createStatement();){
                conn.setAutoCommit(false);
                System.out.println("first test query");
    			ResultSet rs = stmt.executeQuery(testQuery);
    			while(rs.next()) {
    				int restaurantId = rs.getInt("RestaurantID");
                    String barcode = rs.getString("Barcode");
                    String catalogID = rs.getString("CatalogID");
                    String status = rs.getString("Status");
                    String expirationDate = rs.getString("ExpirationDate");
                    System.out.println( "RestaurantID = " + restaurantId );
                    System.out.println( "Barcode = " + barcode );
                    System.out.println( "Catalog = " + catalogID );
                    System.out.println( "Status = " + status );
                    System.out.println( "Expiration Date = " + expirationDate );
                    System.out.println();
    			}
    			System.out.println("update");
                //stmt.executeUpdate(update);
                stmt.executeUpdate(update);
                System.out.println("second test query");
    			rs = stmt.executeQuery(testQuery);
    			while(rs.next()) {
    				int restaurantId = rs.getInt("RestaurantID");
                    String barcode = rs.getString("Barcode");
                    String catalogID = rs.getString("CatalogID");
                    String status = rs.getString("Status");
                    String expirationDate = rs.getString("ExpirationDate");
                    System.out.println( "RestaurantID = " + restaurantId );
                    System.out.println( "Barcode = " + barcode );
                    System.out.println( "Catalog = " + catalogID );
                    System.out.println( "Status = " + status );
                    System.out.println( "Expiration Date = " + expirationDate );
                    System.out.println();
                    updatedList.add(barcode);
    			}
                //commit change
                //conn.commit();
    			conn.rollback();
                conn.close();
                return updatedList;
            }
            catch (SQLException e) {
                //roll back transaction if error
            	e.printStackTrace();
                conn.rollback();
                conn.close();
                return updatedList;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
		return updatedList;
    }
}
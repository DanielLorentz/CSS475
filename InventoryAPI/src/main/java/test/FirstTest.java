package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirstTest {
    static final String DB_Address = "73.59.46.239";
    static final String USER = "inventory";
    static final String PASS = "12345678";
    
    public static void main (String[] args) throws ClassNotFoundException {
        // open connection
        Class.forName("org.postgresql.Driver");
        try(
                Connection conn = DriverManager.getConnection("jdbc:postgresql://" + DB_Address + ":5432/project", USER, PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM warehouseLocation"); ){
            //extract data
            while (rs.next()) {
                System.out.print("ID = " + rs.getString("id"));
                System.out.print(", Name = " + rs.getString("name"));
                System.out.print(", Description = " + rs.getString("description"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        
        }
    }
}

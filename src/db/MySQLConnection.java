package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
     private final static boolean IS_LOCALHOST = true;
    private final static String URL = IS_LOCALHOST
            ? "jdbc:mysql://localhost:3306/splitwise"
            : "jdbc:mysql://localhost:3305/sqldatabase";
    private final static String USER = IS_LOCALHOST ? "root" : "user";
    private final static String PASSWORD = "admin12";
    public static Connection connection = null;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL!");
            return connection;
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
        return null;
    }
    
        public static void buildConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL!");
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }
}

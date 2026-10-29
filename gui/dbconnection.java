package gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/its_ticketingsystem"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; // Leave blank if using default XAMPP setup

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return conn;
    }
}


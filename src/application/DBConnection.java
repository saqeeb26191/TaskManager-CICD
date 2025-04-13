package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/task_management";
    private static final String DB_USER = "root"; // replace with your MySQL username
    private static final String DB_PASSWORD = "syed"; // replace with your MySQL password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}






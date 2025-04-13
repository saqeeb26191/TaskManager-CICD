package application;
//udayhotagond00@gmail.com
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        new LoginFrame(); // Start with the login screen
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Connected to SQLite!");
            } else {
                System.out.println("Failed to connect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


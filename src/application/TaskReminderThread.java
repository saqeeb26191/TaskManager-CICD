package application;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class TaskReminderThread extends Thread {
    private int userId;
    private String userEmail;
    private volatile boolean running = true; // for graceful shutdown

    public TaskReminderThread(int userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }

    // Call this method to stop the thread safely
    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        ZoneId zone = ZoneId.systemDefault(); // or use ZoneId.of("Asia/Kolkata") for consistency
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        while (running) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT title, due_date FROM tasks WHERE user_id = ? AND status = 'pending'";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                LocalDateTime now = LocalDateTime.now(zone);
                LocalDateTime in3Hours = now.plusHours(3);
                LocalDateTime in24Hours = now.plusHours(24);
                LocalDateTime in25Hours = now.plusHours(25);

                while (rs.next()) {
                    String title = rs.getString("title");
                    Timestamp dueTimestamp = rs.getTimestamp("due_date");

                    if (dueTimestamp == null) continue;

                    LocalDateTime dueDate = dueTimestamp.toLocalDateTime();

                    boolean isDueSoon = dueDate.isAfter(now) && dueDate.isBefore(in3Hours);
                    boolean isDueIn24Hours = dueDate.isAfter(now) && dueDate.isBefore(in24Hours);

                    System.out.println("Today:"+isDueSoon);
                    System.out.println(isDueIn24Hours);
                    if (isDueSoon || isDueIn24Hours) {
                        System.out.println("Reminder sent for task: " + title);
                        String formattedDate = dueDate.format(formatter);
                        String subject = "📌 Task Reminder: " + title;
                        String body = "Hi,\n\nYour task \"" + title + "\" is due on " + formattedDate +
                                ".\nPlease make sure to complete it on time!\n\n- Student Task Manager";

                        EmailNotifier.sendReminder(userEmail, subject, body);
                        System.out.println("Reminder sent for task: " + title);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(60 * 60 * 1000); // Sleep for 1 hour
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // restore interrupt flag
                break;
            }
        }

        System.out.println("TaskReminderThread stopped.");
    }
}
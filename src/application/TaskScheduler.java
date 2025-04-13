package application;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskScheduler {

    public static void scheduleTasks(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, due_date, priority FROM tasks WHERE user_id = ? ORDER BY priority DESC, due_date ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp dueDate = rs.getTimestamp("due_date");
                String priority = rs.getString("priority");
                tasks.add(new Task(id, dueDate, priority));
            }

            // Simulate scheduling in time blocks (1 hour each)
            Calendar calendar = Calendar.getInstance();
            for (Task task : tasks) {
                Timestamp scheduledTime = new Timestamp(calendar.getTimeInMillis());
                String update = "UPDATE tasks SET scheduled_time = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(update);
                updateStmt.setTimestamp(1, scheduledTime);
                updateStmt.setInt(2, task.id);
                updateStmt.executeUpdate();

                // Move forward 1 hour for the next task
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Task {
        int id;
        Timestamp dueDate;
        String priority;

        Task(int id, Timestamp dueDate, String priority) {
            this.id = id;
            this.dueDate = dueDate;
            this.priority = priority;
        }
    }
}

package application;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarPanel extends JPanel {
    private int userId;
    private JPanel tasksListPanel;

    public CalendarPanel(int userId) {
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 255)); // Lavender background

        JLabel heading = new JLabel("📅 Task Calendar");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(heading, BorderLayout.NORTH);

        tasksListPanel = new JPanel();
        tasksListPanel.setLayout(new BoxLayout(tasksListPanel, BoxLayout.Y_AXIS));
        tasksListPanel.setBackground(new Color(250, 250, 255));

        JScrollPane scrollPane = new JScrollPane(tasksListPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadTasks();
    }

    private void loadTasks() {
        tasksListPanel.removeAll();
        Map<String, java.util.List<String>> dateTasksMap = new TreeMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT title, due_date FROM tasks WHERE user_id = ? ORDER BY due_date";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            while (rs.next()) {
                String title = rs.getString("title");
                Timestamp dueDate = rs.getTimestamp("due_date");

                String dateKey = dateFormat.format(dueDate);
                String time = timeFormat.format(dueDate);

                String taskInfo = "🕒 " + time + " - " + title;

                dateTasksMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(taskInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display grouped by date
        for (Map.Entry<String, java.util.List<String>> entry : dateTasksMap.entrySet()) {
            JLabel dateLabel = new JLabel("📆 " + entry.getKey());
            dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dateLabel.setForeground(new Color(50, 50, 120));
            dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
            tasksListPanel.add(dateLabel);

            for (String task : entry.getValue()) {
                JLabel taskLabel = new JLabel(task);
                taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                taskLabel.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 0));
                tasksListPanel.add(taskLabel);
            }

            tasksListPanel.add(Box.createVerticalStrut(10));
        }

        revalidate();
        repaint();
    }
}


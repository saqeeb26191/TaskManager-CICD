package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardFrame extends JFrame {
    private JPanel contentPanel;
    private int userId;
    private String email;

    public DashboardFrame(int userId, String email) {
        this.userId = userId;
        this.email = email;

        setTitle("Student Productivity Task Manager - Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Student Productivity Task Manager", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(63, 81, 181));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Sidebar with Navigation
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(3, 1, 10, 10));
        sidebar.setBackground(new Color(48, 63, 159));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton tasksButton = createNavButton("Tasks");
        JButton calendarButton = createNavButton("Calendar");
        JButton profileButton = createNavButton("Profile");

        sidebar.add(tasksButton);
        sidebar.add(calendarButton);
        sidebar.add(profileButton);

        add(sidebar, BorderLayout.WEST);

        // Main content area
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // Default view
        showTasksPanel();

        // Event listeners
        tasksButton.addActionListener(e -> showTasksPanel());
        calendarButton.addActionListener(e -> showCalendarPanel());
        profileButton.addActionListener(e -> showProfilePanel());

        setVisible(true);


    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showTasksPanel() {
        contentPanel.removeAll();
        contentPanel.add(new TasksPanel(userId), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCalendarPanel() {
        contentPanel.removeAll();
        contentPanel.add(new CalendarPanel(userId), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfilePanel() {
        contentPanel.removeAll();
        contentPanel.add(new ProfilePanel(userId, email), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}


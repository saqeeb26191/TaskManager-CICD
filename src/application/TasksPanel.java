package application;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class TasksPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityBox;
    private JDateChooser dueDateChooser;
    private JButton addButton, deleteButton, editButton;

    private int currentUserId;

    public TasksPanel(int userId) {
        this.currentUserId = userId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 255, 250)); // Mint Cream

        JLabel title = new JLabel("Your Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Description", "Due Date", "Priority", "Status"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form for adding tasks
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setBackground(new Color(255, 250, 240)); // Floral White

        titleField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        priorityBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        dueDateChooser = new JDateChooser();
        dueDateChooser.setDateFormatString("yyyy-MM-dd HH:mm:ss");

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        formPanel.add(new JLabel("Due Date:"));
        formPanel.add(dueDateChooser);
        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityBox);

        addButton = new JButton("Add Task");
        deleteButton = new JButton("Delete Selected");
        editButton = new JButton("Edit Selected");

        formPanel.add(addButton);
        formPanel.add(deleteButton);
        formPanel.add(editButton);

        add(formPanel, BorderLayout.SOUTH);

        // Action listeners
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());
        editButton.addActionListener(e -> editSelectedTask());

        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM tasks WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("description"));
                row.add(rs.getString("due_date"));
                row.add(rs.getString("priority"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addTask() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String priority = (String) priorityBox.getSelectedItem();
        java.util.Date dueDate = dueDateChooser.getDate();

        if (title.isEmpty() || dueDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dueDateStr = sdf.format(dueDate);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO tasks (user_id, title, description, due_date, priority) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, dueDateStr);
            stmt.setString(5, priority);
            stmt.executeUpdate();

            TaskScheduler.scheduleTasks(currentUserId);

            JOptionPane.showMessageDialog(this, "Task added and scheduled!");
            loadTasksFromDatabase();
            titleField.setText("");
            descriptionArea.setText("");
            dueDateChooser.setDate(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSelectedTask() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
            return;
        }

        int taskId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, taskId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Task deleted.");
            loadTasksFromDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void editSelectedTask() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.");
            return;
        }

        int taskId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String currentTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 2);
        String currentDueDate = (String) tableModel.getValueAt(selectedRow, 3);
        String currentPriority = (String) tableModel.getValueAt(selectedRow, 4);

        JTextField titleField = new JTextField(currentTitle);
        JTextArea descriptionArea = new JTextArea(currentDescription);
        JDateChooser dateChooser = new JDateChooser();
        JComboBox<String> priorityBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        priorityBox.setSelectedItem(currentPriority);

        // Time Spinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm:ss"));

        try {
            java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDueDate);
            dateChooser.setDate(parsedDate);
            timeSpinner.setValue(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        panel.add(new JLabel("Due Date:"));
        panel.add(dateChooser);
        panel.add(new JLabel("Time (HH:mm:ss):"));
        panel.add(timeSpinner);
        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String updateQuery = "UPDATE tasks SET title=?, description=?, due_date=?, priority=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(updateQuery);

                java.util.Date selectedDate = dateChooser.getDate();
                java.util.Date selectedTime = (java.util.Date) timeSpinner.getValue();

                SimpleDateFormat datePart = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timePart = new SimpleDateFormat("HH:mm:ss");
                String dueDateTimeStr = datePart.format(selectedDate) + " " + timePart.format(selectedTime);

                stmt.setString(1, titleField.getText().trim());
                stmt.setString(2, descriptionArea.getText().trim());
                stmt.setString(3, dueDateTimeStr);
                stmt.setString(4, priorityBox.getSelectedItem().toString());
                stmt.setInt(5, taskId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Task updated successfully!");
                loadTasksFromDatabase();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update task.");
            }
        }
    }
}



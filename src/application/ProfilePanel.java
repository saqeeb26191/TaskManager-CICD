package application;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {
    private int userId;
    private String email;
    private JLabel emailLabel;
    private JButton resetPasswordButton;
    private JButton logoutButton;

    public ProfilePanel(int userId, String email) {
        this.userId = userId;
        this.email = email;

        setLayout(new BorderLayout());
        setBackground(new Color(255, 250, 240)); // Light golden background

        JLabel heading = new JLabel("👤 User Profile");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(heading, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        emailLabel = new JLabel("Email: " + email);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(emailLabel);
        centerPanel.add(Box.createVerticalStrut(30));

        resetPasswordButton = new JButton("🔑 Reset Password");
        resetPasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetPasswordButton.setBackground(new Color(255, 165, 0));
        resetPasswordButton.setForeground(Color.WHITE);
        resetPasswordButton.setFocusPainted(false);
        resetPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(resetPasswordButton);
        centerPanel.add(Box.createVerticalStrut(15));

        logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoutButton);

        add(centerPanel, BorderLayout.CENTER);

        // Actions
        resetPasswordButton.addActionListener(e -> resetPassword());
        logoutButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new LoginFrame();
        });
    }

    private void resetPassword() {
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:");
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try (Connection conn = DBConnection.getConnection()) {
                String update = "UPDATE users SET password = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(update);
                stmt.setString(1, newPassword);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Password updated successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating password.");
            }
        }
    }
}


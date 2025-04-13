package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton forgotPasswordButton;

    public LoginFrame() {
        setTitle("Student Task Manager - Login");
        setSize(450, 350);
        setMinimumSize(new Dimension(400, 300));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 240, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Welcome to Student Task Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 30, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(18);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(70, 130, 180));
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        signupButton = new JButton("Don't have an account? Sign up");
        styleLinkButton(signupButton);
        panel.add(signupButton, gbc);

        gbc.gridy = 5;
        forgotPasswordButton = new JButton("Forgot password?");
        styleLinkButton(forgotPasswordButton);
        panel.add(forgotPasswordButton, gbc);

        add(panel);
        setVisible(true);

        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> {
            dispose();
            new SignupFrame();
        });

        forgotPasswordButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Password reset coming soon!");
        });
    }

    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void styleLinkButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(0, 102, 204));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String hashedPassword = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Start background task reminder thread
                new TaskReminderThread(userId, email).start();

                dispose();
                new DashboardFrame(userId, email);
            }
            else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or user does not exist. Please sign up.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }
}




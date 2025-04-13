package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.*;

public class SignupFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private JButton loginButton;

    public SignupFrame() {
        setTitle("Student Task Manager - Sign Up");
        setSize(450, 350);
        setMinimumSize(new Dimension(400, 300));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Create Your Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 30, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(18);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        signupButton = new JButton("Sign Up");
        styleButton(signupButton, new Color(46, 139, 87)); // SeaGreen
        panel.add(signupButton, gbc);

        gbc.gridy = 4;
        loginButton = new JButton("Already have an account? Login");
        styleLinkButton(loginButton);
        panel.add(loginButton, gbc);

        add(panel);
        setVisible(true);

        signupButton.addActionListener(e -> signup());
        loginButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
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

    private void signup() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String hashedPassword = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email already exists. Please use another.");
            } else {
                String insertSql = "INSERT INTO users (email, password) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, email);
                insertStmt.setString(2, hashedPassword);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Signup successful! You can now login.");
                dispose();
                new LoginFrame();
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
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }
}
package ui;

import Models.User;
import auth.Auth;
import utils.Session;
import utils.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signUpButton;

    public LoginFrame() {
        super("Login");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main content panel with padding
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(titleLabel, gbc);

        // Username label + field
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        content.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        content.add(usernameField, gbc);

        // Password label + field
        gbc.gridy++;
        gbc.gridx = 0;
        content.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        content.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(140, 40));
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        content.add(loginButton, gbc);

        // Sign Up button
        signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(140, 40));
        gbc.gridy++;
        content.add(signUpButton, gbc);

        setContentPane(content);

        // Action listeners (logic unchanged)
        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please fill in all fields.", "Missing Data", JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            User user = Auth.login(username, password);
            if (user != null) {
                Session.currentUsername = user.getUsername();
                Session.currentUserFullName = user.getFullName();
                SessionManager.saveSession(user.getUsername());
                JOptionPane.showMessageDialog(this, "Login successful!",
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new ExpenseTracker().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        signUpButton.addActionListener((ActionEvent e) -> {
            dispose();
            new SignUpFrame().setVisible(true);
        });
    }
}

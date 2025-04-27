package ui;

import auth.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignUpFrame extends JFrame {
    private final JTextField usernameField;
    private final JTextField fullNameField;
    private final JPasswordField passwordField;
    private final JButton signUpButton;
    private final JButton loginButton;

    public SignUpFrame() {
        super("Sign Up");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container with padding
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(titleLabel, gbc);

        // Username
        gbc.gridy++;
        gbc.gridwidth = 1;
        content.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        content.add(usernameField, gbc);

        // Full name
        gbc.gridy++;
        gbc.gridx = 0;
        content.add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        content.add(fullNameField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        content.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        content.add(passwordField, gbc);

        // Sign Up button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(200, 40));
        content.add(signUpButton, gbc);

        // Login button (navigate to login page)
        gbc.gridy++;
        loginButton = new JButton("Already have an account? Login");
        loginButton.setPreferredSize(new Dimension(200, 40));
        content.add(loginButton, gbc);

        // Hook up actions
        signUpButton.addActionListener(this::onSignUp);
        loginButton.addActionListener(this::onGoToLogin);

        setContentPane(content);
    }

    private void onSignUp(ActionEvent e) {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (Auth.signUp(username, fullName, password)) {
            JOptionPane.showMessageDialog(
                this,
                "Sign-up successful!",
                "Welcome",
                JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            new LoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Sign-up failed. Username may already exist.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onGoToLogin(ActionEvent e) {
        dispose();
        new LoginFrame().setVisible(true);
    }
}

package ui;
import auth.Auth;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpFrame extends JFrame {
    private JTextField usernameField, fullNameField;
    private JPasswordField passwordField;
    private JButton signUpButton;

    public SignUpFrame() {
        setTitle("Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // UI Components
        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(20);
        JLabel fullNameLabel = new JLabel("Full Name: ");
        fullNameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(20);
        signUpButton = new JButton("Sign Up");

        // Add components
        add(usernameLabel);
        add(usernameField);
        add(fullNameLabel);
        add(fullNameField);
        add(passwordLabel);
        add(passwordField);
        add(signUpButton);
        // Sign-up Button Action
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String fullName = fullNameField.getText();
                String password = new String(passwordField.getPassword());
                // usernameField.setText()

                if (Auth.signUp(username, fullName, password)) {
                    JOptionPane.showMessageDialog(null, "Sign-up successful!");
                    System.out.println("save");
                    dispose();
                    new LoginFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Sign-up failed. Username may already exist.");
                }
            }
        });
    }
}

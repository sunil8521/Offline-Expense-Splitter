package ui;

import javax.swing.*;
import java.awt.*;
import ui.LoginFrame;
import utils.Session;

public class HomePage extends JFrame {
    public HomePage() {
        setTitle(" Home - " + Session.currentUsername);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + Session.currentUsername + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        add(logoutButton, BorderLayout.SOUTH);

        logoutButton.addActionListener(e -> {
            Session.currentUsername = null;
            dispose();
            new LoginFrame().setVisible(true);  // go back to login
        });
    }
}

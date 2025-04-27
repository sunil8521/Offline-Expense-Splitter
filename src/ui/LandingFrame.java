package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LandingFrame extends JFrame {
    private final JButton getStartedButton;

    public LandingFrame() {
        super("SpendWise");  // App name displayed on window title
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container with padding
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(60, 80, 60, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.CENTER;

        // App title
        JLabel titleLabel = new JLabel("SpendWise", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(30, 144, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        content.add(titleLabel, gbc);

        // Get Started button
        getStartedButton = new JButton("Get Started");
        getStartedButton.setFont(new Font("Sans-serif", Font.PLAIN, 18));
        getStartedButton.setPreferredSize(new Dimension(200, 50));
        getStartedButton.addActionListener(this::onGetStarted);
        gbc.gridy = 1;
        content.add(getStartedButton, gbc);

        setContentPane(content);
    }

    private void onGetStarted(ActionEvent e) {
        // Navigate to login screen
        dispose();
        new LoginFrame().setVisible(true);
    }
}

package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardUI extends JFrame {

    public DashboardUI() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        sidebar.setBackground(Color.WHITE);

        String[] sidebarItems = {"Home", "Borrow", "Debt", "Profile", "Logout"};
        for (String item : sidebarItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(160, 40));
            sidebar.add(Box.createVerticalStrut(20));
            sidebar.add(button);
        }

        add(sidebar, BorderLayout.WEST);

        // Main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(title, BorderLayout.NORTH);

        // Center panel (contains summary + table)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        summaryPanel.add(createStatCard("Total Borrowed", "$170.00"));
        summaryPanel.add(createStatCard("Total Debt", "$105.00"));
        centerPanel.add(summaryPanel);

        // Friends summary table
        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableTitle = new JLabel("Friends Summary");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        String[] columns = {"Friend", "Borrowed", "Debt"};
        Object[][] data = {
                {"Alex Johnson", "$50.00", "$0.00"},
                {"Sam Wilson", "$0.00", "$75.00"},
                {"Taylor Swift", "$120.00", "$30.00"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(tablePanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String amount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 100));
        card.add(new JLabel(title));
        JLabel value = new JLabel(amount);
        value.setFont(new Font("SansSerif", Font.BOLD, 20));
        card.add(value);
        return card;
    }
}

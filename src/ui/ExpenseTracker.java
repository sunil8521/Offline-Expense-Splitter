package ui;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Models.User;
import controller.TransactionController;
import controller.TransactionController.RepaymentRequest;
import db.MySQLConnection;
import utils.Session;
import utils.SessionManager;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.sql.Timestamp;






public class ExpenseTracker extends JFrame {
    // Main panels using CardLayout
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Sidebar buttons
    private JButton homeButton;
    private JButton borrowButton;
    private JButton debtButton;
    private JButton profileButton;
    private JButton logoutButton;

    
    // Currency formatter
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));

    public ExpenseTracker() {
        // Set up the frame
        setTitle("SpendWise");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
       
        
        // Set up the main layout
        setLayout(new BorderLayout());
        
        // Create sidebar
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Create main content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create panels for each section
        contentPanel.add(createHomePanel(), "home");
        contentPanel.add(createBorrowPanel(), "borrow");
        contentPanel.add(createLendPanel(), "lend");
        contentPanel.add(createProfilePanel(), "profile");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show home panel by default
        cardLayout.show(contentPanel, "home");
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        
        // App title
        JLabel titleLabel = new JLabel("SpendWise");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        // Create sidebar buttons with icons
        homeButton = createSidebarButton("Home", "home");
        borrowButton = createSidebarButton("Borrow", "borrow");
        debtButton = createSidebarButton("Lend", "lend");
        profileButton = createSidebarButton("Profile", "profile");
        logoutButton  = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(160, 40));
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(240, 240, 240));
        logoutButton.setBorderPainted(false);
        logoutButton.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        logoutButton.addActionListener(e -> {
            Session.currentUsername = null;
            SessionManager.clearSession();

            this.dispose();
            // 3) Show the login screen
            new ui.LoginFrame().setVisible(true);
        });        
        // Add components to sidebar
        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(homeButton);
        sidebar.add(borrowButton);
        sidebar.add(debtButton);
        sidebar.add(profileButton);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return sidebar;
    }
    
    private JButton createSidebarButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 40));
        button.setFocusPainted(false);
        
        // Style the button to look more modern
        button.setBackground(new Color(240, 240, 240));
        button.setBorderPainted(false);
        button.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        
        if (cardName != null) {
            button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        } else if (text.equals("Logout")) {
            button.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                    ExpenseTracker.this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            });
        }
        
        return button;
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    
        // 1) Build header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome, " + Session.currentUsername + "!");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton showFriendButton   = new JButton("Friend Requests");
        JButton addFriendButton = new JButton("Add Friend");
        // JButton addBorrowButton = new JButton("Borrow");
        JButton addDebtButton   = new JButton("Lend");
        addFriendButton.addActionListener(e -> showAddFriendDialog());
        // addBorrowButton.addActionListener(e -> showAddTransactionDialog(true));
        addDebtButton.addActionListener(e -> showAddTransactionDialog(false));
        showFriendButton.addActionListener(e -> showFriendRequestsDialog());
        buttonPanel.add(showFriendButton);
        buttonPanel.add(addFriendButton);
        // buttonPanel.add(addBorrowButton);
        buttonPanel.add(addDebtButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
    
        // 2) Build summary
        double totalBorrow = TransactionController.getTotalBorrowed();
        double totalLend   = TransactionController.getTotalLent();
    
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        summaryPanel.add(createSummaryCard("Total Borrowed", totalBorrow));
        summaryPanel.add(createSummaryCard("Total lent",     totalLend));
    
        JPanel summaryContainer = new JPanel(new BorderLayout());
        summaryContainer.setBorder(new EmptyBorder(10, 0, 10, 0));
        summaryContainer.add(summaryPanel, BorderLayout.CENTER);
    
        // 3) Combine header + summary into one north panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(headerPanel);
        northPanel.add(summaryContainer);
    
        panel.add(northPanel, BorderLayout.NORTH);

//midlle part
        JPanel pendingContainer = new JPanel(new BorderLayout());
        pendingContainer.setBorder(BorderFactory.createTitledBorder("Clear Requests"));
        pendingContainer.setPreferredSize(new Dimension(0, 200)); 
    
        JPanel pendingList = new JPanel();
        pendingList.setLayout(new BoxLayout(pendingList, BoxLayout.Y_AXIS));
        JScrollPane pendingScroll = new JScrollPane(pendingList);
        pendingScroll.setBorder(new EmptyBorder(5,5,5,5));
        pendingContainer.add(pendingScroll, BorderLayout.CENTER);
    
        panel.add(pendingContainer, BorderLayout.CENTER);
    
        // load them once:
        refreshPendingRequests(pendingList);
        Timer timer = new Timer(7000, e -> refreshPendingRequests(pendingList));
        timer.start();





//last part

        JPanel historyContainer = new JPanel(new BorderLayout());
        historyContainer.setBorder(BorderFactory.createTitledBorder("Request History"));
        historyContainer.setPreferredSize(new Dimension(0, 150));  // adjust height
        
        JPanel historyList = new JPanel();
        historyList.setLayout(new BoxLayout(historyList, BoxLayout.Y_AXIS));
        JScrollPane historyScroll = new JScrollPane(historyList);
        historyScroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        historyContainer.add(historyScroll, BorderLayout.CENTER);
        panel.add(historyContainer, BorderLayout.SOUTH);

        // refreshRequestHistory(historyList);
        // refreshPaymenttHistory(historyList);
        
        
        // Poll every 5 seconds
        refreshCombinedHistory(historyList);

        // schedule periodic refresh
        final JPanel historyListPanel = historyList;
        Timer timer2 = new Timer(10_000, e -> {
            refreshCombinedHistory(historyListPanel);
        });
        timer2.setRepeats(true);
        timer2.start();
        return panel;
    }
    

    private void refreshPendingRequests(JPanel pendingList) {
        System.out.println("pendingList");  
        pendingList.removeAll();
        String me = Session.currentUsername;
        List<TransactionController.RepaymentRequest> reqs =
            TransactionController.fetchPendingRepaymentRequests(me);
    
        if (reqs.isEmpty()) {
            JLabel none = new JLabel("No pending clear-requests.");
            none.setBorder(new EmptyBorder(5,5,5,5));
            pendingList.add(none);
        } else {
            for (TransactionController.RepaymentRequest r : reqs) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                // show borrower + amount + reason
                // row.add(new JLabel(r.borrowerUsername 
                //     + " owes " 
                //     + currencyFormat.format(r.amount)
                //     + " (“" + r.reason + "”)"
                // ));
                row.add(new JLabel(
    r.borrowerUsername + " wants to settle the amount of " +
    currencyFormat.format(r.amount) +
    " for \"" + r.reason + "\""
));


    
                JButton approve = new JButton("Approve");
                approve.addActionListener(e -> {
                    TransactionController.approveRepaymentRequest(
                      r.requestId, r.transactionId
                    );

                    refreshPendingRequests(pendingList);

                    refreshUI("home");

                });
                row.add(approve);
    
                JButton reject = new JButton("Reject");
                reject.addActionListener(e -> {
                    TransactionController.rejectRepaymentRequest(r.requestId);
                    refreshPendingRequests(pendingList);
                    refreshUI("home");
                });
                row.add(reject);
    
                pendingList.add(row);
            }
        }
    
        pendingList.revalidate();
        pendingList.repaint();
    }




    private void refreshCombinedHistory(JPanel historyList) {
        System.out.println("historyList");
        historyList.removeAll();
        String me = Session.currentUsername;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
        // --- Section 1: Your outgoing friend‐request history ---
        // historyList.add(new JLabel("Friend Request History:"));
        // historyList.add(Box.createRigidArea(new Dimension(0,5)));
        String sql = """
          SELECT receiver_username, status, action_at
            FROM friend_request_history
           WHERE sender_username = ?
             AND status <> 'pending'
           ORDER BY action_at DESC
        """;
        try (PreparedStatement ps = 
               MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, me);
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    String receiver = rs.getString("receiver_username");
                    String status   = rs.getString("status");
                    Timestamp ts    = rs.getTimestamp("action_at");
                    String line = String.format(
                        "➜ Your friend‐request to %s was %s on %s",
                        receiver,
                        status.equalsIgnoreCase("accepted") ? "approved" : "rejected",
                        ts.toLocalDateTime().format(fmt)
                      );
                    historyList.add(new JLabel(line));
                }
                if (!any) {
                    historyList.add(new JLabel(""));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            historyList.add(new JLabel("(error loading friend-request history)"));
        }
    
        historyList.add(Box.createRigidArea(new Dimension(0,10)));
    
        // --- Section 2: Your repayment‐request history ---
        // historyList.add(new JLabel("Repayment Request History:"));
        // historyList.add(Box.createRigidArea(new Dimension(0,5)));
        List<TransactionController.RepaymentHistory> payHist =
            TransactionController.fetchRequestHistory();
        if (payHist.isEmpty()) {
            historyList.add(new JLabel(""));
        } else {
            for (var h : payHist) {
                String when = h.respondedAt.toLocalDateTime().format(fmt);

                String line = String.format(
              "➜ You asked %s to clear dues—%s on %s",
              h.lenderUsername,
              h.status.equals("acknowledged") ? "approved" : "rejected",
              when
            );
                historyList.add(new JLabel(line));
            }
        }
    
        historyList.revalidate();
        historyList.repaint();
    }
    



    private JPanel createSummaryCard(String title, double amount) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel amountLabel = new JLabel(currencyFormat.format(amount));
        amountLabel.setFont(new Font("Sans-serif", Font.BOLD, 20));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.CENTER);
        
        return card;
    }
    

    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Borrowed Money");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Fetch real borrow transactions (where current user is borrower)
        List<Transaction> borrowList = fetchTransactions(true); 
        
        // Transactions list
        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        for (Transaction txn : borrowList) {
            JPanel card = createTransactionCard(txn);
            String borrower = Session.currentUsername;
            String lender   = txn.getFriendName();
            boolean pending = TransactionController
                                .hasPendingClearRequest(txn.getId(), borrower, lender);
        
            JButton clearBtn = new JButton(pending ? "Requested" : "Request Clear");
            clearBtn.setEnabled(!pending);

            clearBtn.addActionListener(evt -> {
             
                TransactionController.sendClearRequest(txn.getId(),Session.currentUsername,txn.getFriendName());
                // disable so you can’t spam it
                clearBtn.setEnabled(false);
                clearBtn.setText("Requested");
            });













            // wrap the button in its own panel so it lays out nicely
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(clearBtn);
            card.add(btnPanel, BorderLayout.SOUTH);

            transactionsPanel.add(card);
            transactionsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        JScrollPane scrollPane = new JScrollPane(transactionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
       
    private JPanel createLendPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Lend Money");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
    
        // Fetch real “lend” transactions (where current user is lender)
        List<Transaction> lendList = fetchTransactions(false);
    
        // Transactions list
        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        for (Transaction txn : lendList) {
            JPanel transactionCard = createTransactionCard(txn);
            transactionsPanel.add(transactionCard);
            transactionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    
        JScrollPane scrollPane = new JScrollPane(transactionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        return panel;
    }
    
    private List<Transaction> fetchTransactions(boolean isBorrow) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
          SELECT 
            id,lender_username, borrower_username, amount, reason, transaction_date
          FROM transactions
          WHERE """ + (isBorrow
            ? " borrower_username = ? "
            : " lender_username   = ? ") + 
          "ORDER BY transaction_date DESC";
        
        try (PreparedStatement ps = 
               MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, Session.currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // friendName is the other party
                    int txnId = rs.getInt("id");
                    String friendName = isBorrow
                        ? rs.getString("lender_username")
                        : rs.getString("borrower_username");
                    String reason = rs.getString("reason");
                    double amount = rs.getDouble("amount");

                    String date   = rs.getDate("transaction_date").toString();
                    
                    list.add(new Transaction(friendName, reason, amount, date, txnId));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
              "Error loading transactions: " + ex.getMessage(),
              "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }



private JPanel createProfilePanel() {
    String currentUser = Session.currentUsername;
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Title
    JLabel titleLabel = new JLabel("Change Password");
    titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 24));
    panel.add(titleLabel, BorderLayout.NORTH);

    // Form container
    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
        new EmptyBorder(20, 20, 20, 20)
    ));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Row 0: Current Password
    gbc.gridx = 0; gbc.gridy = 0;
    form.add(new JLabel("Current Password:"), gbc);
    JPasswordField currentPassField = new JPasswordField(20);
    gbc.gridx = 1;
    form.add(currentPassField, gbc);

    // Row 1: New Password
    gbc.gridy++; gbc.gridx = 0;
    form.add(new JLabel("New Password:"), gbc);
    JPasswordField newPassField = new JPasswordField(20);
    gbc.gridx = 1;
    form.add(newPassField, gbc);

    // Row 2: Save Button
    gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
    JButton saveButton = new JButton("Change Password");
    saveButton.setPreferredSize(new Dimension(160, 35));
    form.add(saveButton, gbc);

    panel.add(form, BorderLayout.CENTER);

    // Save logic
    saveButton.addActionListener(ev -> {
        String currentPass = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());

        if (currentPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Both fields are required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String verifySql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(verifySql)) {
            ps.setString(1, currentUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String actualPassword = rs.getString("password");
                    if (!actualPassword.equals(currentPass)) {
                        JOptionPane.showMessageDialog(this,
                            "Current password is incorrect.",
                            "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "User not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String updateSql = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(updateSql)) {
            ps.setString(1, newPass);
            ps.setString(2, currentUser);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Password updated successfully. Please log in again.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to update password: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    return panel;
}
   
    private JPanel createTransactionCard(Transaction transaction) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(transaction.getFriendName());
        nameLabel.setFont(new Font("Sans-serif", Font.BOLD, 14));
        
        JLabel amountLabel = new JLabel(currencyFormat.format(transaction.getAmount()));
        amountLabel.setFont(new Font("Sans-serif", Font.BOLD, 14));
        
        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(amountLabel, BorderLayout.EAST);
        
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        JLabel reasonLabel = new JLabel(transaction.getReason());
        reasonLabel.setForeground(new Color(100, 100, 100));
        
        JLabel dateLabel = new JLabel(transaction.getDate());
        dateLabel.setFont(new Font("Sans-serif", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(150, 150, 150));
        
        detailsPanel.add(reasonLabel);
        detailsPanel.add(dateLabel);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        
        return card;
    }


    

    private void showAddFriendDialog() {
        JDialog dialog = new JDialog(this, "Add Friend", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
    
        // --- Top: search field + button ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Username:"));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        dialog.add(searchPanel, BorderLayout.NORTH);
    
        // --- Center: results container ---
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(scroll, BorderLayout.CENTER);
    
        // --- Search action ---
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            resultsPanel.removeAll();
    
            if (query.isEmpty()) {
                resultsPanel.add(new JLabel("Please enter a username to search."));
            } else {
                List<String> matches = searchUsers(query);
                if (matches.isEmpty()) {
                    resultsPanel.add(new JLabel("No users found."));
                } else {
                    for (String user : matches) {
                        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                        JLabel userLabel = new JLabel(user);
                        JButton reqBtn = new JButton("Send Request");
    
                        // on-click: send request (implement this)
                        reqBtn.addActionListener(evt -> {
                            sendFriendRequest(user);
                            reqBtn.setText("Requested");
                            reqBtn.setEnabled(false);
                        });
    
                        row.add(userLabel);
                        row.add(reqBtn);
                        resultsPanel.add(row);
                    }
                }
            }
    
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });
    
        dialog.setVisible(true);
    }

    private List<String> searchUsers(String usernameQuery) {
        List<String> matches = new ArrayList<>();
        String sql =
          "SELECT u.username " +
          "FROM users u " +
          "WHERE u.username LIKE ? " +
          "  AND u.username <> ? " +
          "  AND NOT EXISTS ( " +
          "      SELECT 1 FROM friendships f " +
          "      WHERE (f.user1 = ? AND f.user2 = u.username) " +
          "         OR (f.user1 = u.username AND f.user2 = ?) " +
          "  )";
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, "%" + usernameQuery + "%");
            ps.setString(2, Session.currentUsername);
            ps.setString(3, Session.currentUsername);
            ps.setString(4, Session.currentUsername);
    
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    matches.add(rs.getString("username"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error searching users: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        return matches;
    }
    


private void sendFriendRequest(String toUsername) {
    String checkSql = """
        SELECT status FROM friend_requests
        WHERE sender_username = ? AND receiver_username = ?
    """;

    String insertSql = """
        INSERT INTO friend_requests (sender_username, receiver_username)
        VALUES (?, ?)
    """;

    String checkHistorySql = """
        SELECT status FROM friend_request_history
        WHERE sender_username = ? AND receiver_username = ?
    """;

    String updateHistorySql = """
        UPDATE friend_request_history
        SET status = 'pending', action_at = CURRENT_TIMESTAMP
        WHERE sender_username = ? AND receiver_username = ?
    """;

    String insertHistorySql = """
        INSERT INTO friend_request_history (sender_username, receiver_username, status)
        VALUES (?, ?, 'pending')
    """;

    try (
        PreparedStatement checkPs = MySQLConnection.connection.prepareStatement(checkSql);
        PreparedStatement insertPs = MySQLConnection.connection.prepareStatement(insertSql);
        PreparedStatement checkHistoryPs = MySQLConnection.connection.prepareStatement(checkHistorySql);
        PreparedStatement updateHistoryPs = MySQLConnection.connection.prepareStatement(updateHistorySql);
        PreparedStatement insertHistoryPs = MySQLConnection.connection.prepareStatement(insertHistorySql)
    ) {
        checkPs.setString(1, Session.currentUsername);
        checkPs.setString(2, toUsername);
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            String status = rs.getString("status");
            if ("pending".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this,
                    "Friend request is already pending.",
                    "Already Sent", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            // Insert into friend_requests table
            insertPs.setString(1, Session.currentUsername);
            insertPs.setString(2, toUsername);
            insertPs.executeUpdate();
        }

        // Handle friend_request_history
        checkHistoryPs.setString(1, Session.currentUsername);
        checkHistoryPs.setString(2, toUsername);
        ResultSet hrs = checkHistoryPs.executeQuery();

        if (hrs.next()) {
            String oldStatus = hrs.getString("status");
            if (!"pending".equalsIgnoreCase(oldStatus)) {
                updateHistoryPs.setString(1, Session.currentUsername);
                updateHistoryPs.setString(2, toUsername);
                updateHistoryPs.executeUpdate();
            }
        } else {
            insertHistoryPs.setString(1, Session.currentUsername);
            insertHistoryPs.setString(2, toUsername);
            insertHistoryPs.executeUpdate();
        }

        JOptionPane.showMessageDialog(this,
            "Friend request sent to " + toUsername,
            "Request Sent", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Failed to send request: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    
private void showFriendRequestsDialog() {
    JDialog dialog = new JDialog(this, "Friend Requests", true);
    dialog.setSize(400, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    // Container for the list of requests
    JPanel resultsPanel = new JPanel();
    resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
    JScrollPane scroll = new JScrollPane(resultsPanel);
    scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
    dialog.add(scroll, BorderLayout.CENTER);

    // Utility to load & display pending requests
    refreshFriendRequests(resultsPanel);

    // Poll every 5 seconds
    Timer timer = new Timer(5000, e ->     refreshFriendRequests(resultsPanel));
    
    timer.setInitialDelay(0);  // immediately load once
    timer.start();

    // Stop polling when dialog closes
    dialog.addWindowListener(new WindowAdapter() {
        @Override public void windowClosing(WindowEvent e) {
            timer.stop();
        }
    });

    dialog.setVisible(true);
}



private void refreshFriendRequests(JPanel resultsPanel) {

    resultsPanel.removeAll();
    List<String> pending = fetchPendingRequests();

    if (pending.isEmpty()) {
        resultsPanel.add(new JLabel("No pending requests."));
    } else {
        for (String sender : pending) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            row.add(new JLabel(sender));

            JButton approve = new JButton("Approve");
            approve.addActionListener(evt -> {
                approveFriendRequest(sender);
                refreshFriendRequests(resultsPanel);
            });
            row.add(approve);

            JButton reject = new JButton("Reject");
            reject.addActionListener(evt -> {
                rejectFriendRequest(sender);
                refreshFriendRequests(resultsPanel);
            });
            row.add(reject);

            resultsPanel.add(row);
        }
    }

    resultsPanel.revalidate();
    resultsPanel.repaint();
}

private List<String> fetchPendingRequests() {
    List<String> list = new ArrayList<>();
    String sql = """
        SELECT sender_username
          FROM friend_requests
         WHERE receiver_username = ?
           AND status = 'pending'
        """;
    try (PreparedStatement ps = 
           MySQLConnection.connection.prepareStatement(sql)) {
        ps.setString(1, Session.currentUsername);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("sender_username"));
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error loading requests: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return list;
}

// Approve: update request + insert into friendships
private void approveFriendRequest(String sender) {
    String deletePending = """
        DELETE FROM friend_requests
        WHERE sender_username = ? AND receiver_username = ?
    """;

    String updateHistory = """
        UPDATE friend_request_history
        SET status = 'accepted', action_at = CURRENT_TIMESTAMP
        WHERE sender_username = ? AND receiver_username = ?
          AND status = 'pending'
    """;

    String insertFriendship = """
        INSERT INTO friendships (user1, user2)
        VALUES (
          LEAST(?, ?),
          GREATEST(?, ?)
        )
        ON DUPLICATE KEY UPDATE since = since
    """;

    try {
        MySQLConnection.connection.setAutoCommit(false);

        // Step 1: Delete from friend_requests
        try (PreparedStatement ps1 =
                     MySQLConnection.connection.prepareStatement(deletePending)) {
            ps1.setString(1, sender);
            ps1.setString(2, Session.currentUsername);
            ps1.executeUpdate();
        }

        // Step 2: Update friend_request_history
        try (PreparedStatement ps2 =
                     MySQLConnection.connection.prepareStatement(updateHistory)) {
            ps2.setString(1, sender);
            ps2.setString(2, Session.currentUsername);
            ps2.executeUpdate();
        }

        // Step 3: Insert into friendships
        try (PreparedStatement ps3 =
                     MySQLConnection.connection.prepareStatement(insertFriendship)) {
            ps3.setString(1, sender);
            ps3.setString(2, Session.currentUsername);
            ps3.setString(3, sender);
            ps3.setString(4, Session.currentUsername);
            ps3.executeUpdate();
        }

        MySQLConnection.connection.commit();
        JOptionPane.showMessageDialog(this,
            "You are now friends with " + sender,
            "Request Approved", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException ex) {
        try {
            MySQLConnection.connection.rollback();
        } catch (Exception ignore) {}
        JOptionPane.showMessageDialog(this,
            "Failed to approve: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            MySQLConnection.connection.setAutoCommit(true);
        } catch (Exception ignore) {}
    }
}


// Reject: simply update the status
private void rejectFriendRequest(String sender) {
    String deleteSql = """
        DELETE FROM friend_requests
        WHERE sender_username = ? AND receiver_username = ?
    """;

    String updateHistorySql = """
        UPDATE friend_request_history
        SET status = 'rejected', action_at = CURRENT_TIMESTAMP
        WHERE sender_username = ? AND receiver_username = ?
          AND status = 'pending'
    """;

    try (
        PreparedStatement deletePs = MySQLConnection.connection.prepareStatement(deleteSql);
        PreparedStatement updateHistoryPs = MySQLConnection.connection.prepareStatement(updateHistorySql)
    ) {
        // Delete from friend_requests (pending only table)
        deletePs.setString(1, sender);
        deletePs.setString(2, Session.currentUsername);
        deletePs.executeUpdate();

        // Update history table with rejected status
        updateHistoryPs.setString(1, sender);
        updateHistoryPs.setString(2, Session.currentUsername);
        updateHistoryPs.executeUpdate();

        JOptionPane.showMessageDialog(this,
            "Request from " + sender + " rejected.",
            "Request Rejected", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Failed to reject: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void showAddTransactionDialog(boolean isBorrow) {
    JDialog dialog = new JDialog(this, isBorrow ? "Borrowed Money" : "Lend Money", true);
    dialog.setSize(400, 350);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    // --- Form panel ---
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5,5,5,5);

    // 1) Friend dropdown (now from DB)
    gbc.gridx = 0; gbc.gridy = 0;
    formPanel.add(new JLabel("Friend"), gbc);

    gbc.gridy = 1; gbc.weightx = 1.0;
    JComboBox<String> friendComboBox = new JComboBox<>();
    List<String> friendNames = getFriendsList();
    if (friendNames.isEmpty()) {
        friendComboBox.addItem("<no friends>");
        friendComboBox.setEnabled(false);
    } else {
        for (String f : friendNames) {
            friendComboBox.addItem(f);
        }
    }
    formPanel.add(friendComboBox, gbc);

    // 2) Amount
    gbc.gridy = 2; gbc.weightx = 0;
    formPanel.add(new JLabel("Amount"), gbc);
    gbc.gridy = 3; gbc.weightx = 1.0;
    JTextField amountField = new JTextField(20);
    formPanel.add(amountField, gbc);

    // 3) Reason
    gbc.gridy = 4; gbc.weightx = 0;
    formPanel.add(new JLabel("Reason"), gbc);
    gbc.gridy = 5; gbc.weightx = 1.0;
    JTextField reasonField = new JTextField(20);
    formPanel.add(reasonField, gbc);

    // 4) Date
    gbc.gridy = 6; gbc.weightx = 0;
    formPanel.add(new JLabel("Date"), gbc);
    gbc.gridy = 7; gbc.weightx = 1.0;
    JTextField dateField = new JTextField(
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    );
    formPanel.add(dateField, gbc);

    dialog.add(formPanel, BorderLayout.CENTER);

    // --- Buttons ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton saveButton   = new JButton("Save");
    cancelButton.addActionListener(e -> dialog.dispose());
    saveButton.addActionListener(e -> {
        if (friendNames.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                "You have no friends to transact with.",
                "No Friends", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String friendName = (String) friendComboBox.getSelectedItem();
            double amount     = Double.parseDouble(amountField.getText().trim());
            String reason     = reasonField.getText().trim();
            String date       = dateField.getText().trim();

            if (amount > 0 && !reason.isEmpty() && !date.isEmpty()) {
                // Persist to DB
                saveTransaction(isBorrow, friendName, amount, reason, date);
                // Refresh UI
                refreshUI("home");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill all fields correctly",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog,
                "Please enter a valid amount",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
}

private List<String> getFriendsList() {
    List<String> list = new ArrayList<>();
    String sql =
      "SELECT CASE WHEN user1 = ? THEN user2 ELSE user1 END AS friend " +
      "FROM friendships " +
      "WHERE user1 = ? OR user2 = ?";
    try (PreparedStatement ps = 
           MySQLConnection.connection.prepareStatement(sql)) {
        ps.setString(1, Session.currentUsername);
        ps.setString(2, Session.currentUsername);
        ps.setString(3, Session.currentUsername);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("friend"));
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error fetching friends list: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return list;
}

private void saveTransaction(boolean isBorrow,
                             String friendUsername,
                             double amount,
                             String reason,
                             String date) {
    String sql =
      "INSERT INTO transactions " +
      "(lender_username, borrower_username, amount, reason, transaction_date) " +
      "VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement ps = 
           MySQLConnection.connection.prepareStatement(sql)) {
        String you = Session.currentUsername;
        if (isBorrow) {
            // you borrowed => friend lent to you
            ps.setString(1, friendUsername); // lender
            ps.setString(2, you);            // borrower
        } else {
            // you lend => you lender, friend borrower
            ps.setString(1, you);
            ps.setString(2, friendUsername);
        }
        ps.setDouble(3, amount);
        ps.setString(4, reason);
        ps.setDate(5, Date.valueOf(date));
        ps.executeUpdate();
        JOptionPane.showMessageDialog(this,
            "Transaction saved.",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Failed to save transaction: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void refreshUI(String panelToShow) {
        // 1) Wipe out everything`
        contentPanel.removeAll();
    
        // 2) Re-add each card
        contentPanel.add(createHomePanel(),   "home");
        contentPanel.add(createBorrowPanel(), "borrow");
        contentPanel.add(createLendPanel(),   "lend");
        contentPanel.add(createProfilePanel(),"profile");
    
        // 3) Tell Swing to re-layout and repaint
        contentPanel.revalidate();
        contentPanel.repaint();
    
        // 4) Show whichever panel the caller requested
        cardLayout.show(contentPanel, panelToShow);
    }
    


    static class Transaction {
        private int id;
        private String friendName;
        private String reason;
        private double amount;
        private String date;
        
        public Transaction( String friendName, String reason, double amount, String date,int id) {
            this.id = id;
            this.friendName = friendName;
            this.reason = reason;
            this.amount = amount;
            this.date = date;
        }
        
        public int getId() {
            return id;
        }
        public String getFriendName() {
            return friendName;
        }
        
        public String getReason() {
            return reason;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public String getDate() {
            return date;
        }

    }


}

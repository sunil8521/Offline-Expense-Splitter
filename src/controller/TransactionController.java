package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import javax.swing.JOptionPane;

import db.MySQLConnection;
import utils.Session;
import utils.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionController {
    public static double getTotalBorrowed() {
        String sql = """
                    SELECT IFNULL(SUM(amount),0) AS total
                      FROM transactions
                     WHERE borrower_username = ?
                """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, Session.currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("total");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading total borrowed:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    public static double getTotalLent() {
        String sql = """
                    SELECT IFNULL(SUM(amount),0) AS total
                      FROM transactions
                     WHERE lender_username = ?
                """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, Session.currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("total");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading total lent:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    public static boolean hasPendingClearRequest(int transactionId,
            String borrowerUsername,
            String lenderUsername) {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM repayment_requests
                WHERE transaction_id    = ?
                AND borrower_username = ?
                AND lender_username   = ?
                AND status            = 'pending'
                """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.setString(2, borrowerUsername);
            ps.setString(3, lenderUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void sendClearRequest(int transactionId,
            String borrowerUsername,
            String lenderUsername) {
        String sql = """
                  INSERT INTO repayment_requests
                    (transaction_id, borrower_username, lender_username)
                  VALUES (?,?,?)
                """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.setString(2, borrowerUsername);
            ps.setString(3, lenderUsername);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(
                    null,
                    "Clear request sent!",
                    "Request Sent",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to send clear request:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



//save the transaction details to the database







    //to get pending acknowledgment for a user
    public static class RepaymentRequest {
        public final int    requestId;
        public final int    transactionId;
        public final String borrowerUsername;
        public final double amount;
        public final String reason;
        public final Date   transactionDate;

        public RepaymentRequest(int requestId,
                                int transactionId,
                                String borrowerUsername,
                                double amount,
                                String reason,
                                Date transactionDate) {
            this.requestId       = requestId;
            this.transactionId   = transactionId;
            this.borrowerUsername= borrowerUsername;
            this.amount          = amount;
            this.reason          = reason;
            this.transactionDate = transactionDate;
        }
    }


    public static List<RepaymentRequest> fetchPendingRepaymentRequests(String lenderUsername) {
        List<RepaymentRequest> list = new ArrayList<>();
        String sql = """
          SELECT r.id AS req_id,
                 r.transaction_id,
                 r.borrower_username,
                 t.amount,
                 t.reason,
                 t.transaction_date
            FROM repayment_requests r
            JOIN transactions t
              ON t.id = r.transaction_id
           WHERE r.lender_username = ?
             AND r.status         = 'pending'
           ORDER BY r.requested_at DESC
        """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, lenderUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RepaymentRequest(
                      rs.getInt("req_id"),
                      rs.getInt("transaction_id"),
                      rs.getString("borrower_username"),
                      rs.getDouble("amount"),
                      rs.getString("reason"),
                      rs.getDate("transaction_date")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
              "Error loading pending requests:\n" + ex.getMessage(),
              "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }


        public static void rejectRepaymentRequest(int requestId) {
        String sql = """
          UPDATE repayment_requests
             SET status = 'cancelled',
                 responded_at = ?
           WHERE id = ?
        """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, requestId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,
              "Request rejected.",
              "Rejected", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
              "Failed to reject request:\n" + ex.getMessage(),
              "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void approveRepaymentRequest(int requestId, int transactionId) {
        String delTxn = " DELETE FROM transactions WHERE id = ?";
        String updReq = """
            UPDATE repayment_requests
               SET status = 'acknowledged',
                   responded_at = ?
             WHERE id = ?
        """;
        try {
            MySQLConnection.connection.setAutoCommit(false);
    
            // 1) Delete the transaction itself
            try (PreparedStatement ps1 = 
                   MySQLConnection.connection.prepareStatement(delTxn)) {
                ps1.setInt(1, transactionId);
                ps1.executeUpdate();
            }
    
            // 2) Mark the request as acknowledged
            try (PreparedStatement ps2 = 
                   MySQLConnection.connection.prepareStatement(updReq)) {
                ps2.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps2.setInt(2, requestId);
                ps2.executeUpdate();
            }
    
            MySQLConnection.connection.commit();
            JOptionPane.showMessageDialog(
                null,
                "Request approved and transaction removed.",
                "Approved",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException ex) {
            try { MySQLConnection.connection.rollback(); } catch (Exception ignored) {}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Failed to approve request:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try { MySQLConnection.connection.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }



    public static class RepaymentHistory {
        public final int    requestId;
        public final String lenderUsername;
        public final String status;        // "acknowledged" or "cancelled"
        public final Timestamp respondedAt;
    
        public RepaymentHistory(int requestId,
                                String lenderUsername,
                                String status,
                                Timestamp respondedAt) {
            this.requestId      = requestId;
            this.lenderUsername = lenderUsername;
            this.status         = status;
            this.respondedAt    = respondedAt;
        }
    }

    public static List<RepaymentHistory> fetchRequestHistory() {
        List<RepaymentHistory> list = new ArrayList<>();
        String sql = """
          SELECT id, lender_username, status, responded_at
            FROM repayment_requests
           WHERE borrower_username = ?
             AND status <> 'pending'
           ORDER BY requested_at DESC
        """;
        try (PreparedStatement ps = MySQLConnection.connection.prepareStatement(sql)) {
            ps.setString(1, Session.currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RepaymentHistory(
                      rs.getInt("id"),
                      rs.getString("lender_username"),
                      rs.getString("status"),
                      rs.getTimestamp("responded_at")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
              "Error loading request history:\n" + ex.getMessage(),
              "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }


}

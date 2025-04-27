package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import db.MySQLConnection;
import utils.Session;
import utils.SessionManager;

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

}


import ui.SignUpFrame;
import utils.Session;
import utils.SessionManager;

import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;
import db.MySQLConnection;

import ui.ExpenseTracker;
import ui.LoginFrame;



public class App {
    public static void main(String[] args) {

         try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf.");
        }



        MySQLConnection.buildConnection();
        if (MySQLConnection.connection == null) {
            System.err.println("App closed due to DB connection failure.");
            System.exit(0);
        }
        String savedUser = SessionManager.loadSession();

        if (savedUser != null) {
            Session.currentUsername = savedUser;
            // new DashboardUI().setVisible(true);
                        new ExpenseTracker().setVisible(true);

        } else {
            // new ui.LoginFrame().setVisible(true);
            new ui.LandingFrame().setVisible(true);
        }
        
    }

}

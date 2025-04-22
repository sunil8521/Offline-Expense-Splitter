// public class App {
//     public static void main(String[] args) throws Exception {
//         System.out.println("Hello, World!");
//     }
// }
import java.sql.*;

class MySQLConnection {
    private final static boolean IS_LOCALHOST = true;
    private final static String URL = IS_LOCALHOST
            ? "jdbc:mysql://localhost:3306/splitwise"
            : "jdbc:mysql://localhost:3305/sqldatabase";
    private final static String USER = IS_LOCALHOST ? "root" : "user";
    private final static String PASSWORD = "admin12";
    static Connection connection = null;

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL!");
            return connection;
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
        return null;
    }
}

class MysqlAction {
    private final Connection connection;

    public MysqlAction(Connection connection) {
        this.connection = connection;
    }

    public void fetchAllUsers() {
        String selectSQL = "SELECT id, name, email FROM users";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(selectSQL)) {

            System.out.println("User List:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println(id + " | " + name + " | " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String name, String email) {
        String insertSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            int rows = pstmt.executeUpdate();
            System.out.println("Inserted " + rows + " user(s)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeleteUser(int id){
        String sqlQuery="DELETE FROM users WHERE id = ? ";

        try{
            PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("No user found with that ID.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("MySQL connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
public class App {
    public static void main(String[] args) {
        Connection conn = MySQLConnection.getConnection();

        if (conn != null) {
            MysqlAction db = new MysqlAction(conn);
            // db.insertUser("Anil", "ani@example.com");
            db.fetchAllUsers();
            // db.DeleteUser(2);

            db.closeConnection();
        }
    }

}

package auth;

import java.io.*;

public class SessionManager {
    private static final String SESSION_FILE = "session.txt";

    public static void saveSession(String username) {
        try (FileWriter writer = new FileWriter(SESSION_FILE)) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadSession() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            return reader.readLine();
        } catch (IOException e) {
            return null; // No session
        }
    }

    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}

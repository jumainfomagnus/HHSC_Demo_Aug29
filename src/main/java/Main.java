import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class Main {
    private static final String DB_URL  = System.getenv("DB_URL") != null ?
            System.getenv("DB_URL")  : "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN    = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static PrintWriter auditLog;

    static {
        try {
            auditLog = new PrintWriter(new FileWriter("application.log", true));
        } catch (IOException e) {
            System.err.println("Could not open audit log file");
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        log("INFO","Application started");

        if (DB_USER == null || DB_PASSWORD == null) {
            log("SEVERE", "Missing DB credentials");
            System.err.println("Error: Database credentials not configured.");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a username: ");
        String username = sanitize(scanner.nextLine());
        if (!isValidUsername(username)) {
            log("WARN", "Invalid username attempted: " + username);
            System.err.println("Error: Invalid username format.");
            scanner.close();
            return;
        }

        System.out.print("Enter your email: ");
        String email = sanitize(scanner.nextLine());
        if (!isValidEmail(email)) {
            log("WARN", "Invalid email attempted: " + email);
            System.err.println("Error: Invalid email format.");
            scanner.close();
            return;
        }

        scanner.close();

        String query = "SELECT username, email FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            log("INFO", "Querying DB for username: " + username);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean userFound = false;
                while (rs.next()) {
                    userFound = true;
                    System.out.println("Username: " + rs.getString("username"));
                    System.out.println("Email: " + rs.getString("email"));
                }
                if (!userFound) {
                    log("INFO", "No user found with username: " + username);
                    System.out.println("No user found with username: " + username);
                }
            }

        } catch (SQLException e) {
            String errorId = UUID.randomUUID().toString();
            log("ERROR", "Database error [" + errorId + "]: " + e.getMessage());
            System.err.println("Database error occurred. Error ID: " + errorId +
                    ". Please contact support with this ID.");
        }

        auditLog.close();
    }

    private static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.length() <= 255 && EMAIL_PATTERN.matcher(email).matches();
    }

    private static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[\n\r\t]", "").substring(0, Math.min(input.length(), 255));
    }

    private static void log(String level, String message) {
        String line = LocalDateTime.now() + " [" + level + "] " + message;
        auditLog.println(line);
        auditLog.flush();
    }
}
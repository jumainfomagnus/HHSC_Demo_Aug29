import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final String DB_URL = System.getenv("DB_URL") != null ?
            System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static void main(String[] args) {
        if (DB_USER == null || DB_PASSWORD == null) {
            System.err.println("Error: Database credentials not configured. " +
                    "Please set DB_USER and DB_PASSWORD environment variables.");
            System.exit(1);
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a username: ");
        String username = scanner.nextLine().trim();
        if (!isValidUsername(username)) {
            System.err.println("Error: Invalid username format. " +
                    "Username must be 3-20 characters and contain only letters, numbers, and underscores.");
            scanner.close();
            return;
        }

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        if (!isValidEmail(email)) {
            System.err.println("Error: Invalid email format.");
            scanner.close();
            return;
        }

        scanner.close();

        String query = "SELECT username, email FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean userFound = false;
                while (rs.next()) {
                    userFound = true;
                    System.out.println("Username: " + rs.getString("username"));
                    System.out.println("Email: " + rs.getString("email"));
                }
                
                if (!userFound) {
                    System.out.println("No user found with username: " + username);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database error occurred. Please contact support.");
        }
    }
    
    private static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.length() <= 255 && EMAIL_PATTERN.matcher(email).matches();
    }
}
import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class demonstrates various code issues that GitHub Copilot can detect
 * WARNING: This code contains intentional security vulnerabilities and bad practices
 * DO NOT USE IN PRODUCTION
 */
public class ProblematicCode {
    
    // Security Issue: Hardcoded credentials
    private static final String DATABASE_PASSWORD = "admin123";
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String ENCRYPTION_KEY = "mySecretKey123";
    
    // Code Quality Issue: Unused imports and variables
    private static String unusedVariable = "This variable is never used";
    private int anotherUnusedField;
    
    // Performance Issue: Inefficient data structures
    private Vector<String> userList = new Vector<>(); // Vector is synchronized and slower
    
    public static void main(String[] args) {
        ProblematicCode app = new ProblematicCode();
        
        // Security Issue: SQL Injection vulnerability
        String username = args.length > 0 ? args[0] : "admin";
        app.vulnerableLogin(username);
        
        // Resource leak: Connection not closed properly
        app.leakyDatabaseConnection();
        
        // Performance Issue: String concatenation in loop
        app.inefficientStringBuilding();
        
        // Security Issue: Weak cryptography
        app.weakEncryption("sensitive data");
        
        // Logic Error: Potential null pointer exception
        app.nullPointerRisk(null);
        
        // Thread safety issue
        app.unsafeThreadOperation();
    }
    
    // SQL Injection Vulnerability
    public boolean vulnerableLogin(String username) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users", 
                "root", 
                DATABASE_PASSWORD); // Hardcoded password
            
            // SQL Injection vulnerability - never do this!
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            return rs.next();
            
        } catch (Exception e) {
            e.printStackTrace(); // Security issue: exposing stack trace
            return false;
        }
        // Resource leak: connection never closed
    }
    
    // Resource Management Issues
    public void leakyDatabaseConnection() {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "user", "pass");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM data");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            // Resources never closed - memory leak!
            
        } catch (SQLException e) {
            // Empty catch block - bad practice
        }
    }
    
    // Performance Issues
    public String inefficientStringBuilding() {
        String result = "";
        
        // Inefficient string concatenation in loop
        for (int i = 0; i < 1000; i++) {
            result += "Item " + i + ", "; // Creates many temporary String objects
        }
        
        // Inefficient collection usage
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) { // Should use enhanced for-loop
            if (items.get(i).equals("target")) { // Potential NPE
                break;
            }
        }
        
        return result;
    }
    
    // Cryptography Issues
    public String weakEncryption(String data) {
        try {
            // Weak encryption algorithm
            Cipher cipher = Cipher.getInstance("DES"); // DES is weak, use AES
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception e) {
            return data; // Returning unencrypted data on error!
        }
    }
    
    // Weak Password Hashing
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // MD5 is cryptographically broken
            byte[] hash = md.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            return password; // Returning plain text password!
        }
    }
    
    // Null Pointer and Logic Issues
    public void nullPointerRisk(String input) {
        // Null pointer exception risk
        if (input.length() > 0) { // Should check for null first
            System.out.println("Input: " + input.toUpperCase());
        }
        
        // Logic error: comparison with floating point
        double value = 0.1 + 0.2;
        if (value == 0.3) { // This will likely fail due to floating point precision
            System.out.println("Math works!");
        }
        
        // Array index out of bounds risk
        int[] numbers = {1, 2, 3};
        for (int i = 0; i <= numbers.length; i++) { // Should be < not <=
            System.out.println(numbers[i]);
        }
    }
    
    // Thread Safety Issues
    private int counter = 0;
    
    public void unsafeThreadOperation() {
        // Race condition - not thread safe
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter++; // Not atomic, race condition possible
                userList.add("User" + i); // Vector is synchronized but this pattern isn't safe
            }
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        
        // Potential deadlock scenario
        synchronized(this) {
            synchronized(userList) {
                // Operations that could cause deadlock
                userList.clear();
            }
        }
    }
    
    // Input Validation Issues
    public void unsafeFileOperations(String filename) {
        try {
            // Path traversal vulnerability
            File file = new File("/uploads/" + filename); // No validation of filename
            FileInputStream fis = new FileInputStream(file);
            
            // Buffer overflow risk
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            
            // No validation of file content or size
            System.out.println("Read " + bytesRead + " bytes");
            
        } catch (Exception e) {
            // Generic exception handling
        }
    }
    
    // Code Quality Issues
    public boolean complexMethod(String input, int type, boolean flag, List<String> items) {
        // Method too complex, too many parameters
        if (input != null) {
            if (type == 1) {
                if (flag) {
                    if (items != null) {
                        if (items.size() > 0) {
                            if (items.contains(input)) {
                                if (input.length() > 5) {
                                    return true; // Deep nesting - hard to read
                                }
                            }
                        }
                    }
                }
            } else if (type == 2) {
                // Duplicate logic
                if (items != null) {
                    if (items.size() > 0) {
                        return items.contains(input);
                    }
                }
            }
        }
        return false;
    }
    
    // Magic Numbers and Hardcoded Values
    public void magicNumbers() {
        try {
            // Magic numbers throughout
            Thread.sleep(5000); // What does 5000 represent?
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (counter > 42) { // Why 42?
            System.out.println("Threshold exceeded");
        }
        
        // Hardcoded array size
        String[] results = new String[100]; // Why 100?
        
        // Magic string
        if ("ADMIN_ROLE".equals(getCurrentUserRole())) {
            // Administrative operations
        }
    }
    
    private String getCurrentUserRole() {
        return "USER_ROLE"; // Dummy implementation
    }
    
    // Memory Leaks and Resource Issues
    static List<String> cache = new ArrayList<>(); // Static collection that grows indefinitely
    
    public void memoryLeakRisk() {
        // Adding items without cleanup
        cache.add("Item " + System.currentTimeMillis());
        
        // Large object creation in loop
        for (int i = 0; i < 10000; i++) {
            byte[] largeArray = new byte[1024 * 1024]; // 1MB each iteration
            // Array goes out of scope but GC pressure increases
        }
    }
    
    // Exception Handling Anti-patterns
    public void poorExceptionHandling() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // Swallowing exceptions
        }
        
        try {
            anotherRiskyOperation();
        } catch (RuntimeException e) {
            throw e; // Re-throwing without adding value
        } catch (Exception e) {
            throw new RuntimeException(e); // Converting checked to unchecked
        }
    }
    
    private void riskyOperation() throws Exception {
        throw new Exception("Something went wrong");
    }
    
    private void anotherRiskyOperation() throws Exception {
        throw new IllegalArgumentException("Invalid argument");
    }
    
    // Unused methods and dead code
    private void unusedMethod() {
        // This method is never called
        System.out.println("Dead code");
    }
    
    @Deprecated
    public void deprecatedMethod() {
        // Deprecated method still being used
        System.out.println("This method is deprecated");
    }
}
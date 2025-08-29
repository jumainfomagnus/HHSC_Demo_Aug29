import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ProblematicCode {
    
    private static final String DATABASE_PASSWORD = "admin123";
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String ENCRYPTION_KEY = "mySecretKey123";
    
    private static String unusedVariable = "This variable is never used";
    private int anotherUnusedField;
    
    private Vector<String> userList = new Vector<>();
    
    public static void main(String[] args) {
        ProblematicCode app = new ProblematicCode();
        
        String username = args.length > 0 ? args[0] : "admin";
        app.vulnerableLogin(username);
        
        app.leakyDatabaseConnection();
        
        app.inefficientStringBuilding();
        
        app.weakEncryption("sensitive data");
        
        app.nullPointerRisk(null);
        
        app.unsafeThreadOperation();
    }
    
    public boolean vulnerableLogin(String username) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users", 
                "root", 
                DATABASE_PASSWORD);
            
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            return rs.next();
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void leakyDatabaseConnection() {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "user", "pass");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM data");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            
        } catch (SQLException e) {
        }
    }
    
    public String inefficientStringBuilding() {
        String result = "";
        
        for (int i = 0; i < 1000; i++) {
            result += "Item " + i + ", ";
        }
        
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals("target")) {
                break;
            }
        }
        
        return result;
    }
    
    public String weakEncryption(String data) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception e) {
            return data;
        }
    }
    
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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
            return password;
        }
    }
    
    public void nullPointerRisk(String input) {
        if (input.length() > 0) {
            System.out.println("Input: " + input.toUpperCase());
        }
        
        double value = 0.1 + 0.2;
        if (value == 0.3) {
            System.out.println("Math works!");
        }
        
        int[] numbers = {1, 2, 3};
        for (int i = 0; i <= numbers.length; i++) {
            System.out.println(numbers[i]);
        }
    }
    
    private int counter = 0;
    
    public void unsafeThreadOperation() {
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter++;
                userList.add("User" + i);
            }
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        
        synchronized(this) {
            synchronized(userList) {
                userList.clear();
            }
        }
    }
    
    public void unsafeFileOperations(String filename) {
        try {
            File file = new File("/uploads/" + filename);
            FileInputStream fis = new FileInputStream(file);
            
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            
            System.out.println("Read " + bytesRead + " bytes");
            
        } catch (Exception e) {
        }
    }
    
    public boolean complexMethod(String input, int type, boolean flag, List<String> items) {
        if (input != null) {
            if (type == 1) {
                if (flag) {
                    if (items != null) {
                        if (items.size() > 0) {
                            if (items.contains(input)) {
                                if (input.length() > 5) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else if (type == 2) {
                if (items != null) {
                    if (items.size() > 0) {
                        return items.contains(input);
                    }
                }
            }
        }
        return false;
    }
    
    public void magicNumbers() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (counter > 42) {
            System.out.println("Threshold exceeded");
        }
        
        String[] results = new String[100];
        
        if ("ADMIN_ROLE".equals(getCurrentUserRole())) {
        }
    }
    
    private String getCurrentUserRole() {
        return "USER_ROLE";
    }
    
    static List<String> cache = new ArrayList<>();
    
    public void memoryLeakRisk() {
        cache.add("Item " + System.currentTimeMillis());
        
        for (int i = 0; i < 10000; i++) {
            byte[] largeArray = new byte[1024 * 1024];
        }
    }
    
    public void poorExceptionHandling() {
        try {
            riskyOperation();
        } catch (Exception e) {
        }
        
        try {
            anotherRiskyOperation();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void riskyOperation() throws Exception {
        throw new Exception("Something went wrong");
    }
    
    private void anotherRiskyOperation() throws Exception {
        throw new IllegalArgumentException("Invalid argument");
    }
    
    private void unusedMethod() {
        System.out.println("Dead code");
    }
    
    @Deprecated
    public void deprecatedMethod() {
        System.out.println("This method is deprecated");
    }
}
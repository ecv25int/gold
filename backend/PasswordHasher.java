import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hashes for our passwords
        System.out.println("admin123 hash: " + encoder.encode("admin123"));
        System.out.println("demo123 hash: " + encoder.encode("demo123"));
        System.out.println("password123 hash: " + encoder.encode("password123"));
        
        // Test verification
        String adminHash = "$2a$10$./k1JL2T6nOPyh/V23ThjOYYJI7mWuaMb5hy8GBOh3k1VjJlYWfPO";
        String demoHash = "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
        String testHash = "$2a$10$qPOE.UiJ.w9UeQShCYbgKOnlgQ4wZmz8V8TG7xJ8l5w7dPYcnfWfm";
        
        System.out.println("\nVerification:");
        System.out.println("admin123 matches: " + encoder.matches("admin123", adminHash));
        System.out.println("demo123 matches: " + encoder.matches("demo123", demoHash));
        System.out.println("password123 matches: " + encoder.matches("password123", testHash));
    }
}

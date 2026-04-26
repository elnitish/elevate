package com.elevate.auth.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashChecker {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Test passwords
        String adminPassword = "admin@123";
        String userPassword = "test@123";

        // Generate hashes
        String adminHash = encoder.encode(adminPassword);
        String userHash = encoder.encode(userPassword);

        System.out.println("Admin password hash: " + adminHash);
        System.out.println("User password hash: " + userHash);

        // Test matching
        System.out.println("\nAdmin password matches: " + encoder.matches(adminPassword, adminHash));
        System.out.println("User password matches: " + encoder.matches(userPassword, userHash));

        // Test with some common hashes that might be in the database
        System.out.println("\n--- Testing common patterns ---");

        // If you have the actual hash from database, test it here
        // Example: String dbHash = "$2a$10$...";
        // System.out.println("DB hash matches admin@123: " +
        // encoder.matches("admin@123", dbHash));
    }
}

package com.svm.ecom.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for generating BCrypt encoded passwords.
 * This is useful for creating password hashes to store in the database.
 * 
 * Run this class to generate BCrypt hashes for your passwords.
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hashes for common test passwords
        String[] passwords = {"A7@kP3!zX9", "Z9#mP4!t@L6x"};
        String[] usernames = {"crm.user", "tailwindweb"};
        
        System.out.println("BCrypt Password Hashes:");
        System.out.println("========================");
        
        for (int i = 0; i < passwords.length; i++) {
            String hash = encoder.encode(passwords[i]);
            System.out.println("Username: " + usernames[i]);
            System.out.println("Plain Password: " + passwords[i]);
            System.out.println("BCrypt Hash: " + hash);
            System.out.println();
        }
        
        System.out.println("SQL INSERT statements:");
        System.out.println("======================");
        
        for (int i = 0; i < passwords.length; i++) {
            String hash = encoder.encode(passwords[i]);
            String role = usernames[i].equals("admin") ? "ADMIN" : "USER";
            System.out.println("INSERT INTO ecomapi_users (username, password, role) VALUES ('" + 
                             usernames[i] + "', '" + hash + "', '" + role + "');");
        }
    }
}
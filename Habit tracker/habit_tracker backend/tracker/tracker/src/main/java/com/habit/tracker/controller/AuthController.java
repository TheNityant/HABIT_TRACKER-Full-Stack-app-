package com.habit.tracker.controller;

import com.habit.tracker.model.User;
import com.habit.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Crucial for Flutter to talk to Java
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            // Return the user object (which includes the user_id)
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Create an account to log in, since the entered credentials are invalid.");
        }
    }
    // 🆕 UPGRADED: The Register Endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // 1. Check if the username is already taken
            if (userRepository.findByUsername(user.getUsername()) != null) {
                return ResponseEntity.status(400).body("Username is already taken");
            }

            // 2. THE FIX: Give the database a dummy email if Flutter didn't send one
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(user.getUsername() + "@test.com");
            }

            // 3. Save the new user to the database
            User savedUser = userRepository.save(user);
            
            // 4. Return the saved user
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            // 🚨 This will print the EXACT database error to your Render logs
            System.out.println("CRASH REASON: " + e.getMessage());
            return ResponseEntity.status(500).body("Database Error: " + e.getMessage());
        }
    }
}
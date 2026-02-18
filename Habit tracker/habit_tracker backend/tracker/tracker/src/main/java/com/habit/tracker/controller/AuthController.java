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
}
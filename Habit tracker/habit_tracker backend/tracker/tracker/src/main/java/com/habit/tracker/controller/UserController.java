package com.habit.tracker.controller;

import com.habit.tracker.model.User;
import com.habit.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 1. Tells Spring: "This class talks to the web (JSON)."
@RequestMapping("/api/users") // 2. The Base URL: http://localhost:8080/api/users
@CrossOrigin(origins = "*") // Allow all origins (Flutter, JavaScript, etc.)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ENDPOINT 1: Register a user
    // URL: POST http://localhost:8080/api/users/register
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // We accept JSON data -> Convert to User object -> Give to Service
        return userService.registerUser(user);
    }

    // ENDPOINT 2: Get all users
    // URL: GET http://localhost:8080/api/users
    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }
}
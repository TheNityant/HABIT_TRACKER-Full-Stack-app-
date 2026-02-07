package com.habit.tracker.service;

import com.habit.tracker.model.User;
import com.habit.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // 1. Tells Spring: "This class holds the business logic."
public class UserService {

    private final UserRepository userRepository;

    // 2. Constructor Injection (The Best Practice)
    // We are asking Spring: "Please give me the UserRepository tool."
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // FEATURE: Register a new user
    public User registerUser(User user) {
        // Logic Check 1: Is the email already taken?
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Logic Check 2: Is the username taken?
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        // If all checks pass, save the user to the database
        return userRepository.save(user);
    }

    // FEATURE: Get all users (for testing)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
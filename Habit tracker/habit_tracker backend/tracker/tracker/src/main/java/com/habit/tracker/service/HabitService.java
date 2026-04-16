package com.habit.tracker.service;

import com.habit.tracker.model.habit;
import com.habit.tracker.model.User;
import com.habit.tracker.repository.HabitRepository;
import com.habit.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Autowired
    public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    // FEATURE: Create a habit for a specific user
    public habit addHabit(Long userId, habit habit) {
        // 1. Find the User first (We need to know who owns this habit)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Link the habit to the user
        habit.setUser(user);

        // 3. Save to DB
        return habitRepository.save(habit);
    }

    // FEATURE: Get all habits for a specific user
    public List<habit> getHabitsByUserId(Long userId) {
        return habitRepository.findByUserId(userId);
    }
}
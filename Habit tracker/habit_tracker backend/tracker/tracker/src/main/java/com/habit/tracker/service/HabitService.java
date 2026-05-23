package com.habit.tracker.service;

import com.habit.tracker.model.habit;
import com.habit.tracker.model.User;
import com.habit.tracker.repository.HabitRepository;
import com.habit.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        habit.setUser(user);
        return habitRepository.save(habit);
    }

    // FEATURE: Get all habits for a specific user
    public List<habit> getHabitsByUserId(Long userId) {
        return habitRepository.findByUserId(userId);
    }

    // 🟢 FEATURE: Check-in a habit (Pure Database Logic)
    public habit checkInHabit(Long habitId) {
        // 1. Find the habit
        habit existingHabit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found with ID: " + habitId));

        // 2. Get today's date
        LocalDate today = LocalDate.now();

        // 3. Check if already completed today to prevent duplicates
        if (existingHabit.getCompletedDates().contains(today)) {
            throw new RuntimeException("Habit already checked in today!");
        }

        // 4. Add the date and update the streak
        existingHabit.getCompletedDates().add(today);
        existingHabit.setStreakCount(existingHabit.getStreakCount() + 1);

        // 5. Save back to DB
        return habitRepository.save(existingHabit);
    }
}
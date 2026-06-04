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
    public habit checkInHabit(Long habitId, String clientDate) {
    habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found"));

    // 1. 🟢 Parse the incoming string from Flutter into a native LocalDate object
    LocalDate localTargetDate = LocalDate.parse(clientDate);
    
    // 2. 🟢 Capture it as a List of LocalDate to match your model definition
    List<LocalDate> completedDates = habit.getCompletedDates();

    // 🟢 Uses the date sent by the phone instead of the server's UTC clock
    if (completedDates.contains(localTargetDate)) {
        throw new IllegalStateException("Habit already checked in for today");
    }

    completedDates.add(localTargetDate);
    habit.setStreakCount(habit.getStreakCount() + 1);
    
    return habitRepository.save(habit);
}

    public void deleteHabit(Long habitId) {
        habitRepository.deleteById(habitId);
    }

    public void updateHabit(Long id, habit updatedHabit) {
        // Find the existing habit
        habit existingHabit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        // Update the fields
        existingHabit.setTitle(updatedHabit.getTitle());
        existingHabit.setDescription(updatedHabit.getDescription());
        
        // Save the changes
        habitRepository.save(existingHabit);
    }
}
package com.habit.tracker.controller;

import com.habit.tracker.model.habit;
import com.habit.tracker.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    @Autowired
    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    // ENDPOINT: Create a new habit
    // URL: POST http://localhost:8080/api/habits/5 (where 5 is the user_id)
    @PostMapping("/{userId}")
    public habit createHabit(@PathVariable Long userId, @RequestBody habit habit) {
        return habitService.addHabit(userId, habit);
    }

    // ENDPOINT: Get all habits for a user
    // URL: GET http://localhost:8080/api/habits/5
    @GetMapping("/{userId}")
    public List<habit> getUserHabits(@PathVariable Long userId) {
        return habitService.getHabitsByUserId(userId);
    }
}
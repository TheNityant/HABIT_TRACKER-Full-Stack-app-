package com.habit.tracker.controller;

import com.habit.tracker.model.habit;
import com.habit.tracker.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*") // Allow all origins (Flutter, JavaScript, etc.)
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
    
    // FEATURE: Check-in a habit
    @PostMapping("/{id}/checkin")
    public ResponseEntity<?> checkInHabit(@PathVariable Long id) {
        try {
            habit updatedHabit = habitService.checkInHabit(id);
            return ResponseEntity.ok(updatedHabit);
        } catch (RuntimeException e) {
            // Returns a 400 Bad Request if already checked in today
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/ping")
    public String ping() {
       return "pong"; // 🏓 Instant reply, no database needed!
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable Long id) {
        try {
            habitService.deleteHabit(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Edit a Habit
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHabit(@PathVariable Long id, @RequestBody habit updatedHabit) {
        try {
            habitService.updateHabit(id, updatedHabit); // 🟢 Uses the Service now!
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update habit");
        }
    }
} 
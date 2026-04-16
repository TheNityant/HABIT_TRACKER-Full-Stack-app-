package com.habit.tracker.repository;

import com.habit.tracker.model.habit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitRepository extends JpaRepository<habit, Long> {
    // This will look at the "user" field in Habit, grab the ID, and run SQL:
    // SELECT * FROM habits WHERE user_id = ?
    List<habit> findByUserId(Long userId);
}
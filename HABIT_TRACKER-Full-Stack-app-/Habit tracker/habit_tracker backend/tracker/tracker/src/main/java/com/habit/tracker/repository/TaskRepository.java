package com.habit.tracker.repository;

import com.habit.tracker.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // SQL: SELECT * FROM tasks WHERE user_id = ?
    List<Task> findByUserId(Long userId);
}
package com.habit.tracker.controller;

import com.habit.tracker.model.Task;
import com.habit.tracker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // POST: Add a new task
    // http://localhost:8080/api/tasks/5
    @PostMapping("/{userId}")
    public Task createTask(@PathVariable Long userId, @RequestBody Task task) {
        return taskService.addTask(userId, task);
    }

    // GET: Get all tasks
    // http://localhost:8080/api/tasks/5
    @GetMapping("/{userId}")
    public List<Task> getUserTasks(@PathVariable Long userId) {
        return taskService.getTasksByUserId(userId);
    }
}
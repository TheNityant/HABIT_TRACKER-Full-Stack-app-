package com.habit.tracker.service;

import com.habit.tracker.model.Task;
import com.habit.tracker.model.User;
import com.habit.tracker.repository.TaskRepository;
import com.habit.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // CREATE TASK
    public Task addTask(Long userId, Task task) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        task.setUser(user);
        return taskRepository.save(task);
    }
    

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task toggleTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        // Flip the boolean (if true make false, if false make true)
        task.setCompleted(!task.isCompleted());
        
        return taskRepository.save(task);
    }

    
    // GET TASKS
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }
}
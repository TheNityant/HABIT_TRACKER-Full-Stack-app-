package com.habit.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    // --- THE MISSING PART WAS HERE ---
    @Id   // <--- CRITICAL: This marks the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;
    // --------------------------------

    @Column(nullable = false)
    private String title;

    // "High", "Medium", "Low"
    @Column(nullable = false)
    private String priority;

    // "Work", "Personal"
    private String category;

    // Note: We use 'is_completed' in SQL, but 'isCompleted' in Java.
    // Sometimes frameworks get confused, so we explicitly map it.
    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // LINK TO USER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Prevents the "Infinite Loop" crash
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
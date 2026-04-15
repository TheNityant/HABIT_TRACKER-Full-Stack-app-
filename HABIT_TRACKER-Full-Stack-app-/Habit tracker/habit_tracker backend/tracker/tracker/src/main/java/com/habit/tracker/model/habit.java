package com.habit.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "habits")
@Data
public class habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id") // MAPPING: Java 'id' = SQL 'habit_id'
    private Long id;

    @Column(nullable = false)
    private String title; // SQL: title

    private String description; // SQL: description

    @Column(name = "streak_count") // MAPPING: Java 'streakCount' = SQL 'streak_count'
    private int streakCount = 0;

    @Column(name = "created_at", updatable = false) // SQL: created_at
    private LocalDateTime createdAt;

    // --- THE RELATIONSHIP ---
    // This tells Java: "Use the 'user_id' column in THIS table to find the User."
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // <--- Add this here too!
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
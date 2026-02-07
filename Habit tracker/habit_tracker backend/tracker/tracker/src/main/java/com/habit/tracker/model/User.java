package com.habit.tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Matches your table name
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // MAPPING: Java 'id' = SQL 'user_id' (The real PK)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // SQL: username

    @Column(nullable = false, unique = true)
    private String email;    // SQL: email

    @Column(nullable = false)
    private String password; // SQL: password

    @Column(name = "xp_score") // MAPPING: Java 'xpScore' = SQL 'xp_score'
    private int xpScore = 0;

    @Column(name = "created_at", updatable = false) // SQL: created_at
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
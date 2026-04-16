package com.habit.tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty; // For Mapping json since flutter needs User_id and lombok sense only the id
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Matches your table name
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // MAPPING: Java 'id' = SQL 'user_id' (The real PK)
    @JsonProperty("user_id")  // 👈 CRITICAL: Tells API to send this as "user_id" to Flutter
    private Long id;

    @Column(name= "username", unique = true)
    private String username; // SQL: username

    @Column(name= "email", unique = true)
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

    // no manual getters/setters needed thanks to Lombok's @Data
}
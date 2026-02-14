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

    // --- GETTERS AND SETTERS ---
    // (You can generate these in IntelliJ/VS Code: Right Click -> Generate -> Getters and Setters)

    public Long getuser_id() { return id;}
    public void setuser_id(Long user_id) { this.id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getXpScore() { return xpScore; }
    public void setXpScore(int xpScore) { this.xpScore = xpScore; }

}
package com.habit.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate; // 🟢 Added
import java.util.List;      // 🟢 Added
import java.util.ArrayList; // 🟢 Added

@Entity
@Table(name = "habits")
@Data // This automatically creates getCompletedDates() for you!
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

    // 🟢 ADDED: This creates the join table we talked about!
    @ElementCollection
    @CollectionTable(name = "habit_completed_dates", joinColumns = @JoinColumn(name = "habit_id"))
    @Column(name = "completed_date")
    private List<LocalDate> completedDates = new ArrayList<>();

    // --- THE RELATIONSHIP ---
    // This tells Java: "Use the 'user_id' column in THIS table to find the User."
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Prevents infinite JSON loops
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private String category; // 🟢 ADD THIS FIELD

    // --- Add the corresponding Getter and Setter ---
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
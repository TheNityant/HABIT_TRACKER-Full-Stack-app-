package com.habit.tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "journals")
@Data // 🟢 Generates all Getters, Setters, toString(), equals(), and hashCode()!
@NoArgsConstructor // 🟢 Generates the empty constructor that Spring/Hibernate requires
@AllArgsConstructor // 🟢 (Optional) Generates a constructor with all properties
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String type;
    private String details;
    private LocalDateTime entryDate;
    private String mediaUrl;

    // Notice how clean this is? No getters or setters needed here anymore!
}
package com.habit.tracker.controller;

import com.habit.tracker.model.Journal;
import com.habit.tracker.service.JournalService;
import com.habit.tracker.service.AI_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/journal") // 🟢 Matches your Flutter ApiService URL
@CrossOrigin(origins = "*") // Allows Flutter to talk to Spring Boot
public class JournalController {

    @Autowired
    private JournalService journalService;
    @Autowired
    private AI_Service AI_Service;
    // 1. GET entries for a user on a specific date
    @GetMapping("/{userId}")
    public ResponseEntity<List<Journal>> getEntries(
            @PathVariable Long userId,
            @RequestParam("date") String dateStr) {
        
        LocalDate date = LocalDate.parse(dateStr);
        
        // Convert the flat date into a 24-hour window
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(java.time.LocalTime.MAX);
        
        // Pass the two LocalDateTime arguments to the service
        List<Journal> entries = journalService.getJournalsByDate(userId, startOfDay, endOfDay);
        return ResponseEntity.ok(entries);
    }

    // 2. POST a new log entry
    @PostMapping("/{userId}")
    public ResponseEntity<Journal> createEntry(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "true") boolean useAi, // 🟢 ADD THIS
            @RequestBody Journal incomingJournal) { 
        
        incomingJournal.setUserId(userId);
        if (incomingJournal.getEntryDate() == null) {
          // Changed LocalDate to LocalDateTime
        incomingJournal.setEntryDate(java.time.LocalDateTime.now()); 
        }

        // 🟢 If AI is requested, parse it. Otherwise, save the raw manual input!
        if (useAi) {
            Journal smartData = AI_Service.analyzeAndParseEntry(incomingJournal.getContent(), incomingJournal.getMediaUrl());
            incomingJournal.setType(smartData.getType());
            // If the AI thinks it's a metric, use the AI content. Otherwise, keep the original text.
            incomingJournal.setContent(smartData.getContent());
            incomingJournal.setDetails(smartData.getDetails());
        } else {
            // Keep exactly what the user typed in manual mode
            if(incomingJournal.getType() == null || incomingJournal.getType().isEmpty()) {
                incomingJournal.setType("journal"); // fallback
            }
            incomingJournal.setDetails("");
        }
        
        Journal savedEntry = journalService.saveJournal(incomingJournal);
        return ResponseEntity.ok(savedEntry);
    }

    // 3. DELETE a log entry
    @DeleteMapping("/{entryId}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long entryId) {
        if (journalService.deleteJournal(entryId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Edit a Journal/Metric Entry
    @PutMapping("/{entryId}")
    public ResponseEntity<?> updateEntry(@PathVariable Long entryId, @RequestBody Journal updatedJournal) {
        try {
            journalService.updateJournal(entryId, updatedJournal);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update log entry");
        }
    }
}
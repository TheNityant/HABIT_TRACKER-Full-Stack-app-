package com.habit.tracker.controller;

import com.habit.tracker.model.Journal;
import com.habit.tracker.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journal") // 🟢 Matches your Flutter ApiService URL
@CrossOrigin(origins = "*") // Allows Flutter to talk to Spring Boot
public class JournalController {

    @Autowired
    private JournalService journalService;

    // 1. GET entries for a user on a specific date
    @GetMapping("/{userId}")
    public ResponseEntity<List<Journal>> getEntries(
            @PathVariable Long userId,
            @RequestParam("date") String dateStr) {
        
        LocalDate date = LocalDate.parse(dateStr);
        List<Journal> entries = journalService.getJournalsByDate(userId, date);
        return ResponseEntity.ok(entries);
    }

    // 2. POST a new log entry
    @PostMapping("/{userId}")
    public ResponseEntity<Journal> createEntry(
            @PathVariable Long userId,
            @RequestBody Journal journal) {
        
        journal.setUserId(userId);
        Journal savedEntry = journalService.saveJournal(journal);
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
}
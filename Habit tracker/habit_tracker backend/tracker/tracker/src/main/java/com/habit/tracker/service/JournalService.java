package com.habit.tracker.service;

import com.habit.tracker.model.Journal;
import com.habit.tracker.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    public List<Journal> getJournalsByDate(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return journalRepository.findByUserIdAndEntryDateBetweenOrderByIdAsc(userId, startOfDay, endOfDay);
    }

    public Journal saveJournal(Journal journal) {
        return journalRepository.save(journal);
    }

    public boolean deleteJournal(Long id) {
        if (journalRepository.existsById(id)) {
            journalRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Journal updateJournal(Long id, Journal updatedJournal) {
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log entry not found"));
        
        existingJournal.setContent(updatedJournal.getContent());
        existingJournal.setDetails(updatedJournal.getDetails());
        
        return journalRepository.save(existingJournal);
    }
}
package com.habit.tracker.repository;

import com.habit.tracker.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    // Finds all logs for a specific user on a specific day, ordered by when they were created
    List<Journal> findByUserIdAndEntryDateOrderByIdAsc(Long userId, LocalDate entryDate);
}
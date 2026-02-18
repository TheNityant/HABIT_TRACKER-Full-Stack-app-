package com.habit.tracker.repository;

import com.habit.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Type, ID_Type>
// We tell it: "Manage the 'User' table, and the ID is a 'Long' number."
public interface UserRepository extends JpaRepository<User, Long> {

    // MAGIC: We just declare the method name, and Spring WRITES the SQL for us!

    // SQL generated: SELECT * FROM users WHERE username = ?
    User findByUsername(String username);

    // SQL generated: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
// =============================================================================
// FILE        : ApiDataModels.java
// DESCRIPTION : Data Transfer Objects (DTOs) for API communication
// =============================================================================

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * User DTO for authentication and registration
 */
class UserDto {
    public Long user_id;  // From backend
    public Long id;       // Alternative field name from backend
    public String username;
    public String email;
    public Integer xpScore;
    public String createdAt;
    
    public UserDto() {}
    
    public UserDto(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    @Override
    public String toString() {
        return username + " (ID: " + (user_id != null ? user_id : id) + ")";
    }
}

/**
 * Habit DTO for API communication
 */
class HabitDto {
    public Long habit_id;      // Backend field name
    public Long id;            // Alternative field name
    public String title;
    public String description;
    public Integer streakCount;
    public String category;    // Added field for frontend
    public String createdAt;
    public String status;      // Added field for frontend: ✔ Done or ○ Pending
    public Long user_id;       // Reference to owner user
    
    public HabitDto() {}
    
    public HabitDto(String title, String description) {
        this.title = title;
        this.description = description;
        this.streakCount = 0;
    }
    
    /**
     * Convert backend HabitDto to frontend Habit object
     */
    public static Habit toFrontendHabit(HabitDto dto) {
        Long habitId = dto.habit_id != null ? dto.habit_id : dto.id;
        return new Habit(
            String.valueOf(habitId),
            dto.title,
            dto.category != null ? dto.category : "Other",
            dto.streakCount != null ? dto.streakCount : 0,
            dto.createdAt != null ? dto.createdAt : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            ""  // Empty completed days (will be synced from server if available)
        );
    }
    
    @Override
    public String toString() {
        return title + " (Streak: " + streakCount + ")";
    }
}

/**
 * Task DTO for API communication
 */
class TaskDto {
    public Long task_id;       // Backend field name
    public Long id;            // Alternative field name
    public String title;
    public String priority;    // HIGH, MEDIUM, LOW
    public String category;    // Work, Personal
    public Boolean isCompleted;
    public String createdAt;
    public Long user_id;       // Reference to owner user
    
    public TaskDto() {}
    
    public TaskDto(String title, String priority, String category) {
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.isCompleted = false;
    }
    
    @Override
    public String toString() {
        return title + " [" + priority + "]";
    }
}

/**
 * Login request DTO
 */
class LoginRequest {
    public String username;
    public String password;
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

/**
 * Generic API response wrapper (if backend uses this pattern)
 */
class ApiResponse<T> {
    public boolean success;
    public String message;
    public T data;
    
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}

/**
 * Paged response for large lists
 */
class PaginatedResponse<T> {
    public List<T> content;
    public int totalElements;
    public int totalPages;
    public int pageNumber;
    
    public PaginatedResponse() {
        this.content = new ArrayList<>();
    }
}

// =============================================================================
// FILE        : ApiManager.java
// DESCRIPTION : High-level API manager for Habit Tracker operations
// =============================================================================

import java.net.http.HttpResponse;
import java.util.List;

public class ApiManager {
    
    private static UserDto currentUser = null;
    
    /**
     * Login user and store authentication info
     */
    public static UserDto login(String username, String password) throws ApiException {
        try {
            LoginRequest loginReq = new LoginRequest(username, password);
            String jsonBody = JsonSerializer.toJson(loginReq);
            
            HttpResponse<String> response = ApiClient.post(ApiConfig.AUTH_LOGIN, jsonBody);
            UserDto user = JsonSerializer.parseUser(response.body());
            
            currentUser = user;
            return user;
            
        } catch (ApiException e) {
            throw new ApiException("Login failed: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse login response: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Register new user
     */
    public static UserDto register(String username, String email, String password) throws ApiException {
        try {
            UserDto newUser = new UserDto(username, email);
            // Note: Password is not sent to backend in this DTO, might need adjustment
            String jsonBody = JsonSerializer.toJson(newUser);
            
            HttpResponse<String> response = ApiClient.post(ApiConfig.USER_REGISTER, jsonBody);
            UserDto registeredUser = JsonSerializer.parseUser(response.body());
            
            return registeredUser;
            
        } catch (ApiException e) {
            throw new ApiException("Registration failed: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse registration response: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Logout current user
     */
    public static void logout() {
        currentUser = null;
        ApiClient.clearAuthToken();
    }
    
    /**
     * Get current logged-in user
     */
    public static UserDto getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * Add new habit for current user
     */
    public static HabitDto addHabit(String title, String category) throws ApiException {
        if (!isAuthenticated()) {
            throw new ApiException("User not authenticated", 401);
        }
        
        try {
            HabitDto habit = new HabitDto(title, null);
            habit.category = category;
            
            String endpoint = ApiConfig.HABIT_ADD.replace("{userId}", String.valueOf(currentUser.user_id != null ? currentUser.user_id : currentUser.id));
            String jsonBody = JsonSerializer.toJson(habit);
            
            HttpResponse<String> response = ApiClient.post(endpoint, jsonBody);
            return JsonSerializer.parseHabit(response.body());
            
        } catch (ApiException e) {
            throw new ApiException("Failed to add habit: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse habit response: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Get all habits for current user
     */
    public static List<HabitDto> getAllHabits() throws ApiException {
        if (!isAuthenticated()) {
            throw new ApiException("User not authenticated", 401);
        }
        
        try {
            String endpoint = ApiConfig.HABIT_GET_ALL.replace("{userId}", String.valueOf(currentUser.user_id != null ? currentUser.user_id : currentUser.id));
            HttpResponse<String> response = ApiClient.get(endpoint);
            return JsonSerializer.parseHabitList(response.body());
            
        } catch (ApiException e) {
            throw new ApiException("Failed to fetch habits: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse habits: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Add new task for current user
     */
    public static TaskDto addTask(String title, String priority, String category) throws ApiException {
        if (!isAuthenticated()) {
            throw new ApiException("User not authenticated", 401);
        }
        
        try {
            TaskDto task = new TaskDto(title, priority, category);
            
            String endpoint = ApiConfig.TASK_ADD.replace("{userId}", String.valueOf(currentUser.user_id != null ? currentUser.user_id : currentUser.id));
            String jsonBody = JsonSerializer.toJson(task);
            
            HttpResponse<String> response = ApiClient.post(endpoint, jsonBody);
            return JsonSerializer.parseTask(response.body());
            
        } catch (ApiException e) {
            throw new ApiException("Failed to add task: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse task response: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Get all tasks for current user
     */
    public static List<TaskDto> getAllTasks() throws ApiException {
        if (!isAuthenticated()) {
            throw new ApiException("User not authenticated", 401);
        }
        
        try {
            String endpoint = ApiConfig.TASK_GET_ALL.replace("{userId}", String.valueOf(currentUser.user_id != null ? currentUser.user_id : currentUser.id));
            HttpResponse<String> response = ApiClient.get(endpoint);
            return JsonSerializer.parseTaskList(response.body());
            
        } catch (ApiException e) {
            throw new ApiException("Failed to fetch tasks: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse tasks: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Delete a task
     */
    public static void deleteTask(Long taskId) throws ApiException {
        try {
            String endpoint = ApiConfig.TASK_DELETE.replace("{taskId}", String.valueOf(taskId));
            ApiClient.delete(endpoint);
            
        } catch (ApiException e) {
            throw new ApiException("Failed to delete task: " + e.getMessage(), e.getStatusCode());
        }
    }
    
    /**
     * Toggle task completion status
     */
    public static TaskDto toggleTaskCompletion(Long taskId) throws ApiException {
        try {
            String endpoint = ApiConfig.TASK_TOGGLE.replace("{taskId}", String.valueOf(taskId));
            HttpResponse<String> response = ApiClient.put(endpoint, "{}");
            return JsonSerializer.parseTask(response.body());
            
        } catch (ApiException e) {
            throw new ApiException("Failed to toggle task: " + e.getMessage(), e.getStatusCode());
        } catch (JsonParseException e) {
            throw new ApiException("Failed to parse task response: " + e.getMessage(), 0);
        }
    }
}

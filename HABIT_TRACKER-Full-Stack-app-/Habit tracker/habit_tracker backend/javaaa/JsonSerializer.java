// =============================================================================
// FILE        : JsonSerializer.java
// DESCRIPTION : JSON serialization/deserialization utility (simple JSON handling without external libs)
// =============================================================================

import java.util.*;
import java.util.regex.*;

public class JsonSerializer {
    
    /**
     * Serialize LoginRequest to JSON
     */
    public static String toJson(LoginRequest req) {
        return String.format(
            "{\"username\":\"%s\",\"password\":\"%s\"}",
            escapeJson(req.username),
            escapeJson(req.password)
        );
    }
    
    /**
     * Serialize HabitDto to JSON
     */
    public static String toJson(HabitDto habit) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"title\":\"").append(escapeJson(habit.title)).append("\"");
        if (habit.description != null) {
            json.append(",\"description\":\"").append(escapeJson(habit.description)).append("\"");
        }
        if (habit.category != null) {
            json.append(",\"category\":\"").append(escapeJson(habit.category)).append("\"");
        }
        if (habit.streakCount != null) {
            json.append(",\"streakCount\":").append(habit.streakCount);
        }
        json.append("}");
        return json.toString();
    }
    
    /**
     * Serialize TaskDto to JSON
     */
    public static String toJson(TaskDto task) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"title\":\"").append(escapeJson(task.title)).append("\"");
        if (task.priority != null) {
            json.append(",\"priority\":\"").append(escapeJson(task.priority)).append("\"");
        }
        if (task.category != null) {
            json.append(",\"category\":\"").append(escapeJson(task.category)).append("\"");
        }
        if (task.isCompleted != null) {
            json.append(",\"isCompleted\":").append(task.isCompleted);
        }
        json.append("}");
        return json.toString();
    }
    
    /**
     * Serialize UserDto to JSON
     */
    public static String toJson(UserDto user) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"username\":\"").append(escapeJson(user.username)).append("\"");
        if (user.email != null) {
            json.append(",\"email\":\"").append(escapeJson(user.email)).append("\"");
        }
        json.append("}");
        return json.toString();
    }
    
    /**
     * Parse JSON response to HabitDto
     */
    public static HabitDto parseHabit(String json) throws JsonParseException {
        HabitDto habit = new HabitDto();
        habit.habit_id = parseLong(json, "habit_id");
        habit.id = parseLong(json, "id");
        habit.title = parseString(json, "title");
        habit.description = parseString(json, "description");
        habit.category = parseString(json, "category");
        habit.streakCount = parseInteger(json, "streakCount");
        habit.createdAt = parseString(json, "createdAt");
        habit.user_id = parseLong(json, "user_id");
        return habit;
    }
    
    /**
     * Parse JSON response to TaskDto
     */
    public static TaskDto parseTask(String json) throws JsonParseException {
        TaskDto task = new TaskDto();
        task.task_id = parseLong(json, "task_id");
        task.id = parseLong(json, "id");
        task.title = parseString(json, "title");
        task.priority = parseString(json, "priority");
        task.category = parseString(json, "category");
        task.isCompleted = parseBoolean(json, "isCompleted");
        task.createdAt = parseString(json, "createdAt");
        task.user_id = parseLong(json, "user_id");
        return task;
    }
    
    /**
     * Parse JSON response to UserDto
     */
    public static UserDto parseUser(String json) throws JsonParseException {
        UserDto user = new UserDto();
        user.user_id = parseLong(json, "user_id");
        user.id = parseLong(json, "id");
        user.username = parseString(json, "username");
        user.email = parseString(json, "email");
        user.xpScore = parseInteger(json, "xpScore");
        user.createdAt = parseString(json, "createdAt");
        return user;
    }
    
    /**
     * Parse JSON array to List of HabitDtos
     */
    public static List<HabitDto> parseHabitList(String json) throws JsonParseException {
        List<HabitDto> habits = new ArrayList<>();
        
        // Extract array content between [ and ]
        int startIdx = json.indexOf('[');
        int endIdx = json.lastIndexOf(']');
        if (startIdx < 0 || endIdx < 0) {
            throw new JsonParseException("Invalid JSON array format");
        }
        
        String arrayContent = json.substring(startIdx + 1, endIdx);
        
        // Split by },{
        String[] objects = arrayContent.split("\\}\\s*,\\s*\\{");
        
        for (String obj : objects) {
            String jsonObj = obj.replaceAll("^\\{*", "{").replaceAll("\\}*$", "}");
            if (!jsonObj.trim().isEmpty()) {
                habits.add(parseHabit(jsonObj));
            }
        }
        
        return habits;
    }
    
    /**
     * Parse JSON array to List of TaskDtos
     */
    public static List<TaskDto> parseTaskList(String json) throws JsonParseException {
        List<TaskDto> tasks = new ArrayList<>();
        
        int startIdx = json.indexOf('[');
        int endIdx = json.lastIndexOf(']');
        if (startIdx < 0 || endIdx < 0) {
            throw new JsonParseException("Invalid JSON array format");
        }
        
        String arrayContent = json.substring(startIdx + 1, endIdx);
        String[] objects = arrayContent.split("\\}\\s*,\\s*\\{");
        
        for (String obj : objects) {
            String jsonObj = obj.replaceAll("^\\{*", "{").replaceAll("\\}*$", "}");
            if (!jsonObj.trim().isEmpty()) {
                tasks.add(parseTask(jsonObj));
            }
        }
        
        return tasks;
    }
    
    // Helper parsing methods
    private static String parseString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }
        return null;
    }
    
    private static Long parseLong(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }
    
    private static Integer parseInteger(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }
    
    private static Boolean parseBoolean(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return null;
    }
    
    private static String escapeJson(String value) {
        if (value == null) return "";
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
    
    private static String unescapeJson(String value) {
        return value
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t");
    }
}

class JsonParseException extends Exception {
    public JsonParseException(String message) {
        super(message);
    }
    
    public JsonParseException(String message, Exception cause) {
        super(message, cause);
    }
}

// =============================================================================
// FILE        : ApiConfig.java
// DESCRIPTION : Configuration for backend API connections. Supports environment switching.
// =============================================================================

public class ApiConfig {
    
    // Backend environment configuration
    public static final String BACKEND_ENV = System.getenv("HABIT_TRACKER_ENV") != null 
        ? System.getenv("HABIT_TRACKER_ENV") 
        : "development";  // development, staging, production
    
    // Backend URLs - modify based on your deployment
    public static final String BACKEND_URL_DEVELOPMENT = "http://localhost:8080";
    public static final String BACKEND_URL_STAGING = "http://localhost:8080";  // Update with staging URL if needed
    public static final String BACKEND_URL_PRODUCTION = "https://habit-tracker-backend-o9bs.onrender.com";  // UPDATE with actual Render URL
    
    public static final String BACKEND_URL = getBackendUrl();
    
    // API endpoints
    public static final String AUTH_LOGIN = "/api/auth/login";
    public static final String AUTH_REGISTER = "/api/auth/register";
    public static final String HABIT_ADD = "/api/habits/{userId}";
    public static final String HABIT_GET_ALL = "/api/habits/{userId}";
    public static final String TASK_ADD = "/api/tasks/{userId}";
    public static final String TASK_GET_ALL = "/api/tasks/{userId}";
    public static final String TASK_DELETE = "/api/tasks/{taskId}";
    public static final String TASK_TOGGLE = "/api/tasks/{taskId}/toggle";
    public static final String USER_REGISTER = "/api/users/register";
    
    // Network configuration
    public static final int CONNECT_TIMEOUT_SECONDS = 10;
    public static final int READ_TIMEOUT_SECONDS = 15;
    public static final int MAX_RETRIES = 3;
    public static final long RETRY_DELAY_MS = 1000;  // 1 second between retries
    
    // JWT/Session handling (for future authentication)
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Get the appropriate backend URL based on environment
     */
    private static String getBackendUrl() {
        switch (BACKEND_ENV.toLowerCase()) {
            case "production":
            case "prod":
                return BACKEND_URL_PRODUCTION;
            case "staging":
                return BACKEND_URL_STAGING;
            case "development":
            case "dev":
            default:
                return BACKEND_URL_DEVELOPMENT;
        }
    }
    
    /**
     * Update the production backend URL at runtime
     * Call this after determining the actual Render URL
     */
    public static void setProductionBackendUrl(String url) {
        // Note: This is a static reference, so this is informational
        System.out.println("Backend URL configuration: " + BACKEND_URL);
    }
}

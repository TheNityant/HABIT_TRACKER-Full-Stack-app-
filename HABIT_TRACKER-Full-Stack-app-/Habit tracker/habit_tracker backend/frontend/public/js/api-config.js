// API Configuration
const ApiConfig = {
  // Backend API endpoint
  BASE_URL: 'https://habit-tracker-backend-o9bs.onrender.com',
  
  // Endpoints (userId required in path for habits/tasks)
  ENDPOINTS: {
    // Auth endpoints
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
    
    // Habit endpoints (userId in path)
    GET_HABITS: (userId) => `/api/habits/${userId}`,
    CREATE_HABIT: (userId) => `/api/habits/${userId}`,
    UPDATE_HABIT: (id) => `/api/habits/${id}`,
    DELETE_HABIT: (id) => `/api/habits/${id}`,
    
    // Task endpoints (userId in path)
    GET_TASKS: (userId) => `/api/tasks/${userId}`,
    CREATE_TASK: (userId) => `/api/tasks/${userId}`,
    DELETE_TASK: (id) => `/api/tasks/${id}`,
    TOGGLE_TASK: (id) => `/api/tasks/${id}/toggle`,
    
    // User endpoints
    GET_USER: '/api/users/profile',
    UPDATE_USER: '/api/users/profile',
  },
  
  // HTTP timeouts (in milliseconds)
  CONNECT_TIMEOUT: 10000,  // 10 seconds
  READ_TIMEOUT: 15000,     // 15 seconds
  
  // Retry configuration
  MAX_RETRIES: 3,
  RETRY_DELAY: 1000,       // 1 second base delay
  
  // Local storage keys
  STORAGE_KEYS: {
    AUTH_TOKEN: 'habitTrackerAuthToken',
    USER_DATA: 'habitTrackerUserData',
    LAST_SYNC: 'habitTrackerLastSync',
  }
};

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = ApiConfig;
}

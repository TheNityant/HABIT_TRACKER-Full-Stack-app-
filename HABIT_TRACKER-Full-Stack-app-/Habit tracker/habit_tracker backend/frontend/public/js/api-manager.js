// API Manager - High-level API operations and session management
class ApiManager {
  constructor(apiClient, config) {
    this.apiClient = apiClient;
    this.config = config;
    this.currentUser = null;
    this.habits = [];
    this.tasks = [];
    this.loadFromStorage();
  }

  // ==================== Authentication ====================

  /**
   * Login user
   */
  async login(username, password) {
    try {
      const response = await this.apiClient.post(this.config.ENDPOINTS.LOGIN, {
        username: username,
        password: password,
      });

      // Store auth token
      const token = response.token || response.accessToken || response.access_token;
      if (token) {
        localStorage.setItem(this.config.STORAGE_KEYS.AUTH_TOKEN, token);
      }

      // Store user data
      this.currentUser = response.user || response;
      this.saveToStorage();

      return this.currentUser;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  /**
   * Register new user
   */
  async register(username, password, email = '') {
    try {
      const response = await this.apiClient.post(this.config.ENDPOINTS.REGISTER, {
        username: username,
        password: password,
        email: email || (username + '@test.com'),
      });

      // Store auth token
      const token = response.token || response.accessToken || response.access_token;
      if (token) {
        localStorage.setItem(this.config.STORAGE_KEYS.AUTH_TOKEN, token);
      }

      // Store user data
      this.currentUser = response.user || response;
      this.saveToStorage();

      return this.currentUser;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  /**
   * Logout user
   */
  logout() {
    localStorage.removeItem(this.config.STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(this.config.STORAGE_KEYS.USER_DATA);
    this.currentUser = null;
    this.habits = [];
    this.tasks = [];
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated() {
    return !!localStorage.getItem(this.config.STORAGE_KEYS.AUTH_TOKEN) && !!this.currentUser;
  }

  // ==================== Habits CRUD ====================

  /**
   * Get all habits for current user
   */
  async getAllHabits() {
    try {
      console.log('🔍 getAllHabits called');
      console.log('📌 currentUser:', this.currentUser);
      
      if (!this.currentUser || !this.currentUser.id && !this.currentUser.user_id) {
        throw new Error('User not authenticated or missing user ID');
      }
      
      const userId = this.currentUser.id || this.currentUser.user_id;
      console.log('📍 Fetching habits for userId:', userId);
      
      const endpoint = this.config.ENDPOINTS.GET_HABITS(userId);
      console.log('🔗 API Endpoint:', endpoint);
      
      const response = await this.apiClient.get(endpoint);
      console.log('✅ API Response:', response);
      
      // Handle both array and object responses
      this.habits = Array.isArray(response) ? response : response.habits || response.data || [];
      console.log('📦 Habits after processing:', this.habits);
      
      this.saveToStorage();
      return this.habits;
    } catch (error) {
      console.error('❌ Get habits error:', error);
      throw error;
    }
  }

  /**
   * Add new habit
   */
  async addHabit(habitTitle, description = '') {
    try {
      if (!this.currentUser || !this.currentUser.id && !this.currentUser.user_id) {
        throw new Error('User not authenticated or missing user ID');
      }
      
      const userId = this.currentUser.id || this.currentUser.user_id;
      const habitData = {
        title: habitTitle,      // Backend uses 'title' not 'name'
        description: description || '',
      };

      const response = await this.apiClient.post(this.config.ENDPOINTS.CREATE_HABIT(userId), habitData);
      
      const newHabit = response;
      this.habits.push(newHabit);
      this.saveToStorage();
      return newHabit;
    } catch (error) {
      console.error('Add habit error:', error);
      throw error;
    }
  }

  /**
   * Update habit
   */
  async updateHabit(habitId, habitData) {
    try {
      const response = await this.apiClient.put(
        this.config.ENDPOINTS.UPDATE_HABIT(habitId),
        habitData
      );
      
      const updatedHabit = response.habit || response;
      const index = this.habits.findIndex(h => h.id === habitId || h.habit_id === habitId);
      if (index >= 0) {
        this.habits[index] = updatedHabit;
        this.saveToStorage();
      }
      return updatedHabit;
    } catch (error) {
      console.error('Update habit error:', error);
      throw error;
    }
  }

  /**
   * Delete habit
   */
  async deleteHabit(habitId) {
    try {
      await this.apiClient.delete(this.config.ENDPOINTS.DELETE_HABIT(habitId));
      
      this.habits = this.habits.filter(h => h.id !== habitId && h.habit_id !== habitId);
      this.saveToStorage();
    } catch (error) {
      console.error('Delete habit error:', error);
      throw error;
    }
  }

  // ==================== Tasks CRUD ====================

  /**
   * Get all tasks for current user
   */
  async getAllTasks() {
    try {
      if (!this.currentUser || !this.currentUser.id && !this.currentUser.user_id) {
        throw new Error('User not authenticated or missing user ID');
      }
      
      const userId = this.currentUser.id || this.currentUser.user_id;
      const response = await this.apiClient.get(this.config.ENDPOINTS.GET_TASKS(userId));
      
      this.tasks = Array.isArray(response) ? response : response.tasks || response.data || [];
      this.saveToStorage();
      return this.tasks;
    } catch (error) {
      console.error('Get tasks error:', error);
      throw error;
    }
  }

  /**
   * Get tasks for specific habit
   */
  async getHabitTasks(habitId) {
    try {
      const response = await this.apiClient.get(this.config.ENDPOINTS.GET_HABIT_TASKS(habitId));
      
      return Array.isArray(response) ? response : response.tasks || response.data || [];
    } catch (error) {
      console.error('Get habit tasks error:', error);
      throw error;
    }
  }

  /**
   * Add task to habit
   */
  async addTask(habitId, taskTitle, description = '') {
    try {
      if (!this.currentUser || !this.currentUser.id && !this.currentUser.user_id) {
        throw new Error('User not authenticated or missing user ID');
      }
      
      const userId = this.currentUser.id || this.currentUser.user_id;
      const taskData = {
        title: taskTitle,
        description: description || '',
        habit_id: habitId,
      };

      const response = await this.apiClient.post(this.config.ENDPOINTS.CREATE_TASK(userId), taskData);
      
      const newTask = response;
      this.tasks.push(newTask);
      this.saveToStorage();
      return newTask;
    } catch (error) {
      console.error('Add task error:', error);
      throw error;
    }
  }

  /**
   * Toggle task completion
   */
  async toggleTask(taskId) {
    try {
      const response = await this.apiClient.put(
        this.config.ENDPOINTS.TOGGLE_TASK(taskId),
        { is_completed: true }
      );
      
      const updatedTask = response.task || response;
      const index = this.tasks.findIndex(t => t.id === taskId || t.task_id === taskId);
      if (index >= 0) {
        this.tasks[index] = updatedTask;
        this.saveToStorage();
      }
      return updatedTask;
    } catch (error) {
      console.error('Toggle task error:', error);
      throw error;
    }
  }

  /**
   * Delete task
   */
  async deleteTask(taskId) {
    try {
      await this.apiClient.delete(this.config.ENDPOINTS.DELETE_TASK(taskId));
      
      this.tasks = this.tasks.filter(t => t.id !== taskId && t.task_id !== taskId);
      this.saveToStorage();
    } catch (error) {
      console.error('Delete task error:', error);
      throw error;
    }
  }

  // ==================== Local Storage ====================

  /**
   * Load data from local storage
   */
  loadFromStorage() {
    try {
      const userData = localStorage.getItem(this.config.STORAGE_KEYS.USER_DATA);
      if (userData) {
        const parsed = JSON.parse(userData);
        this.currentUser = parsed.user;
        this.habits = parsed.habits || [];
        this.tasks = parsed.tasks || [];
      }
    } catch (error) {
      console.error('Load from storage error:', error);
    }
  }

  /**
   * Save data to local storage
   */
  saveToStorage() {
    try {
      const data = {
        user: this.currentUser,
        habits: this.habits,
        tasks: this.tasks,
        lastSync: new Date().toISOString(),
      };
      localStorage.setItem(this.config.STORAGE_KEYS.USER_DATA, JSON.stringify(data));
    } catch (error) {
      console.error('Save to storage error:', error);
    }
  }
}

// Create global instance
const apiManager = new ApiManager(apiClient, ApiConfig);

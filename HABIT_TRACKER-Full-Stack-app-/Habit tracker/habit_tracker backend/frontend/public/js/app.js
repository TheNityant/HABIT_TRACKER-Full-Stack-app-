// Main Application Logic
class HabitTrackerApp {
  constructor() {
    this.currentEditingHabitId = null;
    this.init();
  }

  /**
   * Initialize the application
   */
  async init() {
    this.setupEventListeners();
    
    // Check if user is already logged in
    if (apiManager.isAuthenticated()) {
      await this.loadDashboard();
    } else {
      this.showLoginForm();
    }
  }

  /**
   * Setup all event listeners
   */
  setupEventListeners() {
    // Auth events
    document.getElementById('loginBtn').addEventListener('click', () => this.handleLogin());
    document.getElementById('registerBtn').addEventListener('click', () => this.toggleRegisterForm());
    document.getElementById('submitRegisterBtn').addEventListener('click', () => this.handleRegister());
    document.getElementById('loginUsernameInput').addEventListener('keypress', (e) => {
      if (e.key === 'Enter') this.handleLogin();
    });

    // Habit events
    document.getElementById('addHabitBtn').addEventListener('click', () => this.showAddHabitForm());
    document.getElementById('submitHabitBtn').addEventListener('click', () => this.handleAddHabit());
    document.getElementById('cancelHabitBtn').addEventListener('click', () => this.hideAddHabitForm());

    // Navigation events
    document.getElementById('logoutBtn').addEventListener('click', () => this.handleLogout());
    document.getElementById('backToDashboardBtn').addEventListener('click', () => this.showDashboard());

    // Habit input Enter key
    document.getElementById('habitNameInput').addEventListener('keypress', (e) => {
      if (e.key === 'Enter') this.handleAddHabit();
    });
  }

  /**
   * Show login form
   */
  showLoginForm() {
    document.getElementById('loginContainer').style.display = 'block';
    document.getElementById('dashboardContainer').style.display = 'none';
    document.getElementById('habitDetailContainer').style.display = 'none';
    document.getElementById('loginUsernameInput').focus();
  }

  /**
   * Toggle register form visibility
   */
  toggleRegisterForm() {
    const registerForm = document.getElementById('registerForm');
    registerForm.style.display = registerForm.style.display === 'none' ? 'block' : 'none';
    
    if (registerForm.style.display === 'block') {
      document.getElementById('registerUsernameInput').focus();
    }
  }

  /**
   * Handle user login
   */
  async handleLogin() {
    const username = document.getElementById('loginUsernameInput').value.trim();
    const password = document.getElementById('loginPasswordInput').value.trim();

    if (!username || !password) {
      this.showMessage('error', 'Please enter username and password');
      return;
    }

    try {
      this.showMessage('info', 'Logging in...');
      await apiManager.login(username, password);
      this.showMessage('success', 'Logged in successfully!');
      
      // Clear form
      document.getElementById('loginUsernameInput').value = '';
      document.getElementById('loginPasswordInput').value = '';
      
      setTimeout(() => this.loadDashboard(), 500);
    } catch (error) {
      this.showMessage('error', 'Login failed: ' + (error.message || 'Unknown error'));
      console.error(error);
    }
  }

  /**
   * Handle user registration
   */
  async handleRegister() {
    const username = document.getElementById('registerUsernameInput').value.trim();
    const password = document.getElementById('registerPasswordInput').value.trim();
    const email = document.getElementById('registerEmailInput').value.trim();

    if (!username || !password) {
      this.showMessage('error', 'Please enter username and password');
      return;
    }

    try {
      this.showMessage('info', 'Registering...');
      await apiManager.register(username, password, email);
      this.showMessage('success', 'Account created! Logging in...');
      
      // Clear form
      document.getElementById('registerUsernameInput').value = '';
      document.getElementById('registerEmailInput').value = '';
      document.getElementById('registerPasswordInput').value = '';
      document.getElementById('registerForm').style.display = 'none';
      
      setTimeout(() => this.loadDashboard(), 500);
    } catch (error) {
      this.showMessage('error', 'Registration failed: ' + (error.message || 'Unknown error'));
      console.error(error);
    }
  }

  /**
   * Load and display dashboard with all habits
   */
  async loadDashboard() {
    try {
      console.log('📊 Loading dashboard...');
      console.log('👤 Current user:', apiManager.currentUser);
      
      this.showMessage('info', 'Loading habits...');
      
      // Fetch habits from backend
      const habits = await apiManager.getAllHabits();
      console.log('🎯 Habits loaded:', habits);
      
      this.showMessage('success', `Loaded ${habits.length} habits!`);
      this.showDashboard();
    } catch (error) {
      console.error('❌ Dashboard load error:', error);
      this.showMessage('error', 'Failed to load habits: ' + error.message);
    }
  }

  /**
   * Display dashboard UI
   */
  showDashboard() {
    document.getElementById('loginContainer').style.display = 'none';
    document.getElementById('dashboardContainer').style.display = 'block';
    document.getElementById('habitDetailContainer').style.display = 'none';
    document.getElementById('addHabitFormContainer').style.display = 'none';

    // Update user info
    if (apiManager.currentUser) {
      document.getElementById('userNameDisplay').textContent = 
        apiManager.currentUser.username || apiManager.currentUser.email || 'User';
    }

    // Display habits
    this.displayHabits();
  }

  /**
   * Display all habits in the list
   */
  displayHabits() {
    const habitsList = document.getElementById('habitsList');
    
    if (!apiManager.habits || apiManager.habits.length === 0) {
      habitsList.innerHTML = '<div class="empty-state"><p>No habits yet. Create one to get started!</p></div>';
      return;
    }

    habitsList.innerHTML = apiManager.habits.map(habit => `
      <div class="habit-card" data-habit-id="${habit.id || habit.habit_id}">
        <div class="habit-header">
          <h3>${habit.title || 'Unnamed Habit'}</h3>
        </div>
        <p class="habit-description">${habit.description || 'No description'}</p>
        <div class="habit-meta">
          <span class="habit-streak">Streak: ${habit.streakCount || 0} days</span>
        </div>
        <div class="habit-actions">
          <button class="btn btn-sm btn-primary view-btn" onclick="app.viewHabitDetails('${habit.id || habit.habit_id}')">View</button>
          <button class="btn btn-sm btn-danger delete-btn" onclick="app.handleDeleteHabit('${habit.id || habit.habit_id}')">Delete</button>
        </div>
      </div>
    `).join('');
  }

  /**
   * Show add habit form
   */
  showAddHabitForm() {
    document.getElementById('addHabitFormContainer').style.display = 'block';
    document.getElementById('habitNameInput').focus();
  }

  /**
   * Hide add habit form
   */
  hideAddHabitForm() {
    document.getElementById('addHabitFormContainer').style.display = 'none';
    document.getElementById('habitNameInput').value = '';
    document.getElementById('habitDescInput').value = '';
  }

  /**
   * Handle adding a new habit
   */
  async handleAddHabit() {
    const title = document.getElementById('habitNameInput').value.trim();
    const description = document.getElementById('habitDescInput').value.trim();

    if (!title) {
      this.showMessage('error', 'Please enter a habit title');
      return;
    }

    try {
      this.showMessage('info', 'Adding habit...');
      await apiManager.addHabit(title, description);
      this.showMessage('success', 'Habit added successfully!');
      this.hideAddHabitForm();
      await this.loadDashboard();
    } catch (error) {
      this.showMessage('error', 'Failed to add habit: ' + error.message);
      console.error(error);
    }
  }

  /**
   * View details of a specific habit
   */
  async viewHabitDetails(habitId) {
    try {
      const habit = apiManager.habits.find(h => h.id === habitId || h.habit_id === habitId);
      if (!habit) {
        this.showMessage('error', 'Habit not found');
        return;
      }

      this.currentEditingHabitId = habitId;

      // Load habit tasks
      this.showMessage('info', 'Loading tasks...');
      const tasks = await apiManager.getHabitTasks(habitId);
      this.showMessage('success', 'Tasks loaded!');

      // Display habit details view
      document.getElementById('dashboardContainer').style.display = 'none';
      document.getElementById('habitDetailContainer').style.display = 'block';

      document.getElementById('habitDetailName').textContent = habit.title || 'Unnamed Habit';
      document.getElementById('habitDetailDescription').textContent = habit.description || 'No description';
      document.getElementById('habitDetailStreak').textContent = habit.streakCount || 0;

      this.displayTasks(tasks);
    } catch (error) {
      this.showMessage('error', 'Failed to load habit details: ' + error.message);
      console.error(error);
    }
  }

  /**
   * Display tasks for current habit
   */
  displayTasks(tasks) {
    const tasksList = document.getElementById('tasksList');
    
    if (!tasks || tasks.length === 0) {
      tasksList.innerHTML = '<div class="empty-state"><p>No tasks yet. Add one to get started!</p></div>';
      return;
    }

    tasksList.innerHTML = tasks.map(task => `
      <div class="task-item ${task.is_completed ? 'completed' : ''}">
        <div class="task-content">
          <input type="checkbox" class="task-checkbox" ${task.is_completed ? 'checked' : ''} 
                 onchange="app.handleToggleTask('${task.id || task.task_id}')">
          <div class="task-text">
            <span class="task-title">${task.title || 'Unnamed Task'}</span>
            ${task.description ? `<p class="task-description">${task.description}</p>` : ''}
          </div>
        </div>
        <button class="btn btn-sm btn-danger" onclick="app.handleDeleteTask('${task.id || task.task_id}')">Delete</button>
      </div>
    `).join('');
  }

  /**
   * Handle adding a task to the current habit
   */
  async handleAddTask() {
    const taskTitle = document.getElementById('taskTitleInput').value.trim();
    
    if (!taskTitle) {
      this.showMessage('error', 'Please enter a task title');
      return;
    }

    if (!this.currentEditingHabitId) {
      this.showMessage('error', 'No habit selected');
      return;
    }

    try {
      this.showMessage('info', 'Adding task...');
      const description = document.getElementById('taskDescInput').value.trim();
      await apiManager.addTask(this.currentEditingHabitId, taskTitle, description);
      this.showMessage('success', 'Task added!');
      
      document.getElementById('taskTitleInput').value = '';
      document.getElementById('taskDescInput').value = '';
      
      // Reload all tasks for the user
      await apiManager.getAllTasks();
      const habitTasks = apiManager.tasks.filter(t => t.habit_id === this.currentEditingHabitId);
      this.displayTasks(habitTasks);
    } catch (error) {
      this.showMessage('error', 'Failed to add task: ' + error.message);
      console.error(error);
    }
  }

  /**
   * Handle toggling task completion
   */
  async handleToggleTask(taskId) {
    try {
      this.showMessage('info', 'Updating task...');
      await apiManager.toggleTask(taskId);
      this.showMessage('success', 'Task updated!');
      
      // Reload all tasks for user
      await apiManager.getAllTasks();
      const habitTasks = apiManager.tasks.filter(t => t.habit_id === this.currentEditingHabitId);
      this.displayTasks(habitTasks);
    } catch (error) {
      this.showMessage('error', 'Failed to update task: ' + error.message);
      console.error(error);
    }
  }

  /**
   * Handle deleting a habit
   */
  async handleDeleteHabit(habitId) {
    if (!confirm('Are you sure you want to delete this habit?')) {
      return;
    }

    try {
      this.showMessage('info', 'Deleting habit...');
      await apiManager.deleteHabit(habitId);
      this.showMessage('success', 'Habit deleted!');
      this.displayHabits();
    } catch (error) {
      this.showMessage('error', 'Failed to delete habit: ' + error.message);
      console.error(error);
    }
  }

  /**
   * Handle deleting a task
   */
  async handleDeleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
      return;
    }

    try {
      this.showMessage('info', 'Deleting task...');
      await apiManager.deleteTask(taskId);
      this.showMessage('success', 'Task deleted!');
      
      // Reload all tasks for user
      await apiManager.getAllTasks();
      const habitTasks = apiManager.tasks.filter(t => t.habit_id === this.currentEditingHabitId);
      this.displayTasks(habitTasks);
    } catch (error) {
      this.showMessage('error', 'Failed to delete task: ' + error.message);
      console.error(error);
    }
  }

  /**
   * Handle logout
   */
  handleLogout() {
    if (!confirm('Are you sure you want to logout?')) {
      return;
    }

    apiManager.logout();
    this.showMessage('success', 'Logged out successfully');
    this.showLoginForm();
  }

  /**
   * Show message notification
   */
  showMessage(type, message) {
    const messageDiv = document.getElementById('messageContainer');
    messageDiv.textContent = message;
    messageDiv.className = `message message-${type}`;
    messageDiv.style.display = 'block';

    // Auto-hide success messages
    if (type === 'success') {
      setTimeout(() => {
        messageDiv.style.display = 'none';
      }, 3000);
    }
  }
}

// Initialize app when DOM is ready
let app;
document.addEventListener('DOMContentLoaded', () => {
  app = new HabitTrackerApp();
});

import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:home_widget/home_widget.dart'; // <--- Add this for MAIN ANDROID WIDGET SUPPORT
import '../widgets/widget_renderer.dart';

// Ensure these two files exist in your lib/models folder!
import '../models/habit.dart';
import '../models/task.dart'; 

class HabitProvider extends ChangeNotifier {
  
  // ==========================
  // SECTION 1: HABITS
  // ==========================
  List<Habit> _habits = [];
  
  // Getter: This allows screens to read the list
  List<Habit> get habits => _habits;

  // Add a new habit with a Category
  void addHabit(String title, String category) {
    final newHabit = Habit(
      id: DateTime.now().toString(),
      title: title,
      dateCreated: DateTime.now(),
      completedDays: [],
      category: category,
    );
    _habits.add(newHabit);
    _saveHabitsToStorage();
    notifyListeners();

    // Update the Android widget after adding a habit
    _updateAndroidWidget();
  }

  // Toggle a habit as Done/Not Done for a specific date
  void toggleHabit(String id, DateTime date) {
    final index = _habits.indexWhere((habit) => habit.id == id);
    if (index != -1) {
      String dateStr = date.toIso8601String().split('T')[0];
      List<String> updatedDays = List.from(_habits[index].completedDays);

      if (updatedDays.contains(dateStr)) {
        updatedDays.remove(dateStr);
      } else {
        updatedDays.add(dateStr);
      }

      // Update the habit in the list
      _habits[index] = Habit(
        id: _habits[index].id,
        title: _habits[index].title,
        dateCreated: _habits[index].dateCreated,
        completedDays: updatedDays,
        category: _habits[index].category,
      );

      _saveHabitsToStorage();
      notifyListeners();
    }

    // Update the Android widget after adding a habit
    _updateAndroidWidget();
    
  }

  // Delete a habit
  void deleteHabit(String id) {
    _habits.removeWhere((habit) => habit.id == id);
    _saveHabitsToStorage();
    notifyListeners();

    // Update the Android widget after adding a habit
    _updateAndroidWidget();
  }
  
  // ==========================
  // SECTION 2: TASKS (To-Do List)
  // ==========================
  List<Task> _tasks = [];
  
  // Getter
  List<Task> get tasks {
    final now = DateTime.now();
    return _tasks.where((task) {
      // Rule 1: Always show incomplete tasks (Carry them over)
      if (!task.isCompleted) return true;

      // Rule 2: If completed, only show if it was completed TODAY
      if (task.completedAt == null) return false; 
      
      final doneDate = task.completedAt!;
      return doneDate.year == now.year && 
             doneDate.month == now.month && 
             doneDate.day == now.day;
    }).toList();
  }
    
  // Add a new Task with Priority
  // UPDATED: Now requires a category!
  void addTask(String title, String priority, String category) {
    final newTask = Task(
      id: DateTime.now().toString(),
      title: title,
      priority: priority,
      category: category, // Save the chosen category
    );
    _tasks.add(newTask);
    _saveTasksToStorage();
    notifyListeners();

    // Update the Android widget after adding a habit
    _updateAndroidWidget();
  }
  
  // Toggle Task Completion
  void toggleTask(String id) {
    final index = _tasks.indexWhere((t) => t.id == id);
    if (index != -1) {
      final oldTask = _tasks[index];
      final isNowCompleted = !oldTask.isCompleted;

      // Create new version
      _tasks[index] = oldTask.copyWith(
        isCompleted: isNowCompleted,
        // If we just finished it, mark the time. If we unchecked it, clear the time.
        completedAt: isNowCompleted ? DateTime.now() : null, 
      );

      _saveTasksToStorage();
      notifyListeners();
      _updateAndroidWidget();
    }
  }

  // Delete a Task
  void deleteTask(String id) {
    _tasks.removeWhere((t) => t.id == id);
    _saveTasksToStorage();
    notifyListeners();

    // Update the Android widget after adding a habit
    _updateAndroidWidget();
  }
  void _cleanupOldTasks() {
    final now = DateTime.now();
    // Remove tasks that are completed AND NOT from today
    _tasks.removeWhere((task) {
      if (!task.isCompleted) return false;
      if (task.completedAt == null) return true; // Clean up broken data
      
      final doneDate = task.completedAt!;
      final isToday = doneDate.year == now.year && 
                      doneDate.month == now.month && 
                      doneDate.day == now.day;
      
      return !isToday; // If NOT today, Remove it.
    });
    _saveTasksToStorage();
  }
  // ==========================
  // SECTION 3: DATABASE (Storage)
  // ==========================
  
  // --- Saving Functions ---
  Future<void> _saveHabitsToStorage() async {
    final prefs = await SharedPreferences.getInstance();
    List<String> encodedList = _habits.map((habit) => jsonEncode(habit.toMap())).toList();
    await prefs.setStringList('habit_list', encodedList);
  }

  Future<void> _saveTasksToStorage() async {
    final prefs = await SharedPreferences.getInstance();
    List<String> encoded = _tasks.map((t) => jsonEncode(t.toMap())).toList();
    await prefs.setStringList('task_list', encoded);
  }

  // --- Loading Functions ---
  Future<void> loadHabits() async {
    final prefs = await SharedPreferences.getInstance();
    List<String>? encodedList = prefs.getStringList('habit_list');
    if (encodedList != null) {
      _habits = encodedList.map((item) => Habit.fromMap(jsonDecode(item))).toList();
      notifyListeners();
    }
  }

  Future<void> loadTasks() async {
    final prefs = await SharedPreferences.getInstance();
    List<String>? encoded = prefs.getStringList('task_list');
    if (encoded != null) {
      _tasks = encoded.map((item) => Task.fromMap(jsonDecode(item))).toList();
      notifyListeners();
    }
  }

  // --- MASTER LOADER (Call this in initState) ---
  Future<void> loadAllData() async {
    await loadHabits();
    await loadTasks();
  }

  // --- WIDGET MESSENGER ---
  
  // inside HabitProvider.dart

  Future<void> _updateAndroidWidget() async {

    if (!Platform.isAndroid) return;
    try {
    // 1. Get Data
    int done = _habits.where((h) => h.isCompleted(DateTime.now())).length;
    int total = _habits.length;

    // 2. Render View 1: The Graph and plot the skit
    await HomeWidget.renderFlutterWidget(
      WidgetChartWrapper(child: HabitsGraphWidget(habitsDone: done, totalHabits: total)),
      key: 'snapshot_graph', 
      logicalSize: const Size(350, 250),
    );

    // 3. Render View 2: The Calendar
    await HomeWidget.renderFlutterWidget(
      WidgetChartWrapper(child: CalendarGridWidget()),
      key: 'snapshot_calendar', 
      logicalSize: const Size(350, 250),
    );

    // 4. Update the Widget text and tell it to refresh
    await HomeWidget.saveWidgetData('view_title', 'Analytics');
    await HomeWidget.updateWidget(
      name: 'HomeWidgetProvider',
      androidName: 'HomeWidgetProvider',
    );
  } catch (e) {
      print('Error updating Android widget: $e');
    }
  }
}
 
    

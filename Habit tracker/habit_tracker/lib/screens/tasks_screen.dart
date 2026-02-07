import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/habit_provider.dart';
import '../models/task.dart';
import '../utils/habit_categories.dart'; // Import this to get categories

class TasksScreen extends StatefulWidget {
  const TasksScreen({super.key});

  @override
  State<TasksScreen> createState() => _TasksScreenState();
}

class _TasksScreenState extends State<TasksScreen> {
  
  @override
  void initState() {
    super.initState();
    Provider.of<HabitProvider>(context, listen: false).loadTasks();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF121212),
      appBar: AppBar(
        title: const Text("DAILY TASKS", style: TextStyle(letterSpacing: 1.5, fontWeight: FontWeight.bold)),
        centerTitle: true,
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      // 1. UPGRADE TO CUSTOM SCROLL VIEW (SLIVERS)
      body: Consumer<HabitProvider>(
        builder: (context, provider, child) {
          if (provider.tasks.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.check_circle_outline, size: 80, color: Colors.grey[800]),
                  const SizedBox(height: 10),
                  const Text("No tasks for today!", style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }

          return CustomScrollView(
            slivers: [
              SliverPadding(
                padding: const EdgeInsets.symmetric(vertical: 10),
                sliver: SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      final Task task = provider.tasks[index];
                      return _buildTaskCard(task, provider);
                    },
                    childCount: provider.tasks.length,
                  ),
                ),
              ),
            ],
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.indigoAccent,
        onPressed: () => _showAddTaskDialog(context),
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }

  Widget _buildTaskCard(Task task, HabitProvider provider) {
    Color priorityColor;
    switch (task.priority) {
      case 'High': priorityColor = Colors.redAccent; break;
      case 'Medium': priorityColor = Colors.orangeAccent; break;
      default: priorityColor = Colors.greenAccent;
    }

    // Get category color/icon safely
    IconData catIcon = HabitCategories.getIcon(task.category);
    Color catColor = HabitCategories.getColor(task.category);

    return Card(
      color: const Color(0xFF1E1E1E),
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: priorityColor.withOpacity(0.3)),
      ),
      child: ListTile(
        leading: Transform.scale(
          scale: 1.2,
          child: Checkbox(
            value: task.isCompleted,
            activeColor: priorityColor,
            shape: const CircleBorder(),
            side: BorderSide(color: priorityColor),
            onChanged: (val) => provider.toggleTask(task.id),
          ),
        ),
        title: Text(
          task.title,
          style: TextStyle(
            decoration: task.isCompleted ? TextDecoration.lineThrough : null,
            color: task.isCompleted ? Colors.grey : Colors.white,
          ),
        ),
        // Improved Subtitle with Category
        subtitle: Row(
          children: [
            Icon(catIcon, size: 12, color: Colors.grey),
            const SizedBox(width: 4),
            Text(
              "${task.priority} • ${task.category.isEmpty ? 'General' : task.category}",
              style: TextStyle(color: priorityColor, fontSize: 12),
            ),
          ],
        ),
        trailing: IconButton(
          icon: const Icon(Icons.delete_outline, color: Colors.grey),
          onPressed: () => provider.deleteTask(task.id),
        ),
      ),
    );
  }

  void _showAddTaskDialog(BuildContext context) {
    final TextEditingController controller = TextEditingController();
    String selectedPriority = 'Medium';
    String selectedCategory = 'Work'; // Default category

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setDialogState) {
          return AlertDialog(
            backgroundColor: const Color(0xFF1E1E1E),
            title: const Text("New Task"),
            scrollable: true, // Prevents keyboard overflow
            content: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextField(
                  controller: controller,
                  decoration: const InputDecoration(
                    hintText: "What needs to be done?",
                    hintStyle: TextStyle(color: Colors.grey),
                    focusedBorder: UnderlineInputBorder(borderSide: BorderSide(color: Colors.indigoAccent)),
                  ),
                  style: const TextStyle(color: Colors.white),
                  autofocus: true,
                ),
                const SizedBox(height: 20),
                
                // PRIORITY SELECTOR
                const Text("Priority", style: TextStyle(color: Colors.grey, fontSize: 12)),
                const SizedBox(height: 10),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: ['Low', 'Medium', 'High'].map((priority) {
                    final isSelected = selectedPriority == priority;
                    Color color = priority == 'High' ? Colors.redAccent : (priority == 'Medium' ? Colors.orangeAccent : Colors.greenAccent);

                    return ChoiceChip(
                      label: Text(priority),
                      selected: isSelected,
                      selectedColor: color,
                      backgroundColor: Colors.grey[800],
                      labelStyle: TextStyle(color: isSelected ? Colors.black : Colors.white),
                      onSelected: (selected) {
                        if (selected) setDialogState(() => selectedPriority = priority);
                      },
                    );
                  }).toList(),
                ),

                const SizedBox(height: 20),

                // 2. CATEGORY SELECTOR (ADDED MISSING FEATURE)
                const Text("Category", style: TextStyle(color: Colors.grey, fontSize: 12)),
                const SizedBox(height: 10),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: HabitCategories.list.map((category) {
                    final isSelected = selectedCategory == category;
                    return ChoiceChip(
                      avatar: Icon(HabitCategories.getIcon(category), color: isSelected ? Colors.black : HabitCategories.getColor(category), size: 16),
                      label: Text(category),
                      selected: isSelected,
                      selectedColor: HabitCategories.getColor(category),
                      backgroundColor: Colors.grey[800],
                      labelStyle: TextStyle(color: isSelected ? Colors.black : Colors.white),
                      onSelected: (selected) { if (selected) setDialogState(() => selectedCategory = category); },
                    );
                  }).toList(),
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text("Cancel", style: TextStyle(color: Colors.grey)),
              ),
              TextButton(
                onPressed: () {
                  if (controller.text.isNotEmpty) {
                    Provider.of<HabitProvider>(context, listen: false).addTask(
                          controller.text, 
                          selectedPriority,
                          selectedCategory
                        );
                    Navigator.pop(context);
                  }
                },
                child: const Text("Add", style: TextStyle(color: Colors.indigoAccent)),
              ),
            ],
          );
        },
      ),
    );
  }
}
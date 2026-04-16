import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../utils/habit_categories.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  // We now fetch a List of Lists! [[Habits], [Tasks]]
  late Future<List<List<dynamic>>> _dataFuture;
  
  final int userId = 5; 
  String _selectedFilter = 'All';

  @override
  void initState() {
    super.initState();
    _refreshData();
  }

  // FIXED: Fetch BOTH Habits and Tasks together
  void _refreshData() {
    setState(() {
      _dataFuture = Future.wait([
        ApiService.getHabits(userId), // Index 0
        ApiService.getTasks(userId),  // Index 1
      ]);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF121212),
      appBar: AppBar(
        title: const Text("COMMAND CENTER", style: TextStyle(fontWeight: FontWeight.bold, letterSpacing: 1.2)),
        centerTitle: true,
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      
      body: FutureBuilder<List<List<dynamic>>>(
        future: _dataFuture,
        builder: (context, snapshot) {
          
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator(color: Colors.indigoAccent));
          }
          
          if (snapshot.hasError) {
            return Center(child: Text("Error: ${snapshot.error}", style: const TextStyle(color: Colors.white)));
          }

          if (snapshot.hasData) {
            // UNPACK THE DATA
            final rawHabits = snapshot.data![0];
            final rawTasks = snapshot.data![1];

            // Filter Habits
            final visibleHabits = _selectedFilter == 'All'
                ? rawHabits
                : rawHabits.where((h) => "Health" == _selectedFilter).toList(); 

            return CustomScrollView(
              slivers: [
                // --- SECTION 1: HABITS ---
                SliverToBoxAdapter(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Padding(padding: const EdgeInsets.all(20), child: Text("Connected to User: $userId", style: const TextStyle(color: Colors.greenAccent))),
                      _buildSectionHeader("MY HABITS"),
                      _buildCategoryFilter(),
                      const SizedBox(height: 10),
                    ],
                  ),
                ),

                if (visibleHabits.isEmpty)
                   SliverToBoxAdapter(child: Padding(padding: const EdgeInsets.all(20.0), child: Text("No habits found.", style: TextStyle(color: Colors.grey[600]))))
                else
                  SliverList(
                    delegate: SliverChildBuilderDelegate(
                      (context, index) => _buildBackendHabitCard(visibleHabits[index]),
                      childCount: visibleHabits.length,
                    ),
                  ),

                // --- SECTION 2: TASKS ---
                const SliverToBoxAdapter(child: SizedBox(height: 30)),
                SliverToBoxAdapter(child: _buildSectionHeader("PRIORITY TASKS")),
                
                if (rawTasks.isEmpty)
                   SliverToBoxAdapter(child: Padding(padding: const EdgeInsets.all(20.0), child: Text("No tasks pending.", style: TextStyle(color: Colors.grey[600]))))
                else
                  SliverList(
                    delegate: SliverChildBuilderDelegate(
                      (context, index) => _buildBackendTaskCard(rawTasks[index]),
                      childCount: rawTasks.length,
                    ),
                  ),

                const SliverToBoxAdapter(child: SizedBox(height: 100)),
              ],
            );
          }
          return const Center(child: Text("Unexpected State"));
        },
      ),
      
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showMultiAddDialog(context),
        backgroundColor: Colors.indigoAccent,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }

  // --- HABIT CARD ---
  Widget _buildBackendHabitCard(dynamic habitJson) {
    final String title = habitJson['title'] ?? "Unknown";
    final String description = habitJson['description'] ?? "No desc";
    final int streak = habitJson['streakCount'] ?? 0;
    
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: Card(
        color: const Color(0xFF1E1E1E),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        child: ListTile(
          leading: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(color: Colors.indigoAccent.withOpacity(0.15), borderRadius: BorderRadius.circular(10)),
            child: const Icon(Icons.loop, color: Colors.indigoAccent, size: 24),
          ),
          title: Text(title, style: const TextStyle(color: Colors.white)),
          subtitle: Text(description, style: TextStyle(color: Colors.grey[600], fontSize: 12)),
          trailing: Text("🔥 $streak", style: const TextStyle(color: Colors.orange)),
        ),
      ),
    );
  }

  // --- TASK CARD ---
  Widget _buildBackendTaskCard(dynamic taskJson) {
    final String title = taskJson['title'] ?? "Unknown Task";
    final String priority = taskJson['priority'] ?? "Medium";
    final String category = taskJson['category'] ?? "Work";
    final bool isDone = taskJson['completed'] ?? taskJson['isCompleted'] ?? false;

    Color priorityColor = priority == 'High' ? Colors.redAccent : (priority == 'Medium' ? Colors.orangeAccent : Colors.greenAccent);

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: Card(
        color: const Color(0xFF1E1E1E),
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
            side: BorderSide(color: priorityColor.withOpacity(0.5), width: 1)
        ),
        child: ListTile(
          leading: Icon(HabitCategories.getIcon(category), color: HabitCategories.getColor(category)),
          title: Text(title, style: TextStyle(color: isDone ? Colors.grey : Colors.white, decoration: isDone ? TextDecoration.lineThrough : null)),
          subtitle: Text("$priority Priority • $category", style: TextStyle(color: Colors.grey[500], fontSize: 11)),
          trailing: Checkbox(
            value: isDone,
            activeColor: priorityColor,
            onChanged: (val) {
               ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Task Toggle API coming next!")));
            },
          ),
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 5),
      child: Text(title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, letterSpacing: 1.0)),
    );
  }

  Widget _buildCategoryFilter() {
    List<String> filters = ['All', ...HabitCategories.list];
    return SizedBox(
      height: 50,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        itemCount: filters.length,
        itemBuilder: (context, index) {
          final category = filters[index];
          final isSelected = _selectedFilter == category;
          return Padding(
            padding: const EdgeInsets.only(right: 8.0),
            child: ChoiceChip(
              label: Text(category),
              selected: isSelected,
              selectedColor: category == 'All' ? Colors.white : HabitCategories.getColor(category),
              backgroundColor: const Color(0xFF1E1E1E),
              labelStyle: TextStyle(color: isSelected ? Colors.black : Colors.grey),
              onSelected: (selected) {
                if (selected) setState(() => _selectedFilter = category);
              },
            ),
          );
        },
      ),
    );
  }

  // --- DIALOGS (The Missing Part!) ---
  
  void _showMultiAddDialog(BuildContext context) {
    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF1E1E1E),
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(20),
          height: 200,
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              _buildAddOption("Habit", Icons.loop, Colors.indigoAccent, () {
                 Navigator.pop(context);
                 _showAddHabitDialog(context);
              }),
              _buildAddOption("Task", Icons.check_circle, Colors.orangeAccent, () {
                 Navigator.pop(context);
                 _showAddTaskDialog(context);
              }),
            ],
          ),
        );
      },
    );
  }
  
  Widget _buildAddOption(String label, IconData icon, Color color, VoidCallback onTap) {
    return InkWell(
      onTap: onTap,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, size: 40, color: color),
          const SizedBox(height: 10),
          Text(label, style: const TextStyle(color: Colors.white)),
        ],
      ),
    );
  }

  void _showAddHabitDialog(BuildContext context) {
    final TextEditingController controller = TextEditingController();
    showDialog(context: context, builder: (ctx) => AlertDialog(
      title: const Text("New Habit"),
      content: TextField(controller: controller, decoration: const InputDecoration(hintText: "Title")),
      actions: [
        TextButton(onPressed: () => Navigator.pop(ctx), child: const Text("Cancel")),
        TextButton(onPressed: () async {
          if (controller.text.isNotEmpty) {
             Navigator.pop(ctx);
             await ApiService.addHabit(userId, controller.text, "General");
             _refreshData(); // Refresh UI
          }
        }, child: const Text("Save")),
      ],
    ));
  }

  void _showAddTaskDialog(BuildContext context) {
    final TextEditingController controller = TextEditingController();
    String selectedPriority = 'Medium';
    String selectedCategory = 'Work';

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setDialogState) {
          return AlertDialog(
            backgroundColor: const Color(0xFF1E1E1E),
            title: const Text("New Task"),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(controller: controller, decoration: const InputDecoration(hintText: "Task Name"), style: const TextStyle(color: Colors.white)),
                const SizedBox(height: 10),
                DropdownButton<String>(
                  value: selectedPriority,
                  dropdownColor: Colors.grey[800],
                  items: ['Low', 'Medium', 'High'].map((p) => DropdownMenuItem(value: p, child: Text(p, style: const TextStyle(color: Colors.white)))).toList(),
                  onChanged: (val) => setDialogState(() => selectedPriority = val!),
                ),
                DropdownButton<String>(
                  value: selectedCategory,
                  dropdownColor: Colors.grey[800],
                  items: HabitCategories.list.map((c) => DropdownMenuItem(value: c, child: Text(c, style: const TextStyle(color: Colors.white)))).toList(),
                  onChanged: (val) => setDialogState(() => selectedCategory = val!),
                )
              ],
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
              TextButton(
                onPressed: () async {
                  if (controller.text.isNotEmpty) {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Saving Task...")));
                    try {
                      await ApiService.addTask(userId, controller.text, selectedPriority, selectedCategory);
                      _refreshData(); 
                    } catch (e) {
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Failed to save task")));
                    }
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
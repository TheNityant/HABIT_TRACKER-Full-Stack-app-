import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:table_calendar/table_calendar.dart';
import '../models/habit.dart';
import '../services/habit_provider.dart';
import '../utils/habit_categories.dart';

class HabitDetailsScreen extends StatefulWidget {
  final Habit habit;

  const HabitDetailsScreen({super.key, required this.habit});

  @override
  State<HabitDetailsScreen> createState() => _HabitDetailsScreenState();
}

class _HabitDetailsScreenState extends State<HabitDetailsScreen> {
  DateTime _focusedDay = DateTime.now();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF121212),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: Text(widget.habit.title.toUpperCase(), style: const TextStyle(letterSpacing: 1.2)),
        centerTitle: true,
      ),
      body: Consumer<HabitProvider>(
        builder: (context, provider, child) {
          // 1. Safety Check
          Habit currentHabit;
          try {
            currentHabit = provider.habits.firstWhere((h) => h.id == widget.habit.id);
          } catch (e) {
            return const Center(child: Text("Habit not found"));
          }

          final color = HabitCategories.getColor(currentHabit.category);
          final tasks = provider.tasks;
          
          // 2. Calculate Progress for the Widget
          int totalTasks = tasks.length;
          int completedTasks = tasks.where((t) => t.isCompleted).length;
          double progress = totalTasks == 0 ? 0 : completedTasks / totalTasks;

          return CustomScrollView(
            slivers: [
              SliverToBoxAdapter(
                child: Column(
                  children: [
                    const SizedBox(height: 20),
                    _buildHeader(currentHabit, color),
                    const SizedBox(height: 30),
                    
                    // CALENDAR SECTION
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20.0),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: Text("Monthly Progress", style: TextStyle(color: Colors.grey[400], fontSize: 14)),
                      ),
                    ),
                    const SizedBox(height: 10),
                    _buildHeatmapCalendar(currentHabit, color, provider),
                    const SizedBox(height: 30),

                    // --- NEW WIDGET: DAILY PROGRESS BAR ---
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text("Today's Tasks", style: TextStyle(color: Colors.grey[400], fontSize: 14)),
                          Text(
                            "$completedTasks of $totalTasks completed",
                            style: TextStyle(
                              color: completedTasks == totalTasks && totalTasks > 0 
                                  ? Colors.greenAccent 
                                  : Colors.white,
                              fontWeight: FontWeight.bold
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 10),
                    
                    // The Progress Bar Visual
                    if (totalTasks > 0)
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 20.0),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          child: LinearProgressIndicator(
                            value: progress,
                            minHeight: 8,
                            backgroundColor: const Color(0xFF1E1E1E),
                            valueColor: AlwaysStoppedAnimation<Color>(
                              completedTasks == totalTasks ? Colors.greenAccent : color
                            ),
                          ),
                        ),
                      ),
                    const SizedBox(height: 20),
                  ],
                ),
              ),

              // TASK LIST
              if (tasks.isEmpty)
                SliverToBoxAdapter(
                  child: Container(
                    margin: const EdgeInsets.symmetric(horizontal: 16),
                    padding: const EdgeInsets.all(20),
                    decoration: BoxDecoration(
                      color: const Color(0xFF1E1E1E),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Center(
                       child: Text("No tasks for today.", style: TextStyle(color: Colors.grey[600], fontStyle: FontStyle.italic)),
                    ),
                  ),
                )
              else
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      final task = tasks[index];
                      return Container(
                         margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 5),
                         child: Card(
                          color: const Color(0xFF1E1E1E),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          child: ListTile(
                            leading: Icon(
                              task.isCompleted ? Icons.check_circle : Icons.radio_button_unchecked,
                              color: task.isCompleted ? color : Colors.grey,
                            ),
                            title: Text(
                              task.title,
                              style: TextStyle(
                                color: task.isCompleted ? Colors.grey : Colors.white,
                                decoration: task.isCompleted ? TextDecoration.lineThrough : null,
                              ),
                            ),
                            subtitle: Text(task.priority, style: TextStyle(color: Colors.grey[600], fontSize: 12)),
                            onTap: () => provider.toggleTask(task.id),
                          ),
                        ),
                      );
                    },
                    childCount: tasks.length,
                  ),
                ),

              const SliverToBoxAdapter(child: SizedBox(height: 50)),
            ],
          );
        },
      ),
    );
  }

  // --- WIDGET HELPERS ---

  Widget _buildHeader(Habit habit, Color color) {
    return Container(
      padding: const EdgeInsets.all(20),
      margin: const EdgeInsets.symmetric(horizontal: 16),
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: color.withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(HabitCategories.getIcon(habit.category), color: color, size: 32),
          ),
          const SizedBox(width: 20),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(habit.category, style: TextStyle(color: Colors.grey[500], fontSize: 14)),
              const SizedBox(height: 5),
              Text(habit.category, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.white)),
            ],
          )
        ],
      ),
    );
  }

  Widget _buildHeatmapCalendar(Habit habit, Color color, HabitProvider provider) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      color: const Color(0xFF1E1E1E),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: TableCalendar(
          firstDay: DateTime.utc(2023, 1, 1),
          lastDay: DateTime.utc(2030, 12, 31),
          focusedDay: _focusedDay,
          calendarFormat: CalendarFormat.month,
          availableGestures: AvailableGestures.horizontalSwipe,
          headerStyle: const HeaderStyle(
            formatButtonVisible: false,
            titleCentered: true,
            titleTextStyle: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
            leftChevronIcon: Icon(Icons.chevron_left, color: Colors.white),
            rightChevronIcon: Icon(Icons.chevron_right, color: Colors.white),
          ),
          selectedDayPredicate: (day) => habit.isCompleted(day),
          calendarStyle: CalendarStyle(
            defaultTextStyle: const TextStyle(color: Colors.white),
            weekendTextStyle: TextStyle(color: Colors.grey[500]),
            outsideTextStyle: TextStyle(color: Colors.grey[800]),
            selectedDecoration: BoxDecoration(
              color: color,
              shape: BoxShape.circle,
            ),
            todayDecoration: BoxDecoration(
              color: color.withOpacity(0.3),
              shape: BoxShape.circle,
              border: Border.all(color: color),
            ),
          ),
          onDaySelected: (selectedDay, focusedDay) {
            provider.toggleHabit(habit.id, selectedDay);
            setState(() {
              _focusedDay = focusedDay;
            });
          },
        ),
      ),
    );
  }
}
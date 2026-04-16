import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:intl/intl.dart';
import '../services/habit_provider.dart';
import '../models/habit.dart';
import '../utils/habit_categories.dart';

class StatsScreen extends StatefulWidget {
  const StatsScreen({super.key});

  @override
  State<StatsScreen> createState() => _StatsScreenState();
}

class _StatsScreenState extends State<StatsScreen> {
  Habit? _selectedHabit;
  DateTime _focusedDay = DateTime.now();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF121212), // Deep Space Black
      appBar: AppBar(
        title: const Text("ANALYTICS", style: TextStyle(fontWeight: FontWeight.bold, letterSpacing: 2.0, fontSize: 16)),
        centerTitle: true,
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: Consumer<HabitProvider>(
        builder: (context, provider, child) {
          if (provider.habits.isEmpty) {
            return _buildEmptyState();
          }

          // Auto-select logic
          _selectedHabit ??= provider.habits.first;
          final currentHabit = provider.habits.firstWhere((h) => h.id == _selectedHabit!.id);
          final color = HabitCategories.getColor(currentHabit.category);

          return SingleChildScrollView(
            physics: const BouncingScrollPhysics(), // iOS style "rubber band" scroll
            child: Column(
              children: [
                const SizedBox(height: 10),

                // 1. THE FLOATING SELECTOR
                _buildHabitSelector(provider.habits, currentHabit),

                const SizedBox(height: 30),

                // 2. THE HERO GRAPH (Animated)
                _buildMonthlyProgress(currentHabit, color),

                const SizedBox(height: 30),

                // 3. THE "GRID OF GLORY" (New!)
                _buildStatsGrid(currentHabit, color),

                const SizedBox(height: 30),

                // 4. THE MODERN CALENDAR
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                  child: Align(alignment: Alignment.centerLeft, child: Text("HISTORY LOG", style: TextStyle(color: Colors.grey[500], fontSize: 12, letterSpacing: 1.5, fontWeight: FontWeight.bold))),
                ),
                _buildHeatmap(currentHabit, color),
                
                const SizedBox(height: 50), // Bottom padding
              ],
            ),
          );
        },
      ),
    );
  }

  // --- WIDGET BUILDERS ---

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.bar_chart_rounded, size: 80, color: Colors.grey[800]),
          const SizedBox(height: 20),
          Text("No Data Yet", style: TextStyle(color: Colors.grey[600], fontSize: 18)),
        ],
      ),
    );
  }

  Widget _buildHabitSelector(List<Habit> habits, Habit currentHabit) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 40),
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 5),
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E),
        borderRadius: BorderRadius.circular(30),
        border: Border.all(color: Colors.white.withOpacity(0.1)),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.5), blurRadius: 10, offset: const Offset(0, 4))],
      ),
      child: DropdownButtonHideUnderline(
        child: DropdownButton<String>(
          value: currentHabit.id,
          dropdownColor: const Color(0xFF1E1E1E),
          style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.w500),
          icon: const Icon(Icons.keyboard_arrow_down_rounded, color: Colors.grey),
          isExpanded: true,
          items: habits.map((Habit h) {
            return DropdownMenuItem<String>(
              value: h.id,
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(6),
                    decoration: BoxDecoration(color: HabitCategories.getColor(h.category).withOpacity(0.2), shape: BoxShape.circle),
                    child: Icon(HabitCategories.getIcon(h.category), color: HabitCategories.getColor(h.category), size: 14),
                  ),
                  const SizedBox(width: 12),
                  Text(h.title),
                ],
              ),
            );
          }).toList(),
          onChanged: (String? newId) {
            setState(() => _selectedHabit = habits.firstWhere((h) => h.id == newId));
          },
        ),
      ),
    );
  }

  Widget _buildMonthlyProgress(Habit habit, Color color) {
    int currentMonth = _focusedDay.month;
    int currentYear = _focusedDay.year;
    int completedInMonth = habit.completedDays.where((dateStr) {
      DateTime date = DateTime.parse(dateStr);
      return date.month == currentMonth && date.year == currentYear;
    }).length;
    int totalDaysInMonth = DateTime(currentYear, currentMonth + 1, 0).day;
    double targetProgress = totalDaysInMonth > 0 ? (completedInMonth / totalDaysInMonth) : 0.0;
    String monthName = DateFormat('MMMM').format(_focusedDay);

    return TweenAnimationBuilder<double>(
      key: ValueKey(habit.id),
      tween: Tween<double>(begin: 0.0, end: targetProgress),
      duration: const Duration(milliseconds: 1000), // 1 second smooth
      curve: Curves.easeOutQuart,
      builder: (context, value, _) {
        return Stack(
          alignment: Alignment.center,
          children: [
            // Outer Glow
            Container(
              height: 200, width: 200,
              decoration: BoxDecoration(shape: BoxShape.circle, boxShadow: [BoxShadow(color: color.withOpacity(0.15), blurRadius: 40, spreadRadius: 5)]),
            ),
            // Background Ring
            SizedBox(height: 180, width: 180, child: CircularProgressIndicator(value: 1.0, strokeWidth: 15, color: const Color(0xFF222222))),
            // Active Ring
            SizedBox(height: 180, width: 180, child: CircularProgressIndicator(value: value, strokeWidth: 15, color: color, strokeCap: StrokeCap.round)),
            // Text
            Column(
              children: [
                Text("${(value * totalDaysInMonth).toInt()}", style: const TextStyle(color: Colors.white, fontSize: 42, fontWeight: FontWeight.bold)),
                Text("/ $totalDaysInMonth days", style: TextStyle(color: Colors.grey[500], fontSize: 14)),
                const SizedBox(height: 4),
                Container(padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4), decoration: BoxDecoration(color: color.withOpacity(0.2), borderRadius: BorderRadius.circular(10)), child: Text(monthName.toUpperCase(), style: TextStyle(color: color, fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 1.0)))
              ],
            )
          ],
        );
      },
    );
  }

  // --- NEW FEATURE: THE GRID OF GLORY ---
  Widget _buildStatsGrid(Habit habit, Color color) {
    // Basic math for demo purposes (You can make this complex later)
    int totalDone = habit.completedDays.length;
    // Current Streak Logic (Simplified for now - just checks consecutive days backwards from today)
    int currentStreak = 0;
    DateTime checkDay = DateTime.now();
    while (habit.isCompleted(checkDay)) {
      currentStreak++;
      checkDay = checkDay.subtract(const Duration(days: 1));
    }
    
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GridView.count(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        crossAxisCount: 2,
        crossAxisSpacing: 15,
        mainAxisSpacing: 15,
        childAspectRatio: 1.5,
        children: [
          _buildStatCard("Current Streak", "$currentStreak Days", Icons.local_fire_department, Colors.orangeAccent),
          _buildStatCard("Total Completed", "$totalDone Times", Icons.check_circle, color),
          _buildStatCard("Best Streak", "${currentStreak > 5 ? currentStreak : 5} Days", Icons.emoji_events, Colors.yellowAccent), // Placeholder logic
          _buildStatCard("Completion Rate", "${((totalDone / 30) * 100).clamp(0, 100).toInt()}%", Icons.trending_up, Colors.greenAccent),
        ],
      ),
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, color: color, size: 28),
          const SizedBox(height: 10),
          Text(value, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
          Text(title, style: TextStyle(color: Colors.grey[600], fontSize: 12)),
        ],
      ),
    );
  }

  Widget _buildHeatmap(Habit habit, Color color) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E), 
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white.withOpacity(0.05)),
      ),
      child: TableCalendar(
        firstDay: DateTime.utc(2023, 1, 1),
        lastDay: DateTime.utc(2030, 12, 31),
        focusedDay: _focusedDay,
        calendarFormat: CalendarFormat.month,
        availableGestures: AvailableGestures.horizontalSwipe,
        headerStyle: const HeaderStyle(
          formatButtonVisible: false, titleCentered: true,
          titleTextStyle: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
          leftChevronIcon: Icon(Icons.chevron_left, color: Colors.grey),
          rightChevronIcon: Icon(Icons.chevron_right, color: Colors.grey),
        ),
        selectedDayPredicate: (day) => habit.isCompleted(day),
        calendarStyle: CalendarStyle(
          defaultTextStyle: const TextStyle(color: Colors.white),
          weekendTextStyle: TextStyle(color: Colors.grey[500]),
          outsideTextStyle: TextStyle(color: Colors.grey[800]),
          selectedDecoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(8), shape: BoxShape.rectangle),
          todayDecoration: BoxDecoration(color: Colors.transparent, border: Border.all(color: color, width: 2), borderRadius: BorderRadius.circular(8)),
          selectedTextStyle: const TextStyle(color: Colors.black, fontWeight: FontWeight.bold),
        ),
        onDaySelected: (selectedDay, focusedDay) => setState(() => _focusedDay = focusedDay),
      ),
    );
  }
}
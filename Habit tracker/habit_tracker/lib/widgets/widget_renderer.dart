import 'package:flutter/material.dart';

// 1. The Wrapper for our Widget Snapshots
class WidgetChartWrapper extends StatelessWidget {
  final Widget child;
  const WidgetChartWrapper({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    // We force a specific size for the snapshot to ensure it looks good
    return Container(
      width: 350, 
      height: 250, 
      color: const Color(0xFF1E1E1E), // Match widget background
      padding: const EdgeInsets.all(16),
      child: child,
    );
  }
}

// 2. View A: The Statistics Graph
class HabitsGraphWidget extends StatelessWidget {
  final int habitsDone;
  final int totalHabits;
  
  const HabitsGraphWidget({super.key, required this.habitsDone, required this.totalHabits});

  @override
  Widget build(BuildContext context) {
    // You can use a library like fl_chart here!
    // For now, I'll use a simple visual
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text("Today's Progress", style: TextStyle(color: Colors.white, fontSize: 18)),
        const SizedBox(height: 20),
        LinearProgressIndicator(
          value: totalHabits == 0 ? 0 : habitsDone / totalHabits,
          minHeight: 20,
          backgroundColor: Colors.grey[800],
          color: Colors.blueAccent,
        ),
        const SizedBox(height: 10),
        Text("$habitsDone / $totalHabits Completed", style: TextStyle(color: Colors.grey, fontSize: 14)),
      ],
    );
  }
}

// 3. View B: The Calendar Grid (Simplified)
class CalendarGridWidget extends StatelessWidget {
  const CalendarGridWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView.builder(
      physics: const NeverScrollableScrollPhysics(), // Important for snapshots
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 7),
      itemCount: 28,
      itemBuilder: (c, i) => Container(
        margin: const EdgeInsets.all(2),
        decoration: BoxDecoration(
          color: (i % 3 == 0) ? Colors.green : Colors.grey[800], // Fake data for demo
          shape: BoxShape.circle,
        ),
      ),
    );
  }
}
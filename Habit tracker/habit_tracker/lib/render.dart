import 'package:flutter/material.dart';

// 1. Wrapper (Kept mostly the same, but transparency safe)
class WidgetChartWrapper extends StatelessWidget {
  final Widget child;
  const WidgetChartWrapper({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 350, 
      height: 250, 
      // We use a transparent color here because the Android XML 
      // handles the background color now. This prevents double borders.
      color: Colors.transparent, 
      padding: const EdgeInsets.all(4), // Small padding for the content
      child: child,
    );
  }
}

// 2. View A: Premium Statistics Graph
class HabitsGraphWidget extends StatelessWidget {
  final int habitsDone;
  final int totalHabits;
  
  const HabitsGraphWidget({super.key, required this.habitsDone, required this.totalHabits});

  @override
  Widget build(BuildContext context) {
    final double percentage = totalHabits == 0 ? 0 : habitsDone / totalHabits;

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        // Ring Chart Stack
        Stack(
          alignment: Alignment.center,
          children: [
            SizedBox(
              width: 100,
              height: 100,
              child: CircularProgressIndicator(
                value: 1.0,
                strokeWidth: 10,
                color: Colors.grey.withOpacity(0.2), // Background ring
              ),
            ),
            SizedBox(
              width: 100,
              height: 100,
              child: CircularProgressIndicator(
                value: percentage,
                strokeWidth: 10,
                color: const Color(0xFF6C63FF), // Premium Purple
                strokeCap: StrokeCap.round,
              ),
            ),
            Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  "${(percentage * 100).toInt()}%",
                  style: const TextStyle(
                    color: Colors.white, 
                    fontSize: 22, 
                    fontWeight: FontWeight.bold
                  ),
                ),
              ],
            )
          ],
        ),
        const SizedBox(height: 20),
        // Text Info
        Text(
          "$habitsDone of $totalHabits Habits Done",
          style: const TextStyle(color: Colors.white70, fontSize: 16),
        ),
        const SizedBox(height: 8),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
            color: const Color(0xFF6C63FF).withOpacity(0.2),
            borderRadius: BorderRadius.circular(20),
          ),
          child: const Text(
            "Keep it up!", 
            style: TextStyle(color: Color(0xFF6C63FF), fontSize: 12, fontWeight: FontWeight.bold)
          ),
        )
      ],
    );
  }
}

// 3. View B: GitHub-Style Heatmap Calendar
class CalendarGridWidget extends StatelessWidget {
  const CalendarGridWidget({super.key});

  @override
  Widget build(BuildContext context) {
    // Weekday headers
    final days = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];

    return Column(
      children: [
        // Days of Week Row
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: days.map((d) => Text(d, style: const TextStyle(color: Colors.white38, fontSize: 12))).toList(),
        ),
        const SizedBox(height: 8),
        // Grid
        Expanded(
          child: GridView.builder(
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 7,
              mainAxisSpacing: 8,
              crossAxisSpacing: 8,
            ),
            itemCount: 28,
            itemBuilder: (c, i) {
              // Simulating "Intensity" of habits done
              // 0 = none, 1 = some, 2 = all
              final intensity = (i % 4); 
              Color color;
              if (intensity == 0) {
                color = Colors.grey[850]!;
              } else if (intensity == 1) color = Colors.green.withOpacity(0.4);
              else if (intensity == 2) color = Colors.green.withOpacity(0.7);
              else color = const Color(0xFF00E676); // Bright Green

              return Container(
                decoration: BoxDecoration(
                  color: color,
                  borderRadius: BorderRadius.circular(6), // Soft squares
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}
import 'package:flutter/material.dart';
import 'home_screen.dart';
import 'stats_screen.dart';
import 'journal_screen.dart'; 

class MainScaffold extends StatefulWidget {
  // 🟢 1. Accept the parameters from the Login Screen
  final int userId;
  final String username;

  const MainScaffold({
    super.key, 
    required this.userId, 
    required this.username
  });

  @override
  State<MainScaffold> createState() => _MainScaffoldState();
}

class _MainScaffoldState extends State<MainScaffold> {
  int _currentIndex = 0;

  // 🟢 2. Use a 'getter' to dynamically build the pages so we can pass the user data
  List<Widget> get _pages => [
        HomeScreen(userId: widget.userId, username: widget.username), // Passes data!
        const JournalScreen(), 
        const StatsScreen(),
      ];

  @override
  Widget build(BuildContext context) {
    final isDesktop = MediaQuery.of(context).size.width > 800;

    if (isDesktop) {
      return Scaffold(
        body: Row(
          children: [
            NavigationRail(
              backgroundColor: const Color.fromARGB(255, 1, 1, 1),
              selectedIndex: _currentIndex,
              onDestinationSelected: (int index) => setState(() => _currentIndex = index),
              destinations: const [
                NavigationRailDestination(icon: Icon(Icons.dashboard_outlined), selectedIcon: Icon(Icons.dashboard), label: Text('Home')),
                NavigationRailDestination(icon: Icon(Icons.auto_stories_outlined), selectedIcon: Icon(Icons.auto_stories), label: Text('Journal')),
                NavigationRailDestination(icon: Icon(Icons.pie_chart_outline), selectedIcon: Icon(Icons.pie_chart), label: Text('Analytics')),
              ],
            ),
            const VerticalDivider(thickness: 1, width: 1, color: Colors.white10),
            Expanded(child: _pages[_currentIndex]),
          ],
        ),
      );
    } else {
      return Scaffold(
        body: IndexedStack(index: _currentIndex, children: _pages),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _currentIndex,
          onDestinationSelected: (int index) => setState(() => _currentIndex = index),
          backgroundColor: const Color(0xFF1E1E1E),
          indicatorColor: Colors.indigoAccent,
          destinations: const [
            NavigationDestination(icon: Icon(Icons.dashboard_outlined), selectedIcon: Icon(Icons.dashboard), label: 'Home'),
            NavigationDestination(icon: Icon(Icons.auto_stories_outlined), selectedIcon: Icon(Icons.auto_stories), label: 'Journal'),
            // 🟢 3. Removed the extra 'Health' icon that would have crashed the app
            NavigationDestination(icon: Icon(Icons.pie_chart_outline), selectedIcon: Icon(Icons.pie_chart), label: 'Analytics'),
          ],
        ),
      );
    }
  }
}
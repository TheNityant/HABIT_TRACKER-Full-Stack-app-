import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart'; // 🟢 Import this
import 'package:habit_tracker/screens/login_screen.dart';
import 'package:habit_tracker/screens/main_scaffold.dart';
import 'package:flutter/foundation.dart'; // 🟢 Required for kDebugMode
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final prefs = await SharedPreferences.getInstance();
  
  // 🟢 SMART AMNESIA:
  // If you are debugging (pressing F5 or 'flutter run'), it wipes the vault.
  // If you build the actual app later, it ignores this block entirely.
  if (kDebugMode) {
    await prefs.clear();
  }

  final savedUserId = prefs.getInt('userId');
  final savedUsername = prefs.getString('username');

  runApp(MyApp(
    initialUserId: savedUserId,
    initialUsername: savedUsername,
  ));
}

class MyApp extends StatelessWidget {
  final int? initialUserId;
  final String? initialUsername;

  const MyApp({super.key, this.initialUserId, this.initialUsername});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Habit Tracker',
      theme: ThemeData(
        // ... keep whatever theme code you already had here ...
      ),
      // 🟢 4. The Magic Routing Logic
      // If we found an ID and Name, bypass login and go straight to MainScaffold!
      // Otherwise, go to the LoginScreen.
      home: (initialUserId != null && initialUsername != null)
          ? MainScaffold(userId: initialUserId!, username: initialUsername!)
          : const LoginScreen(),
    );
  }
}


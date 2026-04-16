import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

class ApiService {
  // 1. SMART ADDRESS
  static String get baseUrl {
  if (Platform.isAndroid) {
    // THIS IS THE CORRECT USB IP
    return "http://10.238.176.153:8080/api"; 
  } else {
    return "http://localhost:8080/api";
  }
}

  // --- HABITS (Keep this as is) ---
  static Future<List<dynamic>> getHabits(int userId) async {
    final url = Uri.parse('$baseUrl/habits/$userId');
    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception("Failed to load habits");
      }
    } catch (e) {
      print("Network Error (Habits): $e");
      return [];
    }
  }

  static Future<void> addHabit(int userId, String title, String description) async {
    final url = Uri.parse('$baseUrl/habits/$userId');
    final Map<String, dynamic> data = {
      "title": title,
      "description": description,
      "streakCount": 0
    };

    try {
      await http.post(
        url,
        headers: {"Content-Type": "application/json"},
        body: json.encode(data),
      );
    } catch (e) {
      print("Error adding habit: $e");
      rethrow;
    }
  }

  // --- NEW: TASKS SECTION ---

  // 1. GET TASKS
  static Future<List<dynamic>> getTasks(int userId) async {
    final url = Uri.parse('$baseUrl/tasks/$userId');
    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception("Failed to load tasks");
      }
    } catch (e) {
      print("Network Error (Tasks): $e");
      return [];
    }
  }

  // 2. ADD TASK
  static Future<void> addTask(int userId, String title, String priority, String category) async {
    final url = Uri.parse('$baseUrl/tasks/$userId');
    
    final Map<String, dynamic> data = {
      "title": title,
      "priority": priority,
      "category": category,
      "isCompleted": false // Default to false
    };

    try {
      await http.post(
        url,
        headers: {"Content-Type": "application/json"},
        body: json.encode(data),
      );
    } catch (e) {
      print("Error adding task: $e");
      rethrow;
    }
  }
}
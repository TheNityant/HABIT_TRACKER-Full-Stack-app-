import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

class ApiService {
  // 🟢 UPDATED: Points to your live Render backend
  static const String baseUrl = "https://habit-tracker-backend-o9bs.onrender.com/api";

  // ==========================================
  // 1. AUTHENTICATION (FIXES LOGINSCREEN ERRORS)
  // ==========================================
  
  static Future<Map<String, dynamic>?> login(String username, String password) async {
    final url = Uri.parse('$baseUrl/auth/login');
    try {
      final response = await http.post(
        url,
        headers: {"Content-Type": "application/json"},
        body: json.encode({"username": username, "password": password}),
      );

      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        print("Login Failed: ${response.statusCode}");
        return null; 
      }
    } catch (e) {
      print("Network Error: $e");
      return null;
    }
  }

  static Future<Map<String, dynamic>?> register(String username, String password) async {
    final url = Uri.parse('$baseUrl/auth/register');
    try {
      final response = await http.post(
        url,
        headers: {"Content-Type": "application/json"},
        body: json.encode({"username": username, "password": password}),
      );

      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        print("Registration Failed: ${response.body}");
        return null;
      }
    } catch (e) {
      print("Network Error: $e");
      return null;
    }
  }

  // ==========================================
  // 2. HABITS & TASKS
  // ==========================================

  static Future<List<dynamic>> getHabits(int userId) async {
    final url = Uri.parse('$baseUrl/habits/$userId');
    try {
      final response = await http.get(url);
      if (response.statusCode == 200) return json.decode(response.body);
      return [];
    } catch (e) {
      return [];
    }
  }

  static Future<List<dynamic>> getTasks(int userId) async {
    final url = Uri.parse('$baseUrl/tasks/$userId');
    try {
      final response = await http.get(url);
      if (response.statusCode == 200) return json.decode(response.body);
      return [];
    } catch (e) {
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


  static Future<void> addTask(int userId, String title, String priority, String category) async {
    final url = Uri.parse('$baseUrl/tasks/$userId');
    try {
      await http.post(
        url,
        headers: {"Content-Type": "application/json"},
        body: json.encode({
          "title": title,
          "priority": priority,
          "category": category,
          "completed": false
        }),
      );
    } catch (e) {
      rethrow;
    }
  }

  // 🔴 MISSING METHODS NEEDED BY PROVIDER
  static Future<bool> toggleTask(int taskId) async {
    final url = Uri.parse('$baseUrl/tasks/$taskId/toggle');
    try {
      final response = await http.put(url);
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  static Future<bool> deleteTask(int taskId) async {
    final url = Uri.parse('$baseUrl/tasks/$taskId');
    try {
      final response = await http.delete(url);
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  static Future<bool> checkInHabit(int habitId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/habits/$habitId/checkin'), // Adjust to match your exact Spring Boot endpoint
        headers: {'Content-Type': 'application/json'},
      );
      return response.statusCode == 200 || response.statusCode == 201;
    } catch (e) {
      print("Error checking in habit: $e");
      return false;
    }
  }
  
}
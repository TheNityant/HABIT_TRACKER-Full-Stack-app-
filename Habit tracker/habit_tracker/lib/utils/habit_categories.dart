import 'package:flutter/material.dart';

class HabitCategories {
  // The list of available categories
  static const List<String> list = [
    'Health',
    'Work',
    'Studies',
    'Fitness',
    'Mental',
    'Social',
    'Other'
  ];

  // Map Category Name -> Icon
  static IconData getIcon(String category) {
    switch (category) {
      case 'Health': return Icons.local_hospital_outlined;
      case 'Work': return Icons.work_outline;
      case 'Studies': return Icons.school_outlined;
      case 'Fitness': return Icons.fitness_center_outlined;
      case 'Mental': return Icons.self_improvement;
      case 'Social': return Icons.people_outline;
      default: return Icons.category_outlined;
    }
  }

  // Map Category Name -> Color
  static Color getColor(String category) {
    switch (category) {
      case 'Health': return Colors.redAccent;
      case 'Work': return Colors.blueAccent;
      case 'Studies': return Colors.orangeAccent;
      case 'Fitness': return Colors.greenAccent;
      case 'Mental': return Colors.purpleAccent;
      case 'Social': return Colors.pinkAccent;
      default: return const Color.fromARGB(255, 0, 0, 0);
    }
  }
}
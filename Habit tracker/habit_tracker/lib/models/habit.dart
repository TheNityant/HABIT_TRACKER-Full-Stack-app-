class Habit {
  final String id;
  final String title;
  final List<String> completedDays; 
  final DateTime dateCreated;
  final String category; // <--- NEW FIELD

  Habit({
    required this.id,
    required this.title,
    this.completedDays = const [], 
    required this.dateCreated,
    this.category = 'Other', // <--- Default value
  });

  // HELPER: Check if the habit is done ON A SPECIFIC DATE
  bool isCompleted(DateTime date) {
    // Convert the date to "YYYY-MM-DD"
    String dateString = date.toIso8601String().split('T')[0];
    return completedDays.contains(dateString);
  }
  // ... (Keep isSameDay and isCompleted logic here) ...

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'completedDays': completedDays,
      'dateCreated': dateCreated.toIso8601String(),
      'category': category, // <--- SAVE IT
    };
  }

  factory Habit.fromMap(Map<String, dynamic> map) {
    return Habit(
      id: map['id'],
      title: map['title'],
      completedDays: List<String>.from(map['completedDays'] ?? []),
      dateCreated: map['dateCreated'] != null 
          ? DateTime.parse(map['dateCreated']) 
          : DateTime.now(),
      category: map['category'] ?? 'Other', // <--- LOAD IT (Safe default)
    );
  }
}
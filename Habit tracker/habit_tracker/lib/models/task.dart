class Task {
  final String id;
  final String title;
  final bool isCompleted;
  final String priority;
  final String category;
  final DateTime? completedAt; // <--- NEW FIELD

  Task({
    required this.id,
    required this.title,
    this.isCompleted = false,
    this.priority = 'Medium',
    this.category = 'Other',
    this.completedAt, // <--- Add to constructor
  });

  // Update copyWith to include the new field
  Task copyWith({
    String? id,
    String? title,
    bool? isCompleted,
    String? priority,
    String? category,
    DateTime? completedAt, // <--- Add here
  }) {
    return Task(
      id: id ?? this.id,
      title: title ?? this.title,
      isCompleted: isCompleted ?? this.isCompleted,
      priority: priority ?? this.priority,
      category: category ?? this.category,
      completedAt: completedAt ?? this.completedAt, // <--- Add here
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'isCompleted': isCompleted ? 1 : 0,
      'priority': priority,
      'category': category,
      // Save the timestamp (if it exists)
      'completedAt': completedAt?.toIso8601String(), 
    };
  }

  factory Task.fromMap(Map<String, dynamic> map) {
    return Task(
      id: map['id'],
      title: map['title'],
      isCompleted: map['isCompleted'] == 1,
      priority: map['priority'] ?? 'Medium',
      category: map['category'] ?? 'Other',
      // Load the timestamp
      completedAt: map['completedAt'] != null 
          ? DateTime.parse(map['completedAt']) 
          : null,
    );
  }
}
# Habit Tracker

A comprehensive Flutter mobile application for tracking daily habits, managing tasks, and visualizing progress through analytics. Built with a modern architecture using Provider state management and local storage persistence.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Project Architecture](#project-architecture)
5. [Data Models](#data-models)
6. [Folder Structure](#folder-structure)
7. [Setup & Installation](#setup--installation)
8. [State Management](#state-management)
9. [Data Persistence](#data-persistence)
10. [Widget Integration](#widget-integration)
11. [Development Guide](#development-guide)

---

## 🎯 Overview

**Habit Tracker** is a full-featured habit management and productivity application designed for Android, iOS, Linux, macOS, and Windows. It enables users to:

- Create and manage daily habits with category organization
- Track task completion with priority levels
- Visualize progress through interactive analytics
- Set notifications for habit reminders
- Display habit statistics on home widget (Android)
- Store all data locally using SharedPreferences

**App Version:** 1.0.0+1  
**Min SDK:** Flutter 3.10.3  
**Architecture:** Clean Architecture with Provider Pattern

---

## ✨ Features

### Core Features

#### 🎪 Habit Management
- Create habits with customizable titles
- Organize habits into 7 predefined categories:
  - Health (♥️ Red)
  - Work (💼 Blue)
  - Studies (📚 Orange)
  - Fitness (💪 Green)
  - Mental (🧘 Purple)
  - Social (👥 Pink)
  - Other (⚙️ Gray)
- Track completion status on specific dates
- Mark habits as done/not done with single tap
- Delete habits with confirmation
- Persistent storage across app sessions

#### 📝 Task Management
- Create tasks with titles and descriptions
- Assign priority levels: `High`, `Medium`, `Low`
- Categorize tasks similar to habits
- Toggle task completion status
- Delete completed or unwanted tasks
- Visual priority indicators

#### 📊 Analytics & Statistics
- Real-time habit completion percentage
- Daily habit streak tracking
- Category-based performance breakdown
- Interactive pie charts and graphs
- Historical data visualization
- Weekly/monthly summary views

#### 🔔 Notifications
- Scheduled daily reminders for habits
- Flutter Local Notifications integration
- Timezone support for accurate reminders
- Notification customization per habit

#### 🏠 Android Widget Integration
- Home widget display of habit statistics
- Real-time widget updates
- Completion percentage visualization
- Quick access to app from widget

### Non-Functional Features

- **Dark Mode Only:** Modern dark theme throughout
- **Responsive Design:** Optimized for all screen sizes
- **Smooth Animations:** Transition between screens and states
- **Performance Optimized:** Efficient state updates and re-renders
- **Multi-Platform:** Works on Android, iOS, Linux, macOS, Windows

---

## 🛠 Tech Stack

### Core Framework
- **Flutter 3.10.3+** - Cross-platform mobile framework
- **Dart 3.10.3+** - Programming language

### State Management
- **Provider 6.1.5+1** - Reactive state management
- **ChangeNotifier** - Base for state changes

### UI & Design
- **Material 3** - Latest Material Design guidelines
- **Google Fonts 6.3.3** - Custom typography (Inter font family)
- **Cupertino Icons 1.0.8** - iOS-style icons

### Data & Storage
- **SharedPreferences 2.5.4** - Local key-value storage
- **JSON Serialization** - Data persistence format

### Features & Integrations
- **Table Calendar 3.2.0** - Calendar widget for date selection
- **FL Chart 1.1.1** - Interactive charts and graphs
- **Flutter Local Notifications 17.0.0** - Push notifications
- **Timezone 0.9.2** - Timezone handling for notifications
- **Home Widget 0.7.0** - Android home widget support
- **Intl 0.20.2** - Internationalization and date formatting

### Development Tools
- **Flutter Lints 6.0.0** - Code quality and style guidelines

---

## 🏗 Project Architecture

### Architecture Pattern: Clean Architecture with Provider

The application follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│  (Screens, Widgets, UI Components)                       │
│  ├── screens/                                            │
│  ├── widgets/                                            │
│  └── theme/                                              │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                 STATE MANAGEMENT LAYER                   │
│  (Provider, ChangeNotifier)                              │
│  └── services/habit_provider.dart                        │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                  │
│  (Data Models, Utilities)                                │
│  ├── models/                                             │
│  └── utils/                                              │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│                    DATA LAYER                            │
│  (Persistence, Local Storage)                            │
│  └── SharedPreferences                                   │
└─────────────────────────────────────────────────────────┘
```

### Key Architectural Principles

1. **Unidirectional Data Flow:** Data flows from Provider → UI, user actions trigger state updates
2. **Single Responsibility:** Each class/file has one clear purpose
3. **Dependency Injection:** Provider handles dependency injection via MultiProvider
4. **Immutability:** Models use `toMap()` and `fromMap()` for serialization
5. **Reactive Updates:** UI automatically rebuilds when state changes

---

## 💾 Data Models

### 1. Habit Model

**File:** [lib/models/habit.dart](lib/models/habit.dart)

#### Data Structure

```dart
class Habit {
  final String id;                    // Unique identifier (ISO timestamp)
  final String title;                 // Habit name/title
  final List<String> completedDays;   // List of dates (YYYY-MM-DD format)
  final DateTime dateCreated;         // When habit was created
  final String category;              // Category name (from HabitCategories)
}
```

#### Properties Breakdown

| Property | Type | Description | Example |
|----------|------|-------------|---------|
| `id` | String | Unique identifier using DateTime | "2025-12-26T14:30:45.123456" |
| `title` | String | Display name of the habit | "Morning Exercise" |
| `completedDays` | List<String> | Dates marked as complete (YYYY-MM-DD) | ["2025-12-26", "2025-12-25"] |
| `dateCreated` | DateTime | Creation timestamp | 2025-12-26 14:30:45.123456 |
| `category` | String | Categorization for organization | "Fitness", "Health", "Work" |

#### Key Methods

- **`isCompleted(DateTime date)`** - Check if habit was completed on a specific date
- **`toMap()`** - Convert to Map for JSON serialization
- **`fromMap(Map map)`** - Factory constructor to deserialize from Map

#### Default Values

```dart
Habit(
  category: 'Other',              // Default category
  completedDays: [],              // Initially no completions
)
```

#### Usage Example

```dart
// Create a new habit
Habit newHabit = Habit(
  id: DateTime.now().toString(),
  title: "Meditation",
  dateCreated: DateTime.now(),
  completedDays: [],
  category: "Mental"
);

// Check completion on a date
bool isCompletedToday = newHabit.isCompleted(DateTime.now());

// Serialize to storage
Map<String, dynamic> habitMap = newHabit.toMap();

// Deserialize from storage
Habit loadedHabit = Habit.fromMap(habitMap);
```

---

### 2. Task Model

**File:** [lib/models/task.dart](lib/models/task.dart)

#### Data Structure

```dart
class Task {
  final String id;          // Unique identifier
  final String title;       // Task description
  final bool isCompleted;   // Completion status
  final String priority;    // Priority level (High/Medium/Low)
  final String category;    // Category name
}
```

#### Properties Breakdown

| Property | Type | Description | Example |
|----------|------|-------------|---------|
| `id` | String | Unique identifier using DateTime | "2025-12-26T14:30:45.123456" |
| `title` | String | Task description/name | "Complete project report" |
| `isCompleted` | bool | Whether task is done | true, false |
| `priority` | String | Task priority level | "High", "Medium", "Low" |
| `category` | String | Task categorization | "Work", "Studies" |

#### Default Values

```dart
Task(
  isCompleted: false,         // Initially not completed
  priority: 'Medium',         // Default priority
  category: 'Other',          // Default category
)
```

#### Key Methods

- **`copyWith({...})`** - Create a copy with selected fields updated (Immutable pattern)
- **`toMap()`** - Serialize to Map for storage
- **`fromMap(Map map)`** - Factory constructor for deserialization

#### Usage Example

```dart
// Create a task
Task task = Task(
  id: DateTime.now().toString(),
  title: "Design UI mockups",
  isCompleted: false,
  priority: "High",
  category: "Work"
);

// Update task with copyWith (immutable pattern)
Task updatedTask = task.copyWith(isCompleted: true);

// Serialize and deserialize
Map<String, dynamic> taskMap = task.toMap();
Task loadedTask = Task.fromMap(taskMap);
```

---

## 📁 Folder Structure & Architecture

### Complete Directory Layout

```
lib/
├── main.dart                          # App entry point & MaterialApp setup
├── render.dart                        # Widget rendering utilities & charts
│
├── models/                            # 📦 DATA MODELS LAYER
│   ├── habit.dart                     # Habit data model (47 lines)
│   └── task.dart                      # Task data model (65 lines)
│
├── screens/                           # 🎨 PRESENTATION LAYER (UI Screens)
│   ├── main_scaffold.dart             # Root navigation scaffold
│   ├── home_screen.dart               # Dashboard/habits overview
│   ├── stats_screen.dart              # Analytics & statistics
│   ├── habit_details_screen.dart      # Individual habit details
│   └── tasks_screen.dart              # Task management interface
│
├── services/                          # 🔄 STATE MANAGEMENT LAYER
│   └── habit_provider.dart            # Central state provider (208 lines)
│       ├── Habits management section
│       ├── Tasks management section
│       └── Storage & persistence logic
│
├── widgets/                           # 🧩 REUSABLE COMPONENTS
│   └── widget_renderer.dart           # Android widget display components
│
├── utils/                             # 🛠 UTILITY FUNCTIONS & CONSTANTS
│   └── habit_categories.dart          # Category definitions, colors, icons
│
└── theme/                             # 🎨 THEMING (currently empty)
    └── [theme configuration files]

android/                               # 🤖 ANDROID PLATFORM-SPECIFIC
├── app/
│   └── src/
│       └── [Android source files]
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts

ios/                                   # 🍎 iOS PLATFORM-SPECIFIC
└── [iOS configuration files]

[web/, linux/, macos/, windows/]      # 🌐 OTHER PLATFORMS

test/                                  # 🧪 TEST FILES
└── [Test specifications]
```

---

### 📂 Detailed Module Breakdown

#### **1. Models Layer** (`lib/models/`)

**Purpose:** Define data structures and entities

**Files:**
- `habit.dart` - Complete habit definition with completion tracking
- `task.dart` - Task definition with priority and category

**Key Responsibilities:**
- Define data structure
- Provide serialization (toMap/fromMap)
- Include helper methods (isCompleted, copyWith)
- Ensure immutability patterns

**Dependencies:** None (pure Dart)

---

#### **2. Screens Layer** (`lib/screens/`)

**Purpose:** UI presentation and user interaction

**Files & Responsibilities:**

1. **main_scaffold.dart** (Navigation Hub)
   - Root navigation structure
   - Bottom navigation bar with 2 main tabs
   - Manages page switching between screens
   - Uses StatefulWidget for navigation state

2. **home_screen.dart** (Dashboard)
   - Displays all habits
   - Displays all tasks
   - Quick habit completion toggle
   - Unified overview of daily activities
   - Calendar integration for date selection

3. **stats_screen.dart** (Analytics)
   - Habit completion statistics
   - Pie charts and graphs using FL Chart
   - Category breakdown analysis
   - Streaks and achievements display

4. **habit_details_screen.dart** (Detail View)
   - Individual habit information
   - Detailed calendar with completion history
   - Edit habit information
   - Delete habit option

5. **tasks_screen.dart** (Task Management)
   - Create new tasks
   - List all tasks with priority display
   - Mark tasks as done
   - Delete tasks

**Navigation Flow:**
```
MainScaffold (Navigation Root)
  ├── HomeScreen (Tab 0)
  │   ├── Habits List
  │   ├── Tasks List
  │   └── Quick Actions
  └── StatsScreen (Tab 1)
      ├── Charts & Graphs
      ├── Category Analytics
      └── Streak Information
```

**UI Patterns:**
- Material Design 3 components
- Dark theme (0xFF121212 background)
- Responsive layouts
- FloatingActionButton for adding items

---

#### **3. Services Layer** (`lib/services/`)

**Purpose:** State management and business logic

**File:** `habit_provider.dart` (208 lines - Central State Management)

**Architecture:** Provider + ChangeNotifier Pattern

**Key Classes:**

```dart
class HabitProvider extends ChangeNotifier {
  // HABITS MANAGEMENT
  List<Habit> _habits = [];
  
  void addHabit(String title, String category)
  void toggleHabit(String id, DateTime date)
  void deleteHabit(String id)
  
  // TASKS MANAGEMENT
  List<Task> _tasks = [];
  
  void addTask(String title, String priority, String category)
  void toggleTask(String id)
  void deleteTask(String id)
  
  // PERSISTENCE
  void _saveHabitsToStorage()
  void _loadHabitsFromStorage()
  void _saveTasksToStorage()
  void _loadTasksFromStorage()
  
  // ANDROID WIDGET
  void _updateAndroidWidget()
}
```

**State Management Flow:**

```
User Action (UI)
    ↓
HabitProvider Method Called
    ↓
Update Internal State (_habits, _tasks)
    ↓
Save to SharedPreferences
    ↓
Call notifyListeners()
    ↓
Rebuild Listening Widgets (via Consumer)
```

**Responsibilities:**
- Manage `_habits` list (CRUD operations)
- Manage `_tasks` list (CRUD operations)
- Handle serialization to SharedPreferences
- Load data on app startup
- Trigger Android widget updates
- Notify UI listeners of changes

**Integration Points:**
- Persists to SharedPreferences
- Communicates with HomeWidget for Android widget
- Accessed throughout app via `Provider.of<HabitProvider>(context)`

---

#### **4. Widgets Layer** (`lib/widgets/`)

**Purpose:** Reusable UI components for Android widget display

**File:** `widget_renderer.dart`

**Key Components:**
- `WidgetChartWrapper` - Container for widget display
- `HabitsGraphWidget` - Statistics visualization
- Circular progress indicators
- Completion percentage displays

**Usage:** Rendered to Android home widget via HomeWidget plugin

---

#### **5. Utils Layer** (`lib/utils/`)

**Purpose:** Utilities, constants, and helper functions

**File:** `habit_categories.dart` (Category System)

**Defines:**

```dart
class HabitCategories {
  static const List<String> list = [
    'Health',      // 🏥 Red
    'Work',        // 💼 Blue
    'Studies',     // 📚 Orange
    'Fitness',     // 💪 Green
    'Mental',      // 🧘 Purple
    'Social',      // 👥 Pink
    'Other'        // ⚙️ Gray
  ];
  
  static IconData getIcon(String category)
  static Color getColor(String category)
}
```

**Features:**
- Category list for dropdowns
- Icon mapping for each category
- Color mapping for visual differentiation
- Centralized constants for consistency

---

#### **6. Root Layer**

**main.dart** - Application entry point
```dart
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => HabitProvider()),
      ],
      child: const HabitTrackerApp(),
    ),
  );
}
```

**Responsibilities:**
- Initialize HabitProvider globally
- Set up Material 3 theme
- Configure dark mode
- Set Google Fonts
- Define route home (MainScaffold)

**Theme Configuration:**
- **Background:** `#121212` (Dark gray)
- **Primary Color:** Indigo Accent
- **Secondary Color:** Teal Accent
- **Surface:** `#1E1E1E`
- **Text:** Inter font family via Google Fonts
- **Material 3:** Enabled for modern UI

---

## 🔄 State Management

### Provider Architecture

The app uses **Provider 6.1.5+1** with **ChangeNotifier** for reactive state management.

### Initialization

**Location:** [lib/main.dart](lib/main.dart#L5-L16)

```dart
MultiProvider(
  providers: [
    ChangeNotifierProvider(create: (_) => HabitProvider()),
  ],
  child: const HabitTrackerApp(),
)
```

### Accessing State in Widgets

**Method 1: Read-Only (Doesn't trigger rebuild)**
```dart
HabitProvider provider = Provider.of<HabitProvider>(context, listen: false);
List<Habit> habits = provider.habits;
```

**Method 2: Reactive (Rebuilds on change)**
```dart
List<Habit> habits = Provider.of<HabitProvider>(context).habits;
```

**Method 3: Consumer Pattern (Best practice)**
```dart
Consumer<HabitProvider>(
  builder: (context, provider, child) {
    return ListView(
      children: provider.habits.map((habit) {
        return HabitTile(habit: habit);
      }).toList(),
    );
  },
)
```

### State Updates

All state updates follow this pattern:

1. **Method called** on provider (e.g., `addHabit()`)
2. **Modify internal state** (update `_habits` list)
3. **Persist to storage** (call `_saveHabitsToStorage()`)
4. **Notify listeners** (call `notifyListeners()`)
5. **Rebuild UI** (all listening widgets rebuild)

### Complete State Management Flow

```
┌─────────────────────────────────────────┐
│         HabitProvider (Service)          │
│  ├─ _habits: List<Habit>                │
│  ├─ _tasks: List<Task>                  │
│  └─ Methods:                            │
│     ├─ addHabit(title, category)        │
│     ├─ toggleHabit(id, date)            │
│     ├─ deleteHabit(id)                  │
│     ├─ addTask(title, priority, cat)    │
│     ├─ toggleTask(id)                   │
│     └─ deleteTask(id)                   │
└─────────────────┬───────────────────────┘
                  │
                  │ notifyListeners()
                  │
     ┌────────────▼────────────┐
     │   Listening Widgets     │
     │  Consumer<HabitProvider>│
     │   build() called again  │
     └────────────────────────┘
```

---

## 💾 Data Persistence

### Storage Strategy

The app uses **SharedPreferences** for local data persistence - a key-value store suitable for small to medium datasets.

### Persistence Layer

**Location:** [lib/services/habit_provider.dart](lib/services/habit_provider.dart#L80-L130)

### Data Storage Format

**Habits Storage:**
- **Key:** `'habits'`
- **Format:** JSON array of serialized Habit objects
- **Example:**
```json
[
  {
    "id": "2025-12-26T14:30:45.123456",
    "title": "Morning Exercise",
    "completedDays": ["2025-12-26", "2025-12-25"],
    "dateCreated": "2025-12-20T08:00:00.000",
    "category": "Fitness"
  }
]
```

**Tasks Storage:**
- **Key:** `'tasks'`
- **Format:** JSON array of serialized Task objects
- **Example:**
```json
[
  {
    "id": "2025-12-26T14:30:45.123456",
    "title": "Complete project report",
    "isCompleted": 1,
    "priority": "High",
    "category": "Work"
  }
]
```

### Serialization Methods

Each model implements serialization:

**Habit:**
```dart
// Save
Map<String, dynamic> habitMap = habit.toMap();

// Load
Habit habit = Habit.fromMap(habitMap);
```

**Task:**
```dart
// Save
Map<String, dynamic> taskMap = task.toMap();

// Load
Task task = Task.fromMap(taskMap);
```

### Persistence Flow

**Save Process:**
```
Model Change (UI Action)
    ↓
Provider Method Updates State
    ↓
_saveHabitsToStorage() / _saveTasksToStorage()
    ↓
Convert models to Maps
    ↓
JSON encode (jsonEncode)
    ↓
Save to SharedPreferences
```

**Load Process (App Startup):**
```
Provider initialized
    ↓
_loadHabitsFromStorage()
    ↓
Retrieve JSON string from SharedPreferences
    ↓
JSON decode (jsonDecode)
    ↓
Convert Maps to Models
    ↓
Update internal _habits/_tasks lists
    ↓
UI reflects loaded data
```

### Data Lifetime

- **Creation:** When item added via UI
- **Update:** Any completion toggle or edit
- **Deletion:** When item removed from list
- **Persistence:** Immediately saved to device
- **Recovery:** Auto-loaded on app restart

---

## 🏠 Android Widget Integration

### Home Widget Support

The app provides an Android home widget for quick habit statistics viewing.

**Dependencies:**
- `home_widget: ^0.7.0` - Flutter plugin for home widgets
- `flutter_local_notifications: ^17.0.0` - Notification integration

### Widget Features

- Display habit completion percentage
- Show today's habit stats
- Visual progress indicator
- One-tap access to open app

### Widget Update Trigger

**Location:** [lib/services/habit_provider.dart](lib/services/habit_provider.dart#L35)

```dart
void _updateAndroidWidget() {
  HomeWidget.updateWidget(
    name: 'HabitTrackerWidget',
    androidName: 'HabitTrackerWidget',
  );
}
```

**Called After:**
- Adding a habit
- Toggling habit completion
- Deleting a habit
- Any state change affecting statistics

### Widget Rendering

**Location:** [lib/widgets/widget_renderer.dart](lib/widgets/widget_renderer.dart)

Contains reusable widgets for:
- `WidgetChartWrapper` - Container styling
- `HabitsGraphWidget` - Statistics visualization
- `CircularProgressIndicator` - Completion ring
- Percentage display components

---

## ⚙ Setup & Installation

### Prerequisites

- **Flutter SDK:** 3.10.3 or higher
- **Dart SDK:** 3.10.3 or higher
- **Android Studio** (for Android development)
- **Xcode** (for iOS development - Mac only)

### Installation Steps

1. **Clone the repository:**
```bash
git clone <repository-url>
cd habit_tracker
```

2. **Install dependencies:**
```bash
flutter pub get
```

3. **Run on Android:**
```bash
flutter run
```

4. **Run on iOS:**
```bash
flutter run -d iPhone
```

5. **Run on Web:**
```bash
flutter run -d web
```

### Configuration

**Android Configuration:**
- Minimum SDK: 21 (set in `android/app/build.gradle.kts`)
- Target SDK: Latest (set in Android configuration)

**iOS Configuration:**
- Minimum iOS: 12.0 (set in `ios/Podfile`)

---

## 🚀 Development Guide

### Adding a New Habit Category

1. **Update** [lib/utils/habit_categories.dart](lib/utils/habit_categories.dart)
```dart
static const List<String> list = [
  'Health',
  'Work',
  'Studies',
  'Fitness',
  'Mental',
  'Social',
  'NewCategory',  // Add here
  'Other'
];
```

2. **Add icon mapping:**
```dart
case 'NewCategory': return Icons.your_icon;
```

3. **Add color mapping:**
```dart
case 'NewCategory': return Colors.yourColor;
```

### Adding a New Feature

1. **Create data model** in `lib/models/` if needed
2. **Add state management** in `lib/services/habit_provider.dart`
3. **Create UI screens** in `lib/screens/`
4. **Create reusable widgets** in `lib/widgets/`
5. **Update navigation** in `main_scaffold.dart`

### Debugging

**Enable debug logging:**
```bash
flutter run -v
```

**Check shared preferences:**
```dart
final prefs = await SharedPreferences.getInstance();
print(prefs.getKeys());
```

### Code Structure Best Practices

- **One file = One primary class**
- **Models are immutable** (use copyWith for updates)
- **State updates trigger notifyListeners()**
- **Always serialize/deserialize data**
- **Use Consumer pattern for reactive widgets**
- **Keep business logic in Provider**
- **Keep UI in Screens and Widgets**

---

## 📊 Complete Data Structure Reference

### Habit Object Tree

```
Habit
  ├── id: String (DateTime.now().toString())
  ├── title: String (user input)
  ├── dateCreated: DateTime
  ├── category: String (from HabitCategories.list)
  └── completedDays: List<String>
      ├── "2025-12-26" (YYYY-MM-DD format)
      ├── "2025-12-25"
      └── ...
```

### Task Object Tree

```
Task
  ├── id: String (DateTime.now().toString())
  ├── title: String (user input)
  ├── isCompleted: bool
  ├── priority: String ("High" | "Medium" | "Low")
  └── category: String (from HabitCategories.list)
```

### Provider State Tree

```
HabitProvider
  ├── _habits: List<Habit>
  │   ├── [0] Habit { ... }
  │   ├── [1] Habit { ... }
  │   └── ...
  ├── _tasks: List<Task>
  │   ├── [0] Task { ... }
  │   ├── [1] Task { ... }
  │   └── ...
  ├── Getters:
  │   ├── habits: List<Habit> (read-only)
  │   └── tasks: List<Task> (read-only)
  └── Methods:
      ├── addHabit(title, category)
      ├── toggleHabit(id, date)
      ├── deleteHabit(id)
      ├── addTask(title, priority, category)
      ├── toggleTask(id)
      └── deleteTask(id)
```

---

## 📝 Notes for Developers

- **SharedPreferences Key Names:** `'habits'` and `'tasks'` - don't change these
- **DateTime Format:** ISO 8601 with timezone info for storage
- **Completion Dates:** Store as `'YYYY-MM-DD'` strings for easy comparison
- **Provider Scope:** HabitProvider is global, available to all widgets
- **Widget Updates:** Always call `notifyListeners()` after state changes
- **Android Widget:** Updates trigger automatically on state changes
- **Icon Sets:** Uses Material Icons library
- **Dark Theme:** Colors optimized for dark mode (`#121212` background)

---

## 🔗 Resources

- [Flutter Documentation](https://docs.flutter.dev/)
- [Provider Package](https://pub.dev/packages/provider)
- [SharedPreferences Plugin](https://pub.dev/packages/shared_preferences)
- [Material 3 Design](https://m3.material.io/)
- [Dart Language Tour](https://dart.dev/guides/language/language-tour)

---

## 📄 License

This project is private and not publicly licensed.

**Last Updated:** December 26, 2025  
**Maintained by:** Development Team
#   N i t d e v 
 
 
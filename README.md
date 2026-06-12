<div align="center">
  <h1>🌟 Full-Stack Habit Tracker</h1>
  <p>A comprehensive, feature-rich Habit Tracker built with <b>Flutter</b> and <b>Spring Boot</b>.</p>

  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/TheNityant/HABIT_TRACKER-Full-Stack-app-">
  <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/TheNityant/HABIT_TRACKER-Full-Stack-app-">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-green.svg">
</div>

---

## 📖 Overview

The **Habit Tracker** is a full-stack mobile application designed to help users track their daily habits, manage tasks, and visualize their progress over time through beautiful analytics. 

With a robust **Spring Boot (Java)** backend paired with a modern **Flutter** mobile frontend, this project demonstrates scalable architecture, clean code practices, and a responsive user interface.

## ✨ Features

- **Habit Management**: Create, update, delete, and track daily habits.
- **Progress Tracking**: Visualize your habit streaks and completion rates.
- **Task Management**: Organize to-do items alongside your habits.
- **Cross-Platform**: Beautifully designed for both iOS and Android.
- **Robust API**: RESTful architecture powered by Spring Boot.
- **Data Persistence**: Secure and reliable PostgreSQL database integration.
- **State Management**: Efficient UI updates using Provider in Flutter.

## 🛠️ Tech Stack

### Frontend (Mobile App)
- **Framework**: [Flutter](https://flutter.dev/)
- **Language**: Dart
- **State Management**: Provider
- **Storage**: Local persistence & REST API integration

### Backend (REST API)
- **Framework**: [Spring Boot 4.0](https://spring.io/projects/spring-boot) (Spring WebMVC, Spring Data JPA)
- **Language**: Java 26
- **Database**: PostgreSQL
- **Utilities**: Lombok, Jackson Databind

## 📂 Project Structure

```text
HABIT_TRACKER-Full-Stack-app-/
└── Habit tracker/
    ├── habit_tracker/               # Flutter Frontend Application
    │   ├── lib/                     # Dart source code
    │   ├── android/                 # Android-specific files
    │   ├── ios/                     # iOS-specific files
    │   └── pubspec.yaml             # Flutter dependencies
    │
    ├── habit_tracker backend/       # Spring Boot Backend API
    │   └── tracker/
    │       ├── src/main/java/       # Java source code
    │       ├── src/main/resources/  # Application properties & config
    │       ├── pom.xml              # Maven dependencies
    │       └── Dockerfile           # Docker configuration
    │
    └── Spring-Boot-Backend-Documentation.docx # In-depth backend docs
```

## 🚀 Getting Started

### Prerequisites

- **Frontend**: Flutter SDK (v3.0+ recommended), Android Studio / Xcode.
- **Backend**: Java JDK 26+, Maven, PostgreSQL database.

### 1. Setting up the Backend

1. Navigate to the backend directory:
   ```bash
   cd "Habit tracker/habit_tracker backend/tracker/tracker"
   ```
2. Configure your database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/habittracker
   spring.datasource.username=your_username
   spring.datasource.pass=YOUR_DB_PASS
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Build and run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

### 2. Setting up the Frontend

1. Navigate to the frontend directory:
   ```bash
   cd "Habit tracker/habit_tracker"
   ```
2. Install Flutter dependencies:
   ```bash
   flutter pub get
   ```
3. Ensure your emulator or physical device is connected.
4. Run the app:
   ```bash
   flutter run
   ```

## 📚 Documentation

For a deep dive into the backend architecture, endpoints, and design decisions, please refer to the comprehensive documentation provided in the repository:
* `Habit tracker/Spring-Boot-Backend-Documentation.docx`

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](../../issues).

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

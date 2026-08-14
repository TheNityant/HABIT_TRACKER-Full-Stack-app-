# 🌟 HABIT_TRACKER_Full_Stack_App

A cross-platform habit and task management application with cloud-backed persistence, analytics, journaling, and AI-assisted journal parsing.

- **Frontend:** Flutter (mobile + desktop/web targets)
- **Backend:** Spring Boot REST API
- **Database:** PostgreSQL
- **Extra:** AI-assisted journal parsing via Gemini API

---

## 📌 Project Structure

```text
HABIT_TRACKER_Full_Stack_app/
└── Habit tracker/
    ├── habit_tracker/                       # Flutter app
    └── habit_tracker backend/
        └── tracker/tracker/                # Spring Boot backend
```

---

## ✨ Main Features

- User registration and login
- Habit CRUD + daily check-in and streak tracking
- Task CRUD + priority and completion toggle
- Analytics screen with progress visuals and calendar-based history
- Journal endpoints with optional AI parsing
- File upload endpoint for media attachments
- Multi-platform Flutter UI with modern dark theme

---

## 🧰 Tech Stack

### Frontend
- Flutter / Dart
- Provider, SharedPreferences
- table_calendar, fl_chart, google_fonts

### Backend
- Java + Spring Boot
- Spring Web MVC, Spring Data JPA
- PostgreSQL

---

## 🚀 Installation & Local Setup

## 1) Prerequisites

Install:

- **Flutter SDK** (compatible with Dart 3.10.x)
- **Java JDK** (project `pom.xml` currently sets `java.version` to `26`)
- **Maven** (or use the included Maven Wrapper)
- **PostgreSQL** database

---

## 2) Clone and enter repository

```bash
git clone https://github.com/TheNityant/HABIT_TRACKER-Full-Stack-app.git
cd HABIT_TRACKER-Full-Stack-app
```

---

## 3) Backend setup (Spring Boot)

Backend path:

```bash
cd "Habit tracker/habit_tracker backend/tracker/tracker"
```

Update `src/main/resources/application.properties` with your own values:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

Set Gemini API key (used by AI journal service):

```bash
export GEMINI_API_KEY="your_api_key_here"
```

Run backend:

```bash
./mvnw spring-boot:run
```

Backend default URL:

- `http://localhost:8080`

Example health check:

- `GET http://localhost:8080/api/habits/ping`

---

## 4) Frontend setup (Flutter)

Frontend path:

```bash
cd "Habit tracker/habit_tracker"
```

Install dependencies:

```bash
flutter pub get
```

### Configure API base URL (important)

`lib/services/api_service.dart` currently points to a hosted backend:

```dart
static const String baseUrl = "https://habit-tracker-backend-o9bs.onrender.com/api";
```

If you want to use your local backend, change it to:

```dart
static const String baseUrl = "http://localhost:8080/api";
```

Then run app:

```bash
flutter run
```

---

## 🔌 API Areas (High-level)

- `/api/auth` → login/register
- `/api/habits` → habit operations + check-in
- `/api/tasks` → task operations + toggle
- `/api/journal` → journal CRUD and AI workflow
- `/api/upload` → media file upload

---

## 🎥 Demo Video

> _Add your project walkthrough/demo video link here_

- YouTube: `TBD`
- Short feature walkthrough: `TBD`

---

## 📝 Notes

- Uploads are served from backend `/uploads/**` resource mapping.
- CORS is enabled in controllers (`@CrossOrigin(origins = "*")`) for client integration.
- The Flutter module includes platform folders for Android, iOS, web, Linux, macOS, and Windows.

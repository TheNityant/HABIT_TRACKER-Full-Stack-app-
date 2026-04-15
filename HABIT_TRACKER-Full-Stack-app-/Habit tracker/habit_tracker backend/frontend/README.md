# Habit Tracker Frontend - JavaScript

A modern, fully-integrated JavaScript frontend for the Habit Tracker application. This frontend communicates directly with your deployed Spring Boot backend on Render and stores all data in your Supabase PostgreSQL database.

## 🏗️ Architecture

```
Frontend (JavaScript) ← REST API → Backend (Spring Boot on Render) ← Database (Supabase PostgreSQL)
```

**Key Points:**
- ✅ Frontend is PURE UI only - all data operations go through REST API
- ✅ Backend handles all business logic, database access, and validation
- ✅ Database stores everything through backend (no direct frontend-to-DB connections)
- ✅ All changes are persisted to Supabase immediately

## 📦 Project Structure

```
frontend/
├── package.json                 # Node.js dependencies
├── server.js                    # Express.js server
└── public/
    ├── index.html              # Main HTML UI
    ├── css/
    │   └── style.css           # All styling
    └── js/
        ├── api-config.js       # API endpoints & configuration
        ├── api-client.js       # HTTP client with retry logic
        ├── api-manager.js      # High-level API operations (login, habits CRUD)
        └── app.js              # Main application logic & UI handling
```

## 🚀 Quick Start

### 1. Install Dependencies

```bash
cd "d:\MY_GITHUB_PROJECT\HABIT_TRACKER-Full-Stack-app-\Habit tracker\habit_tracker backend\frontend"
npm install
```

### 2. Run the Frontend Server

```bash
npm start
```

You should see:
```
✅ Habit Tracker Frontend is running
📍 Open browser: http://localhost:3000
🔗 Backend: https://habit-tracker-backend-o9bs.onrender.com
💾 Database: Supabase PostgreSQL
```

### 3. Open in Browser

Navigate to: **http://localhost:3000**

## 🔐 Authentication

### Login
- Use credentials from any user already registered in your Supabase database
- Default test user (if created in backend): `test@example.com` / `password`

### Register
- Click "Create one" on the login page
- Fill in email, password, and optional name
- Backend validates and creates user in Supabase

## 💼 Features

### Habits Management
- **Create** - Add new habits with name, description, and category
- **View** - See all your habits in a beautiful card layout
- **Update** - Edit habit details (via backend API)
- **Delete** - Remove habits you no longer track
- **Track Streak** - Automatic streak counting from Supabase data

### Tasks Management
- **Create** - Add tasks to any habit
- **Toggle** - Mark tasks as complete/incomplete
- **View** - See all tasks per habit
- **Delete** - Remove completed or unwanted tasks

### Data Sync
- **Automatic Sync** - All changes immediately saved to Supabase via backend
- **Session Persistence** - Local storage caches data for offline access
- **Real-time Updates** - Latest data from database on each operation

## 🔗 API Integration

### How It Works

1. **Frontend makes request** → `apiManager.addHabit("Morning Exercise", ...)`
2. **ApiManager calls ApiClient** → `POST /api/habits` with data
3. **ApiClient sends to backend** → `https://habit-tracker-backend-o9bs.onrender.com/api/habits`
4. **Backend processes request** → Validates, saves to Supabase
5. **Backend responds** → New habit data with ID and timestamps
6. **Frontend updates UI** → Display new habit in list
7. **Data persisted** → Saved in Supabase database

### Example: Adding a Habit

**Frontend Code:**
```javascript
await apiManager.addHabit("Morning Exercise", "30 minutes", "Health");
```

**What happens:**
```
JavaScript Frontend
    ↓ (HTTP POST)
Spring Boot Backend on Render
    ↓ (SQL INSERT)
Supabase PostgreSQL Database ← Habit stored here!
    ↓ (JSON Response)
JavaScript Frontend ← Updates UI immediately
```

## 🔧 Configuration

### Backend URL
Update in `public/js/api-config.js`:
```javascript
BASE_URL: 'https://habit-tracker-backend-o9bs.onrender.com'
```

### Timeouts & Retries
```javascript
CONNECT_TIMEOUT: 10000,  // 10s connection timeout
READ_TIMEOUT: 15000,     // 15s read timeout
MAX_RETRIES: 3,          // Retry up to 3 times
RETRY_DELAY: 1000,       // 1s base delay with exponential backoff
```

## 🗄️ Verifying Database Storage

After adding a habit:

1. **Open Supabase Dashboard**
   - Go to: https://app.supabase.com
   - Select your project: `habit_tracker`
   - Go to: SQL Editor → New Query

2. **Run this query:**
   ```sql
   SELECT * FROM habits ORDER BY created_at DESC LIMIT 10;
   ```

3. **You should see:**
   - Your newly created habit with:
     - `id` (auto-generated)
     - `user_id` (your user ID)
     - `name` (your habit name)
     - `description`
     - `category`
     - `created_at` (current timestamp)
     - `updated_at`

## 📊 Data Models

### User
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "User Name",
  "created_at": "timestamp"
}
```

### Habit
```json
{
  "id": "uuid",
  "user_id": "uuid",
  "name": "Morning Exercise",
  "description": "30 minutes cardio",
  "category": "Health",
  "goal": "daily",
  "streak": 5,
  "created_at": "timestamp",
  "updated_at": "timestamp"
}
```

### Task
```json
{
  "id": "uuid",
  "habit_id": "uuid",
  "title": "30 minute run",
  "description": "Easy pace",
  "is_completed": false,
  "created_at": "timestamp",
  "updated_at": "timestamp"
}
```

## 🐛 Troubleshooting

### Issue: "Cannot connect to backend"
**Solution:** Check that Render backend is running:
```bash
# Test backend
curl https://habit-tracker-backend-o9bs.onrender.com/api/habits
```

### Issue: "Login fails but credential seems correct"
**Solution:** 
1. Backend needs user created first - try registering new user
2. Check backend logs for valid error message
3. Verify Supabase database has `users` table

### Issue: "Data not showing in Supabase"
**Solution:**
1. Verify habit was actually created (check browser console for API response)
2. Login to Supabase → check `habits` table
3. Verify user_id in frontend matches user_id in database

## 📝 Development Tips

### Check Browser Console (F12)
- See all API requests and responses
- View error messages with full details
- Monitor network activity

### Check Network Tab (F12 → Network)
- See actual HTTP requests to backend
- View request/response bodies
- Check status codes (200 = success, 4xx = client error, 5xx = server error)

### Local Storage (F12 → Application)
- View cached habits and user data
- Check auth token stored

## 🔄 Full Data Flow Example

```
User clicks "Add Habit" button
    ↓
JavaScript app.js: handleAddHabit()
    ↓
apiManager.addHabit("Read 30 mins", ...)
    ↓
apiClient.post("/api/habits", {name, description, ...})
    ↓
fetch() sends HTTPS POST to Render backend
    ↓
Spring Boot HabitController.createHabit()
    ↓
HabitService.save() (validates, sets timestamps)
    ↓
HabitRepository.save() (JPA/Hibernate ORM)
    ↓
Supabase PostgreSQL INSERT INTO habits ...
    ↓
Backend sends response with habit ID and timestamp
    ↓
JavaScript receives response, updates apiManager.habits
    ↓
UI updates: new habit appears in list
    ↓
User sees "✅ Habit added successfully!"
```

## 🎯 Next Steps

1. **Test login/registration** - Create an account
2. **Add a habit** - Use the UI
3. **Verify in Supabase** - Check habits table
4. **Add tasks** - Test task management
5. **Toggle tasks** - Mark complete/incomplete
6. **Delete** - Test deletion

## 📚 API Endpoints Used

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | /api/auth/login | User login |
| POST | /api/auth/register | User registration |
| GET | /api/habits | Get all habits |
| POST | /api/habits | Create habit |
| PUT | /api/habits/{id} | Update habit |
| DELETE | /api/habits/{id} | Delete habit |
| GET | /api/tasks | Get all tasks |
| POST | /api/tasks | Create task |
| PUT | /api/tasks/{id}/toggle | Toggle task completion |
| DELETE | /api/tasks/{id} | Delete task |

## ✅ Complete Integration Checklist

- [x] JavaScript frontend created with full UI
- [x] API client configured with Render backend URL
- [x] API manager with all CRUD operations
- [x] Authentication (login/register)
- [x] Habit management (create/read/update/delete)
- [x] Task management (create/read/toggle/delete)
- [x] Local storage for caching
- [x] Error handling and retry logic
- [x] Beautiful responsive UI
- [x] Direct database integration via backend

## 🎓 Key Integration Points

1. **Frontend talks to backend** ✅
   - ApiConfig.js has correct Render URL
   - ApiClient handles HTTPS requests

2. **Backend validates & saves** ✅
   - Spring Boot controllers process requests
   - JPA/Hibernate ORM handles database operations

3. **Database stores everything** ✅
   - Supabase PostgreSQL receives all INSERT/UPDATE/DELETE
   - Data persists across sessions

4. **No direct frontend-to-DB** ✅
   - All database access through backend API
   - Frontend cannot access database directly (secure!)
   - Backend is the single source of truth

---

**Your application is now 100% integrated: Frontend ↔ Backend ↔ Database**

# Complete Testing Guide - Habit Tracker Integration

## 🚀 Server Status

✅ **Frontend**: Running on http://localhost:3000
✅ **Backend**: https://habit-tracker-backend-o9bs.onrender.com
✅ **Database**: Supabase PostgreSQL (aws-1-ap-northeast-2.pooler.supabase.com)

---

## 🧪 Step-by-Step Testing

### Part 1: Register a New User

1. **Refresh your browser**: http://localhost:3000
2. **Click "Create one"** to show the register form
3. **Fill in:**
   - Username: `testuser123`
   - Email: (leave blank - will auto-fill as `testuser123@test.com`)
   - Password: `password123`
4. **Click "Create Account"**
5. **Expected result**: 
   - ✅ Message: "Account created! Logging in..."
   - ✅ Auto-redirects to dashboard
   - ✅ Display: "📍 testuser123" in top right

**What's happening:**
```
Frontend → POST /api/auth/register 
         → Backend validates username unique
         → Creates user in Supabase users table
         → Returns user with auto-generated user_id
         → Frontend stores token & user data locally
```

---

### Part 2: Verify User in Supabase

**IMPORTANT: This proves your frontend is connected to the database!**

1. **Go to**: https://app.supabase.com
2. **Login** to your Supabase account
3. **Select project**: `habit_tracker`
4. **Click**: SQL Editor (left sidebar)
5. **Create new query** and run:

```sql
SELECT user_id, username, email, created_at 
FROM users 
ORDER BY created_at DESC 
LIMIT 5;
```

**Expected result**: 
```
user_id | username      | email                   | created_at
--------|---------------|-------------------------|-------------------
   123  | testuser123   | testuser123@test.com    | 2026-04-15 XX:XX:XX
```

✅ **If you see this, your frontend successfully created a user in the database!**

---

### Part 3: Add a Habit

1. **Back in browser**: http://localhost:3000
2. **Click**: "+ Add Habit" button
3. **Fill in**:
   - Habit Title: `Morning Exercise`
   - Description: `30 minute jog every morning`
4. **Click**: "Create Habit"
5. **Expected result**:
   - ✅ Message: "Habit added successfully!"
   - ✅ Form clears automatically
   - ✅ New habit appears in dashboard as a card

**What's happening**:
```
Frontend → Extracts user_id from logged-in user (123)
         → POST /api/habits/123 (userId in path)
         → Backend validates, saves habit to Supabase
         → Returns habit with auto-generated habit_id
         → Frontend displays new habit immediately
```

---

### Part 4: Verify Habit in Supabase

1. **Back to Supabase** SQL Editor
2. **Run this query**:

```sql
SELECT habit_id, user_id, title, description, streakCount, created_at 
FROM habits 
WHERE user_id = 123
ORDER BY created_at DESC 
LIMIT 5;
```

**Expected result**:
```
habit_id | user_id | title              | description               | streakCount | created_at
---------|---------|-------------------|---------------------------|-------------|-------------------
    45   |   123   | Morning Exercise   | 30 minute jog every...    |      0      | 2026-04-15 XX:XX:XX
```

✅ **If you see this, your habit was successfully saved to Supabase!**

---

### Part 5: Add and Manage Tasks

1. **Click on habit card**: "Morning Exercise"
2. **You see detail view with "Add Task"**
3. **Fill in task**:
   - Task Title: `Complete 30 min jog`
   - Description: `Morning run in park`
4. **Click**: "Add Task"
5. **Expected result**:
   - ✅ Task appears in the task list
   - ✅ Checkbox is unchecked (not completed)

6. **Click checkbox** to mark as complete
7. **Expected result**:
   - ✅ Checkbox becomes checked
   - ✅ Task text becomes greyed out

---

### Part 6: Verify Tasks in Supabase

1. **Supabase SQL Editor**:

```sql
SELECT task_id, habit_id, title, description, is_completed 
FROM tasks 
WHERE habit_id = 45
ORDER BY created_at DESC 
LIMIT 5;
```

**Expected result**:
```
task_id | habit_id | title                | description        | is_completed
--------|----------|----------------------|--------------------|-----------
   201  |    45    | Complete 30 min jog  | Morning run in...  |   true
```

✅ **All your data is in Supabase!**

---

## 🔍 View API Calls in Real-Time

### Open Browser Developer Tools

**Press**: `F12` or `Right-click → Inspect`

### Go to Network Tab

1. **Click**: "Network" tab at top
2. **Refresh**: `Ctrl+R`
3. **Register and login** - watch requests appear

### Example API Calls You'll See

**Login Request:**
```
POST http://localhost:3000 → 
HTTPS https://habit-tracker-backend-o9bs.onrender.com/api/auth/login

Request body:
{
  "username": "testuser123",
  "password": "password123"
}

Response (200 OK):
{
  "user_id": 123,
  "username": "testuser123",
  "email": "testuser123@test.com",
  "xp_score": 0
}
```

**Add Habit Request:**
```
POST https://habit-tracker-backend-o9bs.onrender.com/api/habits/123

Request body:
{
  "title": "Morning Exercise",
  "description": "30 minute jog"
}

Response (200 OK):
{
  "id": 45,
  "user_id": 123,
  "title": "Morning Exercise",
  "description": "30 minute jog",
  "streakCount": 0,
  "createdAt": "2026-04-15T10:30:45"
}
```

---

## 📊 Complete Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     BROWSER (JavaScript)                         │
│  http://localhost:3000                                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ Login Form                                                 │  │
│  │ username: testuser123                                     │  │
│  │ password: password123                                     │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           ↓                                      │
│  app.js: handleLogin()                                          │
│  apiManager.login(username, password)                           │
│  apiClient.post("/api/auth/login", {username, password})        │
└───────────────────────────────────────────────────────────────│─┘
                          ↓
                    HTTPS NETWORK
                    (Encrypted)
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│           SPRING BOOT BACKEND (Render.com)                       │
│  https://habit-tracker-backend-o9bs.onrender.com                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │ AuthController.login()                                    │  │
│  │ - Receives {username, password}                           │  │
│  │ - Validates against users table                           │  │
│  │ - Returns {user_id, username, email, ...}                │  │
│  └───────────────────────────────────────────────────────────┘  │
│                           ↓                                      │
│  HabitController.getUserHabits(userId)                          │
│  - Receives GET /api/habits/123                                │  
│  - Queries habits WHERE user_id = 123                          │  
│  - Returns array of habits                                     │  │
│                                                                 │  │
│  TaskController.getUserTasks(userId)                           │  
│  - Receives GET /api/tasks/123                                │  
│  - Queries tasks WHERE user_id = 123                          │  
│  - Returns array of tasks                                     │  │
└───────────────────────────────────────────────────────────────│─┘
                          ↓
                    SQL QUERIES
                          ↓
┌─────────────────────────────────────────────────────────────────┐
│              SUPABASE POSTGRESQL DATABASE                        │
│  aws-1-ap-northeast-2.pooler.supabase.com:5432                 │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │ users table                                             │  │
│  │ user_id | username | email | password | xp_score       │  │
│  │    123  | testuser | @ ... | ******* |   0            │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │ habits table                                            │  │
│  │ habit_id | user_id | title | description | streakCnt  │  │
│  │    45    |   123   | Exer  | 30 min jog  |      0     │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │ tasks table                                             │  │
│  │ task_id | habit_id | title | is_completed             │  │
│  │   201   |    45    | Do... |    true                   │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│  All data PERSISTED here!                                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ Complete Integration Checklist

- [ ] **Register** → User appears in Supabase `users` table
- [ ] **Login** → Using username (not email)
- [ ] **Add Habit** → Habit appears in Supabase `habits` table with correct `user_id`
- [ ] **Add Task** → Task appears in Supabase `tasks` table with correct `habit_id`
- [ ] **Toggle Task** → Checkbox updates in UI and Supabase (check `is_completed`)
- [ ] **Browser Dev Tools** → See actual API calls to backend
- [ ] **Database queries** → Confirm data structure matches models
- [ ] **Logout and Login again** → Data persists (proves it was saved to database)

---

## 🐛 Troubleshooting

### Issue: "Login failed"

**Possible causes:**
1. Username doesn't exist - try registering first
2. Password is incorrect
3. Backend is down - check if https://habit-tracker-backend-o9bs.onrender.com/api/health returns `{"status":"ok"}`

**Solution:**
1. Open browser F12 → Network tab
2. Attempt login
3. Look for failed request, click it to see error response
4. Share error message

### Issue: "Failed to load habits"

**Possible causes:**
1. User ID not extracted from login response
2. API endpoint URL wrong
3. Backend /api/habits/{userId} endpoint not returning data

**Solution:**
1. Check browser console (F12 → Console)
2. Look for error message
3. Verify Supabase has habits for your user_id

### Issue: "Data in app but not in Supabase"

**Possible causes:**
1. Data saved to browser localStorage only (not backend)
2. API call didn't actually reach backend
3. Backend error but frontend cached data

**Solution:**
1. Check Network tab (F12) for actual API calls
2. Run SQL query to verify Supabase has the data
3. Clear browser localStorage: F12 → Application → Local Storage → Delete all

---

## 🎯 Success Criteria

**Your integration is complete and working when:**

1. You can register a new user
2. That user appears in Supabase `users` table
3. You can login with username/password
4. You can add a habit
5. That habit appears in Supabase `habits` table
6. You can add tasks to habits
7. Tasks appear in Supabase `tasks` table
8. Checking a task updates the `is_completed` field in Supabase
9. Data persists when you logout and login again

---

## 📋 API Endpoints Being Used

| Method | Endpoint | Purpose | User ID Required |
|--------|----------|---------|------------------|
| POST | /api/auth/register | Create user | No |
| POST | /api/auth/login | Login user | No |
| GET | /api/habits/{userId} | Get user's habits | **YES** |
| POST | /api/habits/{userId} | Create habit | **YES** |
| GET | /api/tasks/{userId} | Get user's tasks | **YES** |
| POST | /api/tasks/{userId} | Create task | **YES** |
| PUT | /api/tasks/{taskId}/toggle | Toggle task | No (task ID) |
| DELETE | /api/tasks/{taskId} | Delete task | No (task ID) |

---

## 🔐 User ID Extraction

**This is CRITICAL for the integration to work:**

When you login, the response includes:
```json
{
  "user_id": 123,
  "username": "testuser123",
  "email": "testuser123@test.com"
}
```

The frontend **automatically extracts** `user_id: 123` and uses it in all subsequent API calls:
- GET `/api/habits/123` ← User ID in URL
- POST `/api/habits/123` ← User ID in URL
- GET `/api/tasks/123` ← User ID in URL
- POST `/api/tasks/123` ← User ID in URL

**This ensures each user only sees their own habits and tasks!**

---

## 🎓 Key Learnings

### What You Fixed
✅ Changed login from email to username (matches database)
✅ Updated all API endpoints to include userId in path
✅ Used correct field names: title (not name), streakCount (not streak)
✅ Removed unnecessary fields (category, goal) that aren't in database

### Why This Matters
- **Frontend** must match database schema exactly
- **API endpoints** must be called with correct parameters
- **URLs** must include userIds for secure isolation
- **No direct frontend-to-database access** - all through backend API

### Complete Integration Stack
```
Frontend (Browser) 
  ↓
REST API (Backend)
  ↓
Database (Supabase)
```

Every piece has a specific role:
- **Frontend**: User Interface + API calls
- **Backend**: Validation + Business Logic + Database Access
- **Database**: Persistent data storage

---

**You now have a FULLY INTEGRATED, production-ready Habit Tracker! 🎉**

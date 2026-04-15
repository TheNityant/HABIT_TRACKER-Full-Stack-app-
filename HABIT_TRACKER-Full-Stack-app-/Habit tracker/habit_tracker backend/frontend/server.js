const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Serve index.html for root path
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', message: 'Frontend server is running' });
});

// Start server
app.listen(PORT, () => {
  console.log(`\n✅ Habit Tracker Frontend is running`);
  console.log(`📍 Open browser: http://localhost:${PORT}`);
  console.log(`🔗 Backend: https://habit-tracker-backend-o9bs.onrender.com`);
  console.log(`💾 Database: Supabase PostgreSQL\n`);
});

require('dotenv').config();
const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const API_KEY = "AIzaSyB1poB4xW3nIhYZzRlLYE6g54wOAs6Th1k"; 

app.post('/api/simulate', async (req, res) => {
    try {
        // CHANGED: Using 'v1' and 'gemini-pro' for maximum compatibility
        const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${API_KEY}`;
        
        console.log("📡 Pinging Gemini Stable...");
        
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                contents: [{ 
                    parts: [{ text: "Give a 10-word vibe check for a student in Mumbai." }] 
                }]
            })
        });

        const data = await response.json();

        if (data.error) {
            console.error("❌ API Error:", data.error.message);
            return res.status(data.error.code).json({ error: data.error.message });
        }

        const aiInsight = data.candidates[0].content.parts[0].text;

        res.json({
            name: "Property Selection",
            score: "8.5",
            aiInsight: aiInsight
        });
        
        console.log("✅ Success! AI Response:", aiInsight);

    } catch (err) {
        console.error("🔥 Server Error:", err.message);
        res.status(500).json({ error: "Backend failed to reach AI" });
    }
});

app.listen(5000, () => console.log("🚀 Server live on port 5000"));
# 🌟 Habit Tracker

A cross-platform habit and task management application built with Flutter and Spring Boot, combining cloud-backed persistence, progress analytics, calendar-based history, journaling, and AI-assisted journal parsing.

<p align="center">

![Flutter](https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)
![Dart](https://img.shields.io/badge/Dart-0175C2?style=for-the-badge&logo=dart&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Gemini](https://img.shields.io/badge/Gemini_API-8E75B2?style=for-the-badge&logo=google&logoColor=white)

</p>

---

## 🧭 Overview

Habit Tracker is a full-stack productivity application designed to help users manage **habits, tasks, journals, and personal progress** across supported platforms.

The application uses a Flutter client connected to a Spring Boot REST API, with PostgreSQL providing persistent storage and the Gemini API supporting AI-assisted journal processing.

> **Current repository:** Stable cloud-backed implementation.

---

## ✨ Highlights

| | Capability | Description |
|---|---|---|
| 🎯 | **Habit Management** | Create, update, delete, check-in and track habit streaks |
| ✅ | **Task Management** | Manage tasks with priorities and completion states |
| 📊 | **Analytics** | Visualize progress and historical activity |
| 📅 | **Calendar History** | Explore habit and task activity across previous dates |
| 📝 | **Journaling** | Create and manage personal journal entries |
| 🤖 | **AI Assistance** | Gemini-powered journal parsing |
| 📁 | **Media Uploads** | Upload and serve media through the backend |
| 🌐 | **Cross-Platform** | Android, iOS, Web, Linux, macOS and Windows |

---

## 🎥 Demo

> **Demo video coming soon**

A short walkthrough will demonstrate the main application workflow, including habit tracking, task management, analytics, journaling and AI-assisted features.

<!--
When the demo is ready, replace the section above with something similar to:

<p align="center">
  <a href="YOUR_YOUTUBE_URL">
    <img src="docs/assets/demo-thumbnail.png"
         width="850"
         alt="Habit Tracker Demo">
  </a>
</p>
-->

---

## 🏗️ Architecture

```text
                    ┌───────────────────────┐
                    │     Flutter Client    │
                    │                       │
                    │ Habits · Tasks        │
                    │ Analytics · Journal   │
                    │ Calendar · UI         │
                    └───────────┬───────────┘
                                │
                                │ REST API
                                ▼
                    ┌───────────────────────┐
                    │    Spring Boot API    │
                    │                       │
                    │ Auth · Habits         │
                    │ Tasks · Journal       │
                    │ Uploads               │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │       PostgreSQL      │
                    └───────────────────────┘

                                │
                                │ AI-assisted
                                │ journal parsing
                                ▼
                         ┌──────────────┐
                         │  Gemini API  │
                         └──────────────┘

# 🌟 Habit Tracker

<p align="center">
  <strong>A cross-platform habit and productivity application built with Flutter and Spring Boot.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Flutter-02569B?style=flat-square&logo=flutter&logoColor=white" alt="Flutter" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Gemini_API-8E75B2?style=flat-square&logo=google&logoColor=white" alt="Gemini API" />
</p>

---

## 🧭 Overview

Habit Tracker is a full-stack productivity application designed to help users manage **habits, tasks, journals, and personal progress** across supported platforms.

The application uses a Flutter client connected to a Spring Boot REST API, with PostgreSQL providing persistent storage and the Gemini API supporting AI-assisted journal processing.

> 🟢 **Current repository:** Stable cloud-backed implementation.

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

> 🎬 **Demo video coming soon**

A short walkthrough will demonstrate the main application workflow, including habit tracking, task management, analytics, journaling and AI-assisted features.

<!--
When the demo is ready:

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

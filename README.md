# Habit Tracker

A cross-platform habit and task management application built with Flutter and Spring Boot, combining cloud-backed persistence, progress analytics, calendar-based history, journaling, and AI-assisted journal parsing.

**Flutter · Dart · Spring Boot · Java · PostgreSQL · Gemini API**

---

## Overview

Habit Tracker is a full-stack productivity application designed to help users manage habits, tasks, and personal progress across supported platforms.

The application uses a Flutter client connected to a Spring Boot REST API backed by PostgreSQL. It also includes journaling functionality with optional AI-assisted parsing through the Gemini API.

The current repository represents a stable cloud-backed implementation of the application.

---

## Highlights

- Habit creation, editing, deletion, daily check-ins, and streak tracking
- Task management with priorities and completion states
- Calendar-based history and progress analytics
- Journal creation and management
- AI-assisted journal parsing through the Gemini API
- Media attachment and file upload support
- Cross-platform Flutter application
- Spring Boot REST API
- PostgreSQL persistence
- Hosted backend configuration for the Flutter client

---

## Demo

> 🎥 **Demo video coming soon**

A short walkthrough demonstrating the application's main workflows will be added here.

---

## Architecture

```text
┌─────────────────────────────────┐
│         Flutter Client          │
│                                 │
│ Habits · Tasks · Analytics      │
│ Journal · Calendar · UI         │
└───────────────┬─────────────────┘
                │
                │ REST API
                ▼
┌─────────────────────────────────┐
│        Spring Boot API          │
│                                 │
│ Auth · Habits · Tasks           │
│ Journal · Uploads               │
└───────────────┬─────────────────┘
                │
                ▼
┌─────────────────────────────────┐
│           PostgreSQL            │
└─────────────────────────────────┘

                │
                │ AI-assisted journal parsing
                ▼
         ┌──────────────┐
         │  Gemini API  │
         └──────────────┘

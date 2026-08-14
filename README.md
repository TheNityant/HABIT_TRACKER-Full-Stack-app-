# Habit Tracker

A cross-platform habit and task management application built with Flutter and Spring Boot, featuring cloud-backed persistence, progress analytics, calendar-based history, journaling, and AI-assisted journal parsing.

**Flutter · Dart · Spring Boot · Java · PostgreSQL · Gemini API**

[![Demo](https://img.shields.io/badge/Demo-Coming%20Soon-000000?style=for-the-badge)](#demo)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](#architecture)
[![Frontend](https://img.shields.io/badge/Frontend-Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)](#tech-stack)

---

## Overview

Habit Tracker is a full-stack productivity application designed to help users manage habits, tasks, and personal progress across supported platforms.

The application combines a Flutter client with a Spring Boot REST backend and PostgreSQL persistence. It also includes journal functionality with optional AI-assisted parsing through the Gemini API.

---

## Highlights

- Habit creation, editing, completion tracking, and streak management
- Task management with priorities and completion states
- Calendar-based history and progress analytics
- Personal journaling with AI-assisted parsing
- Media attachment support
- Cross-platform Flutter client
- RESTful Spring Boot backend
- PostgreSQL-backed persistence
- Cloud-hosted backend deployment

---

## Demo

A short walkthrough demonstrating the application's main workflows will be added here.

> 🎥 **Demo video coming soon**

---

## Architecture

```text
┌──────────────────────────────┐
│       Flutter Client         │
│                              │
│  Habits · Tasks · Analytics  │
│  Journal · Calendar · UI     │
└──────────────┬───────────────┘
               │
               │ REST API
               ▼
┌──────────────────────────────┐
│       Spring Boot API        │
│                              │
│ Auth · Habits · Tasks        │
│ Journal · Uploads            │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         PostgreSQL           │
└──────────────────────────────┘
               
               │
               │ Journal analysis
               ▼
        ┌──────────────┐
        │  Gemini API  │
        └──────────────┘

# Expense Tracker Application

### Important URLs
--**Presentation URL**: https://docs.google.com/presentation/d/1HGkOTeZ8ssMHM05R7zHwONliJYOdZfJ693VjlmuNRGQ/edit?usp=sharing

--
**Hosted Application URL**: https://expense-tracker-3pzv.onrender.com/

--
**Disclaimer**:  Application is setup to run with seed data on startup

A **Spring Boot–based Expense Tracker** that allows users to record, manage, analyze, and export personal expenses.  
The application follows clean backend design principles, includes **file-based persistence (no database)**, and is fully tested using **JUnit 5, Mockito, and Spring Boot Test**.

---

## Features

### Core Functionality
- Create, update, and delete expenses
- View all recorded expenses
- Persist data to a JSON file (no database required)
- Automatically initialize demo data on first run

### Expense Analytics
- Total expense across all records
- Total expenses grouped by category (case-insensitive)
- Daily spending trends (sorted chronologically)
- Highest spending category
- Lowest spending category
- Consolidated expense summary API

###  Export
- Export all expenses to an **Excel (.xlsx)** file
- Human-readable headers and formatted dates

### Testing
- Controller tests with mocked dependencies
- Service-layer business logic tests
- Repository tests using temporary files
- Excel export validation using Apache POI
- Full Spring context load verification

---




## Architecture & Design

The application follows a **layered architecture**:
Controller → Service → Repository → File (JSON)



---

### Why file-based storage?
- Assignment requirement avoids database usage
- Lightweight and portable
- Human-readable and easy to debug
- Enables fast local testing without setup

---


## Key Components Explained

### Expense Model
Represents a single expense record:
- `id`
- `category`
- `amount`
- `date`
- `note`

### Repository Layer
- File-based persistence using JSON
- Thread-safe read/write access
- Automatically generates unique IDs
- Loads demo data if file does not exist

### Service Layer
Responsible for:
- Input validation (no future dates, amount > 0)
- Calculating totals, trends, and summaries

### Controller Layer
- Exposes RESTful APIs
- Separates web and business logic
- Returns appropriate HTTP responses

---

## API Endpoints

### Create Expense
POST /api/expenses

### Update Expense
PUT /api/expenses/{id}

### Delete Expense
DELETE /api/expenses/{id}

### Get All Expenses
GET /api/expenses

### Get Expense Summary
GET /api/expenses/summary

### Export Expenses to Excel
GET /api/expenses/export/excel


## How to Run the Application

### Prerequisites
- Java 17+
- Gradle (or Gradle Wrapper)

### Run locally
./gradlew bootRun 

Application will start at: http://localhost:8080


### Run all tests
./gradlew test

### Run a specific test class (example)
./gradlew test --tests "com.saloni.expensetracker.service.ExpenseServiceTest"

# Employee Time Tracking & Management System

## 📌 Overview

A backend system for managing employee attendance, working hours, overtime, and administrative approvals.

## 🚀 Features

* User registration with admin approval
* JWT-based authentication & authorization
* Time in / Time out tracking
* Automatic break and overtime calculation
* User dashboard (total hours, overtime, working days)
* Admin monitoring of all employee records
* CSV export for reporting

## 🔐 Security

* Password encryption using BCrypt
* JWT token authentication
* Role-based access (USER / ADMIN)

## 🧰 Tech Stack

* Java 17
* Spring Boot
* Spring Security
* MariaDB
* JWT (jjwt)
* Maven

## 📡 API Highlights

### Auth

* POST `/api/auth/register`
* POST `/api/auth/login`

### Time Tracking

* POST `/api/time/in/{userId}`
* POST `/api/time/out/{userId}`
* GET `/api/time/dashboard/{userId}`

### Admin

* PUT `/api/admin/approve/{userId}`
* GET `/api/admin/records`

## 📊 Business Logic

* Standard working hours: 9 hours/day
* Automatic 1-hour break deduction
* Overtime calculated beyond 9 hours
* Every 9 hours of overtime deducts 1 hour

## ▶️ How to Run

1. Configure MariaDB in `application.yml`
2. Run the Spring Boot application
3. Use Postman to test endpoints

## 📌 Future Improvements

* Docker deployment
* Frontend dashboard (React)
* Pagination & filtering improvements
* Refresh token support

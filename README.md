# User_Management_Backend
Mo_Money_Assignment_Backend_Java_Springboot

# User Management System – Backend (Spring Boot)

## 📌 Overview
This is the **backend service** for the User Management System, developed using **Spring Boot**.  
It provides secure REST APIs for user management with **JWT-based authentication**, **role-based access control**, and **PostgreSQL** as the database.

The project is built as part of an **interview coding test submission**.

---

## 🛠 Tech Stack
- Java 21
- Spring Boot 4.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- Lombok
- Swagger / OpenAPI (Springdoc)

---

## 📦 Project Configuration
- **Group**: `com.project`
- **Artifact**: `user-management`
- **Java Version**: 21
- **Build Tool**: Gradle

---

## 🚀 Getting Started

### 1️⃣ Prerequisites
Make sure the following are installed on your machine:
- Java 21+
- Gradle (or use Gradle Wrapper)
- PostgreSQL
- IDE (IntelliJ IDEA recommended)

---

### 2️⃣ Database Setup
Create a PostgreSQL database:

```sql
CREATE DATABASE user_management_db;

Update your application.properties or application.yml:
spring.datasource.url=jdbc:postgresql://localhost:5432/user_management_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

./gradlew clean build
./gradlew bootRun

```
4️⃣ API Documentation (Swagger)

Once the application is running, open:
http://localhost:8080/swagger-ui.html

This provides:

All available APIs
Request/response schemas
JWT authorization support

🔐 Security

JWT-based authentication
Spring Security filter chain
Role-based authorization
Password encryption using BCrypt

```
src/main/java
 └── com.project
     ├── common
     ├── data
     ├── features
     ├── security
     ├── startup
```



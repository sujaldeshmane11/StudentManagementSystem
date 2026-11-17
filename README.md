# Student Management System

A web-based Student Management System developed using Java, Spring Boot, MySQL, Thymeleaf, HTML, CSS, and JavaScript.

This application helps manage student records, courses, and course enrollments through a simple and user-friendly interface.

## Features

- User Login and Authentication
- Dashboard
- Add, View, Edit, and Delete Students
- Add, View, Edit, and Delete Courses
- Student-Course Enrollment
- View Enrolled Students
- Enrollment Details
- Search and Pagination
- Form Validation
- Exception Handling
- Soft Delete functionality for Students and Courses

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend Programming |
| Spring Boot | Backend Framework |
| Spring Data JPA | Database Operations |
| Hibernate | ORM |
| MySQL | Database |
| Thymeleaf | Template Engine |
| HTML5 | Structure |
| CSS3 | Styling |
| JavaScript | Frontend Interaction |
| Bootstrap | Responsive UI |
| Maven | Build Tool |
| Git & GitHub | Version Control |

## Project Structure

```text
StudentManagementSystem/
│
├── src/main/java/
│   └── com/cwm/studentmanagement/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── exception/
│       ├── model/
│       ├── repository/
│       └── service/
│
├── src/main/resources/
│   ├── static/
│   │   └── css/
│   ├── templates/
│   └── application.properties
│
├── src/test/
├── pom.xml
└── README.md
```

## Prerequisites

Make sure you have installed:

- Java JDK
- MySQL Server
- Git
- IDE (VS Code / IntelliJ IDEA / Eclipse)

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/sujaldeshmane11/StudentManagementSystem.git
```

### 2. Open the Project

```bash
cd StudentManagementSystem
```

### 3. Create MySQL Database

```sql
CREATE DATABASE student_mgmt_db;
```

### 4. Configure Database Credentials

Set the following environment variables on your system:

```text
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Make sure your `application.properties` uses these environment variables.

### 5. Run the Application

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Or use Maven:

```bash
mvn spring-boot:run
```

### 6. Open in Browser

```text
http://localhost:8080
```

## Future Improvements

- Dashboard Analytics
- Restore Deleted Records
- Improved Security
- Additional Reporting Features

## Author

**Sujal Deshmane**

Java Full Stack Developer (Fresher)

GitHub: [sujaldeshmane11](https://github.com/sujaldeshmane11)

Project Repository: [Student Management System](https://github.com/sujaldeshmane11/StudentManagementSystem)

---

⭐ If you find this project useful, feel free to explore the repository.
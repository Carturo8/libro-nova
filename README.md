# LibroNova - Library Management System

LibroNova is a comprehensive, console-based library management system built with Java. It provides essential functionalities for managing books, members, and loans within a library environment. The application is designed with a layered architecture for clarity, maintainability, and separation of concerns.

## ✨ Features

- **Book Management**: Full CRUD (Create, Read, Update, Delete) operations for books.
- **Stock Control**: Automatically tracks total and available copies of each book.
- **Member Management**: Manages library members and their status.
- **Loan System**: Handles book check-outs and returns.
- **Fine Calculation**: Automatically calculates and applies fines for overdue loans.
- **Robust Search**: Search for books by title or retrieve them by ISBN.
- **User Authentication**: A basic authentication layer to control access.
- **Console-Based UI**: A clean and interactive command-line interface for all operations.

## 🛠️ Technology Stack

- **Backend**: Java 21
- **Database**: PostgreSQL
- **Build & Dependency Management**: Apache Maven
- **Database Connectivity**: JDBC (Java Database Connectivity)
- **Testing**: JUnit 5
- **Logging**: SLF4J with Logback

## 📂 Project Structure

The project follows a classic layered architecture to ensure a clean separation of concerns:

 ```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── libronova/
│   │           ├── app/           # Main application entry point
│   │           ├── config/        # Database and environment configuration
│   │           ├── controller/    # Controllers managing application flow
│   │           ├── dao/           # Data Access Object layer (interfaces and JDBC implementations)
│   │           ├── errors/        # Custom exception classes
│   │           ├── model/         # Domain objects (Book, Loan, Member, etc.)
│   │           ├── service/       # Business logic layer
│   │           └── view/          # Console-based user interface
│   └── resources/
│       ├── sql/                   # Database schema and seed data
│       │   ├── schema.sql
│       │   └── seed.sql
│       ├── db.properties          # Database connection properties
│       ├── db.properties.example  # Example configuration file
│       └── logback.xml            # Logging configuration
├── target/                        # Compiled output and build artifacts
├── .gitignore
├── pom.xml                        # Maven project descriptor
└── README.md
 ```

- **`view`**: Responsible for presenting data to the user and interpreting user commands.
- **`service`**: Contains the core business logic, orchestrating calls to the DAO layer.
- **`dao`**: Handles all database interactions using JDBC. It abstracts the SQL queries from the rest of the application.
- **`model`**: Plain Old Java Objects (POJOs) that represent the application's entities.
- **`config`**: Manages the database connection details and provides connections to the DAOs.

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- **Java Development Kit (JDK)**: Version 21 or higher.
- **Apache Maven**: To build the project and manage dependencies.
- **PostgreSQL**: A running instance of a PostgreSQL database.

### 1. Clone the Repository

 ```bash
 git clone <your-repository-url>
 cd libro-nova
 ```

### 2. Database Setup

You need to have a PostgreSQL database and a user with privileges on it. The application will look for a database named `libronova_db` by default.

1.  Connect to your PostgreSQL instance.
2.  Create the database:
    ```sql
    CREATE DATABASE libronova_db;
    ```
3.  Run the SQL scripts located in the `database/` directory (you should create this directory and add your `.sql` files) to set up the required tables (`books`, `members`, `users`, `loans`, etc.).

### 3. Configure Database Connection

Open the `DatabaseConfig.java` file and update the connection details to match your local PostgreSQL setup:

 ```java
 // src/main/java/com/libronova/config/DatabaseConfig.java
 
 private static final String URL = "jdbc:postgresql://localhost:5432/libronova_db";
 private static final String USER = "your_postgres_user";
 private static final String PASSWORD = "your_postgres_password";
 ```

### 4. Build and Run the Application

1.  Use Maven to compile the project and install dependencies:
    ```bash
    mvn clean install
    ```

2.  Run the application from your IDE by executing the `main` method in `com.libronova.app.Main`.

    Alternatively, you can run it via the command line after building:
    ```bash
    mvn exec:java -Dexec.mainClass="com.libronova.app.Main"
    ```
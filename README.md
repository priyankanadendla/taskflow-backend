# TaskFlow Pro — Backend

A RESTful backend application for **TaskFlow Pro**, a task management web application. Built using Java and Spring Boot, with REST APIs to create, view, update, and delete tasks.

## 🚀 Live Application

- **Frontend:** https://taskflow-pro-sandy-eta.vercel.app/
- **Backend API:** https://taskflow-backend-wllt.onrender.com/api/tasks

## 🛠️ Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Database:** H2 Database
- **Data Access:** Spring Data JPA
- **Build Tool:** Maven
- **Architecture:** REST API
- **Deployment:** Render

## ✨ Features

- Create new tasks
- Retrieve all tasks
- Update existing tasks
- Delete tasks
- Mark tasks as completed
- Store task descriptions and details
- Support task categories and priorities
- Configure CORS for frontend-backend communication
- Validate task input

## 📌 API Endpoints

Base URL:

`https://taskflow-backend-wllt.onrender.com/api/tasks`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | Retrieve all tasks |
| POST | `/api/tasks` | Create a task |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task |

## ⚙️ Run Locally

### Prerequisites

- Java 21 or compatible configured JDK
- Maven
- Git

### 1. Clone the repository

```bash
git clone https://github.com/priyankanadendla/taskflow-backend.git
```

### 2. Navigate to the project

```bash
cd taskflow-backend
```

### 3. Run the application

If Maven is installed:

```bash
mvn spring-boot:run
```

Or, if the Maven wrapper is available:

**Windows**
```powershell
.\mvnw.cmd spring-boot:run
```

The backend will typically run at:

`http://localhost:8080`

## 🧪 Testing the API

Retrieve all tasks:

```bash
curl http://localhost:8080/api/tasks
```

Create a task:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Learn Spring Boot\",\"description\":\"Practice REST APIs\",\"completed\":false}"
```

## 🌐 Deployment

The backend is deployed on Render and communicates with the React frontend hosted on Vercel.

The server port is configured using the `PORT` environment variable to support deployment environments.

## 👩‍💻 Author

**Priyanka Nadendla**

GitHub: [@priyankanadendla](https://github.com/priyankanadendla)

---

⭐ If you find this project useful, feel free to explore the repository and the live application.

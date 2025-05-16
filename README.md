# Translations Application

A Spring Boot application to manage and test translations with JWT-based authentication and pre-seeded data for testing.

---

## 🚀 1. Setup and Run the Application

### Prerequisites

- Docker must be installed and running.
- Ensure the following ports are **not in use**:
    - `8082` → H2 Console
    - `8086` → Swagger UI
    - `9092` → H2 Database (TCP mode)

---

### Step 1: Clone the Repository

```bash
git clone https://github.com/Mkkhatri713/translations.git
cd translations
```

---

### Step 2: Build the Docker Image

In the project root directory, run:

you will see the logs and test cases run while making build 
```bash
docker build -t translations-app .
```
---

### Step 3: Run the Application Using Docker Compose

To start the app and all required services, run:

```bash
docker compose up
```

Once started, the logs will display the application boot process and data seeding output.

---

## 🛢️ 2. Access the Database Console (H2)

Open your browser and navigate to:

```
http://localhost:8082
```

Use the following settings to log in:

| Field       | Value                                           |
|-------------|-------------------------------------------------|
| JDBC URL    | jdbc:h2:tcp://h2-db:9092/mem:translationdb      |
| User Name   | *(leave empty)*                                 |
| Password    | *(leave empty)*                                 |

---

## 📘 3. Explore the API Using Swagger UI

Open Swagger UI in your browser:

```
http://localhost:8086/swagger-ui/index.html#/
```

---

## 🔐 4. Authentication and Authorization (JWT)

Use the following credentials to obtain a JWT token:

| Username | Password  |
|----------|-----------|
| admin    | 12345678  |

Steps:

1. Use the `/auth/login` endpoint to log in and receive a token.
2. Copy the token from the response.
3. Click the **Authorize** button (top right in Swagger UI).
4. Paste the token into the popup and click **Authorize**.

You are now authorized to call secured APIs.

---

## 📊 5. Seeded Data

- An **admin** user is created automatically every time the app starts.
- The application seeds **100,000 transaction records** at startup for testing.
- Seeding logic is located in:

```
/util/DataSeeder.java
```

---

## 🛠️ 6. Troubleshooting

- ✅ Ensure Docker is running before executing any command.
- 🔌 If services fail to start, check if required ports are free.
- 🌐 If the H2 Console doesn’t connect, double-check the JDBC URL and port mappings.
- 🔧 You can change port bindings in `docker-compose.yml` if needed.

---

## 📎 Additional Info

- GitHub Repository: [https://github.com/Mkkhatri713/translations](https://github.com/Mkkhatri713/translations)

# Spring Boot User CRUD

Simple CRUD API for `User` with PostgreSQL using Spring Boot.

## Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL running locally

## Database

Create a database:

```sql
CREATE DATABASE userdb;
```

Default credentials in `src/main/resources/application.properties`:

- username: `postgres`
- password: `postgres`

Adjust them if your local PostgreSQL setup is different.

## Run

```bash
mvn spring-boot:run
```

## Endpoints

- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`

Example payload:

```json
{
  "name": "Maria Silva",
  "cellPhone": "+55 11 99999-9999"
}
```

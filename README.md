# Spring Boot User CRUD

Simple CRUD API for `User` with PostgreSQL using Spring Boot.

## Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL running locally, or Docker Desktop with Docker Compose

## Database

Create a database:

```sql
CREATE DATABASE userdb;
```

Default credentials in `src/main/resources/application.properties`:

- username: `postgres`
- password: `root`

Adjust them if your local PostgreSQL setup is different.

## Run

Using local Java/PostgreSQL:

```bash
mvn spring-boot:run
```

Using Docker:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080` and PostgreSQL at `localhost:5432`.

To stop the stack:

```bash
docker compose down
```

To stop it and remove the database volume:

```bash
docker compose down -v
```

## Endpoints

- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`

## Authorization

The API now uses HTTP Basic authentication with two access levels:

- `reader` / `reader123`: can call `GET /users` and `GET /users/{id}`
- `admin` / `admin123`: can call all endpoints, including create, update, and delete

Example requests:

```bash
curl -u reader:reader123 http://localhost:8080/users
curl -u admin:admin123 -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Maria Silva\",\"cellPhone\":\"+55 11 99999-9999\"}"
```

Change the credentials in `src/main/resources/application.properties` before using this outside local development.

## Environment Variables

The app now accepts runtime overrides for containerized deployments:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SPRING_JPA_SHOW_SQL`
- `APP_SECURITY_READER_USERNAME`
- `APP_SECURITY_READER_PASSWORD`
- `APP_SECURITY_WRITER_USERNAME`
- `APP_SECURITY_WRITER_PASSWORD`

## Notes

- The project requires Java 21 to build and test.
- If `mvn test` fails with `UnsupportedClassVersionError`, your shell is using an older JDK and needs to be pointed at Java 21.

Example payload:

```json
{
  "name": "Maria Silva",
  "cellPhone": "+55 11 99999-9999"
}
```

# tcc-api

Backend API for the TCC evaluation system.

Requirements:
- Java 25
- Maven
- MySQL

Environment variables: see `.env.example`.

Run (example):

```
mvn clean test
mvn spring-boot:run
```

Notes:
- Replace `src/main/resources/db/migration/V1__initial_schema.sql` with the canonical `V1__initial_schema.sql` provided for the project before running Flyway.

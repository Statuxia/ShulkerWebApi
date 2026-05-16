# ShulkerWebApi

An API for Minecraft servers with authentication, economy and moderation. Suitable for plugins, profile sites and Discord bots. Includes several features out of the box:
- **Authorization and registration** — Discord OAuth2, tokens with granular access rights, game sessions validation by IP and nickname, rate limiting
- **Banking system** — personal and group cards, transfers, operation history, PIN-codes, customization
- **Fines** — issuance, payment, editing, history
- **Accounts** — linking game and Discord accounts, profile editing
- **Metrics** — Prometheus integration for API observability

## Installation

### 1. Clone repository

```bash
git clone https://github.com/statuxia/ShulkerWebApi.git
cd ShulkerWebApi
```

### 2. Set up a database

Create PostgreSQL database and fill connection properties in `application.properties`:

```properties
spring.datasource.url=${db.protocol}://${db.host}:${db.port}/${db.database}
spring.datasource.username=${db.username}
spring.datasource.password=${db.password}
```

Liquibase will apply all migrations automatically on first run

### 3. Set up Discord OAuth2

Create application on [Discord Developer Portal](https://discord.com/developers/applications) and fill properties in `application.properties`:

```properties
discord.oauth.clientId=YOUR_CLIENT_ID
discord.oauth.clientSecret=YOUR_CLIENT_SECRET
discord.oauth.redirect-uri=http://localhost:8080/api/v1/oauth2/discord/auth
```

### 4. Run

```bash
./gradlew bootRun
```

After startup:
- Swagger UI: `http://localhost:8080/docs`
- Prometheus metrics: `http://localhost:8080/actuator/prometheus`

## For developers

### Running tests

Tests use TestContainers — Docker must be running.

```bash
./gradlew test
```

### Code quality check

```bash
./gradlew checkstyle
./gradlew pmd
./gradlew jacocoTestReport
```

## Found a bug or have a suggestion?

For bugs: [issue](https://github.com/statuxia/ShulkerWebApi/issues) with problem description
For fixes: issue, then pull request with link

## License

Project distributed under a custom license. For details — file [LICENSE](LICENSE).

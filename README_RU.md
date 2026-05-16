# ShulkerWebApi

API для Minecraft-серверов с системой авторизации, экономикой и модерацией. Подходит для плагинов, сайтов с профилем, и Discord-ботов. Включает несколько фич из коробки:
- **Авторизация и регистрация** — Discord OAuth2, токены с гранулярными правами доступа, валидация игровых сессий по IP и нику, rate limiting
- **Банковская система** — личные и групповые карты, переводы, история операций, PIN-коды, кастомизация
- **Штрафы** — выдача, оплата, редактирование, история
- **Аккаунты** — привязка игровых и Discord-аккаунтов, управление профилями
- **Метрики** — связка с prometheus для наблюдения за работоспособностью API

## Установка

### 1. Клонировать репозиторий

```bash
git clone https://github.com/statuxia/ShulkerWebApi.git
cd ShulkerWebApi
```

### 2. Настроить базу данных

Создать базу данных PostgreSQL и заполнить параметры подключения в `application.properties`:

```properties
spring.datasource.url=${db.protocol}://${db.host}:${db.port}/${db.database}
spring.datasource.username=${db.username}
spring.datasource.password=${db.password}
```

При первом запуске Liquibase применит все миграции автоматически для подготовки БД к работе.

### 3. Настроить Discord OAuth2

Создать приложение на [Discord Developer Portal](https://discord.com/developers/applications) и заполнить в `application.properties`:

```properties
discord.oauth.clientId=YOUR_CLIENT_ID
discord.oauth.clientSecret=YOUR_CLIENT_SECRET
discord.oauth.redirect-uri=http://localhost:8080/api/v1/oauth2/discord/auth
```

### 4. Запустить

```bash
./gradlew bootRun
```

После запуска:
- Swagger UI: `http://localhost:8080/docs`
- Prometheus метрики: `http://localhost:8080/actuator/prometheus`

## Для разработчиков

### Запуск тестов

Тесты используют TestContainers — Docker должен быть запущен.

```bash
./gradlew test
```

### Проверка качества кода

```bash
./gradlew checkstyle
./gradlew pmd
./gradlew jacocoTestReport
```

## Если баг или предложение

Для багов: [issue](https://github.com/statuxia/ShulkerWebApi/issues) с описанием проблемы.
Для готовых исправлений: issue, затем pull request со ссылкой на него.

## Лицензия

Проект распространяется под кастомной лицензией. Подробнее — в файле [LICENSE](LICENSE).

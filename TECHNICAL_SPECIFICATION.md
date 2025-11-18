# Техническое задание: TODO API (Упрощенная версия)

## 1. Описание проекта
Простой REST API для управления списком задач (TODO). Базовый CRUD функционал без аутентификации.

## 2. Технологический стек
- **Язык**: Kotlin
- **Фреймворк**: Spring Boot 3.3.4
- **База данных**: PostgreSQL
- **ORM**: Spring Data JPA
- **Валидация**: Spring Validation

## 3. Модель данных

### Сущность Todo (Задача)
```
Todo:
  - id: Long (PK, auto-generated)
  - title: String (not null, 1-100 символов)
  - description: String (nullable, до 500 символов)
  - completed: Boolean (default false)
  - priority: String (LOW, MEDIUM, HIGH, default MEDIUM)
  - createdAt: LocalDateTime (auto)
  - updatedAt: LocalDateTime (auto)
```

## 4. REST API Endpoints

### Основные операции
- `GET /api/todos` - Получить список всех задач
- `GET /api/todos/{id}` - Получить задачу по ID
- `POST /api/todos` - Создать новую задачу
- `PUT /api/todos/{id}` - Обновить задачу полностью
- `PATCH /api/todos/{id}` - Частичное обновление задачи
- `DELETE /api/todos/{id}` - Удалить задачу

### Дополнительные операции
- `PATCH /api/todos/{id}/complete` - Отметить задачу выполненной
- `PATCH /api/todos/{id}/incomplete` - Отметить задачу невыполненной
- `GET /api/todos/completed` - Получить только выполненные задачи
- `GET /api/todos/pending` - Получить только невыполненные задачи

## 5. План поэтапной реализации

### Этап 1: Создание Entity и Repository
1. Создать класс `Todo` с аннотациями JPA
2. Создать `TodoRepository` интерфейс
3. Настроить автоматическое создание таблицы в БД

### Этап 2: Создание DTO
1. Создать `CreateTodoRequest` - для создания задачи
2. Создать `UpdateTodoRequest` - для обновления задачи
3. Создать `TodoResponse` - для ответа клиенту

### Этап 3: Создание Service слоя
1. Создать `TodoService` класс
2. Реализовать методы:
   - `getAllTodos()`
   - `getTodoById(id)`
   - `createTodo(request)`
   - `updateTodo(id, request)`
   - `deleteTodo(id)`
   - `markAsComplete(id)`

### Этап 4: Создание Controller
1. Создать `TodoController` с маппингом `/api/todos`
2. Реализовать все endpoints
3. Добавить базовую валидацию входных данных

### Этап 5: Обработка ошибок
1. Создать custom исключения (TodoNotFoundException)
2. Создать GlobalExceptionHandler
3. Настроить корректные HTTP статусы ответов

### Этап 6: Тестирование
1. Написать unit тесты для Service
2. Написать интеграционные тесты для Controller
3. Протестировать через Postman/curl

## 6. Структура пакетов
```
com.example.todoapi/
├── controller/
│   └── TodoController.kt
├── dto/
│   ├── CreateTodoRequest.kt
│   ├── UpdateTodoRequest.kt
│   └── TodoResponse.kt
├── entity/
│   └── Todo.kt
├── exception/
│   ├── TodoNotFoundException.kt
│   └── GlobalExceptionHandler.kt
├── repository/
│   └── TodoRepository.kt
├── service/
│   └── TodoService.kt
└── TodoApiApplication.kt
```

## 7. Примеры запросов и ответов

### Создание задачи
**Request:**
```json
POST /api/todos
{
  "title": "Изучить Spring Data JPA",
  "description": "Разобраться с основными аннотациями",
  "priority": "HIGH"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Изучить Spring Data JPA",
  "description": "Разобраться с основными аннотациями",
  "completed": false,
  "priority": "HIGH",
  "createdAt": "2024-11-18T10:00:00",
  "updatedAt": "2024-11-18T10:00:00"
}
```

### Получение всех задач
**Request:**
```
GET /api/todos
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Изучить Spring Data JPA",
    "description": "Разобраться с основными аннотациями",
    "completed": false,
    "priority": "HIGH",
    "createdAt": "2024-11-18T10:00:00",
    "updatedAt": "2024-11-18T10:00:00"
  },
  {
    "id": 2,
    "title": "Написать unit тесты",
    "description": null,
    "completed": true,
    "priority": "MEDIUM",
    "createdAt": "2024-11-18T11:00:00",
    "updatedAt": "2024-11-18T12:00:00"
  }
]
```

### Обновление задачи
**Request:**
```json
PUT /api/todos/1
{
  "title": "Изучить Spring Data JPA и Hibernate",
  "description": "Разобраться с аннотациями и настройками",
  "priority": "HIGH",
  "completed": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Изучить Spring Data JPA и Hibernate",
  "description": "Разобраться с аннотациями и настройками",
  "completed": true,
  "priority": "HIGH",
  "createdAt": "2024-11-18T10:00:00",
  "updatedAt": "2024-11-18T13:00:00"
}
```

### Отметить как выполненную
**Request:**
```
PATCH /api/todos/1/complete
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Изучить Spring Data JPA",
  "description": "Разобраться с основными аннотациями",
  "completed": true,
  "priority": "HIGH",
  "createdAt": "2024-11-18T10:00:00",
  "updatedAt": "2024-11-18T13:30:00"
}
```

### Удаление задачи
**Request:**
```
DELETE /api/todos/1
```

**Response (204 No Content):**
```
(пустой ответ)
```

## 8. Валидация

### При создании задачи:
- `title` - обязательное поле, от 1 до 100 символов
- `description` - опциональное, максимум 500 символов
- `priority` - если указан, должен быть LOW, MEDIUM или HIGH

### При обновлении задачи:
- Те же правила, что и при создании
- Все поля опциональные при PATCH запросе

## 9. Обработка ошибок

### 400 Bad Request - Ошибка валидации
```json
{
  "timestamp": "2024-11-18T10:00:00",
  "status": 400,
  "error": "Validation failed",
  "message": "Title must not be empty",
  "path": "/api/todos"
}
```

### 404 Not Found - Задача не найдена
```json
{
  "timestamp": "2024-11-18T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Todo with id 999 not found",
  "path": "/api/todos/999"
}
```

## 10. Конфигурация application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/todo_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

## Примечания для разработки

1. Начните с самого простого - создайте Entity и Repository
2. Проверяйте каждый этап через Spring Boot DevTools и отладку
3. Используйте Postman для тестирования endpoints
4. Не усложняйте на первом этапе - сначала заставьте работать базовый функционал
5. Spring Security добавите позже, когда освоите базовые операции
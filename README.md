# 💰 Expense Tracker API

A robust REST API for managing personal expenses built with Spring Boot, JPA, and PostgreSQL. This project demonstrates modern backend development practices including validation, logging, testing, and comprehensive API documentation.

> **Note**: This is the backend-only repository. The frontend React application is maintained in a separate repository for better separation of concerns and independent deployment.

## 🚀 Features

- **CRUD Operations**: Create, Read, Update, and Delete expense records
- **Data Validation**: Comprehensive input validation with detailed error messages
- **Professional Logging**: Structured logging with SLF4J and Logback
- **Comprehensive Testing**: Unit tests with Mockito and integration tests with H2 database
- **API Documentation**: Custom JSON-based API documentation endpoint
- **Error Handling**: Global exception handling with structured error responses
- **Database Integration**: PostgreSQL with JPA/Hibernate ORM

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL 17.2
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Validation API
- **Logging**: SLF4J with Logback
- **Testing**: JUnit 5, Mockito, TestRestTemplate
- **Build Tool**: Maven
- **Java Version**: 21

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 17.2
- Git

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/expense-tracker.git
cd expense-tracker
```

### 2. Database Setup
Create a PostgreSQL database and update the connection details in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Welcome message |
| GET | `/all` | Get all expenses |
| POST | `/add` | Create new expense |
| PUT | `/updateExpense` | Update existing expense |
| DELETE | `/delete/{id}` | Delete expense by ID |
| GET | `/api-docs` | API documentation |

### API Documentation
Access the comprehensive API documentation at:
```
http://localhost:8080/api-docs
```

This endpoint returns detailed JSON documentation including:
- All available endpoints
- Request/response formats
- Data model specifications
- Validation rules
- Example requests

## 📝 API Usage Examples

### Create an Expense
```bash
curl -X POST http://localhost:8080/add \
  -H "Content-Type: application/json" \
  -d '{
    "expense": "Groceries",
    "expenseType": "Food",
    "expenseAmount": "50.00"
  }'
```

### Get All Expenses
```bash
curl http://localhost:8080/all
```

### Update an Expense
```bash
curl -X PUT http://localhost:8080/updateExpense \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "expense": "Updated Groceries",
    "expenseType": "Food",
    "expenseAmount": "75.00"
  }'
```

### Delete an Expense
```bash
curl -X DELETE http://localhost:8080/delete/1
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/org/learnspring/expensetracker/
│   │   ├── Controllers/
│   │   │   ├── HomeController.java          # REST API endpoints
│   │   │   └── GlobalExceptionHandler.java  # Global error handling
│   │   ├── Model/
│   │   │   └── Expense.java                 # JPA entity
│   │   ├── Service/
│   │   │   └── expenseService.java          # Business logic
│   │   ├── repo/
│   │   │   └── expenseRepo.java             # Data access layer
│   │   └── ExpenseTrackerApplication.java   # Main application class
│   └── resources/
│       └── application.properties           # Configuration
└── test/
    ├── java/org/learnspring/expensetracker/
    │   ├── Controllers/
    │   │   └── HomeControllerIntegrationTest.java  # Integration tests
    │   └── Service/
    │       └── ExpenseServiceTest.java             # Unit tests
    └── resources/
        └── application-test.properties             # Test configuration
```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Test Coverage
- **Unit Tests**: Service layer with Mockito
- **Integration Tests**: REST API endpoints with TestRestTemplate
- **Test Database**: H2 in-memory database for isolated testing

### Test Structure
- **ExpenseServiceTest**: Unit tests for business logic
- **HomeControllerIntegrationTest**: Integration tests for API endpoints
- **Test Configuration**: Separate test properties with H2 database

## 📊 Data Model

### Expense Entity
```java
public class Expense {
    private int id;                    // Auto-generated primary key
    private String expense;            // Expense name/description
    private String expenseType;        // Category/type
    private String expenseAmount;      // Amount as string
}
```

### Validation Rules
- **expense**: Required, max 100 characters
- **expenseType**: Required, max 50 characters  
- **expenseAmount**: Required, max 20 characters

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=0000
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update

# Logging Configuration
logging.level.org.learnspring.expensetracker=DEBUG
logging.file.name=logs/expense-tracker.log
```

## 📈 Logging

The application uses structured logging with different levels:
- **INFO**: Business operations (CRUD actions)
- **DEBUG**: Detailed operation information
- **WARN**: Validation errors and warnings
- **ERROR**: System errors and exceptions

Logs are written to both console and file (`logs/expense-tracker.log`).

## 🚨 Error Handling

The application includes comprehensive error handling:
- **Validation Errors**: Detailed field-level validation messages
- **Global Exception Handler**: Consistent error response format
- **HTTP Status Codes**: Proper status codes for different scenarios

### Error Response Format
```json
{
  "fieldName": "Error message",
  "anotherField": "Another error message"
}
```

## 🎯 Key Learning Outcomes

This project demonstrates:
- **Spring Boot Best Practices**: Proper project structure and configuration
- **REST API Design**: Clean, RESTful endpoint design
- **Data Validation**: Input validation with detailed error messages
- **Professional Logging**: Structured logging for production applications
- **Comprehensive Testing**: Both unit and integration testing strategies
- **Error Handling**: Global exception handling and consistent error responses
- **API Documentation**: Custom documentation solution
- **Database Integration**: JPA/Hibernate with PostgreSQL

## 🌐 Frontend Integration

This backend API is designed to work with the separate Expense Tracker frontend application. The frontend repository includes:

- **React + TypeScript** application
- **Modern UI** with Tailwind CSS
- **Authentication** integration
- **Real-time dashboard** with charts and analytics

### CORS Configuration

The backend is configured to accept requests from:
- `http://localhost:5173` (frontend development)
- `http://localhost:3000` (alternative frontend port)
- `https://moneyfind.netlify.app` (production frontend)

To add additional frontend domains, update the CORS configuration in `src/main/java/org/learnspring/expensetracker/config/CorsConfig.java`.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

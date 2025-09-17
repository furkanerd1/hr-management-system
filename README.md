
# HR Management System REST API

This project is a Spring Boot-based HR Management System backend service, built as a personal learning and growth project. It follows best practices such as JWT authentication, role-based authorization, DTO-based architecture, and consistent RESTful API design. The system covers core HR operations including employee, department, position, salary, attendance, performance review, leave request, announcements, and notifications management.

## Key Principles Implemented
- **Security**: JWT-based authentication, hierarchical role-based authorization (HR > Manager > Employee), and secure password policies.
- **Clean Architecture**: DTO-based request/response models, layered service design, and separation of concerns.
- **RESTful API**: Consistent response structure, centralized exception handling, and detailed Swagger/OpenAPI documentation.
- **Scalability**: Modular design with reusable components to support future extensions (e.g., payroll, reporting).
- **Best Practices**: Meaningful validations, logging, and testable service implementations.
## Features
- **Employee Management**: Create, update, and manage employee profiles with role-based access.
- **Department & Position Management**: Organize employees into departments and positions.
- **Salary Management**: Track employee salaries, bonuses, and salary history.
- **Attendance Tracking**: Check-in/check-out system and attendance history.
- **Performance Reviews**: Record and evaluate employee performance.
- **Leave Requests**: Submit, approve, or reject leave requests with balance tracking.
- **Announcements & Notifications**: Share company-wide updates and send personal notifications.
- **Authentication & Authorization**: Secure login, JWT tokens, and role-based permissions.
## Documentation

- [**Requirements Analysis**](https://github.com/furkanerd1/hr-management-system/wiki/Requirements-Analysis) – Business needs and functional requirements.
- [**Detailed Requirements & Access Control**](https://github.com/furkanerd1/hr-management-system/wiki/Detailed-Requirements-&-Access-Control) – User roles, permissions, and authorization matrix.
- [**Data Model Analysis**](https://github.com/furkanerd1/hr-management-system/wiki/Data-Model-Analysis) – Database schema and entity relationships.
- [**Technical Analysis & API Documentation**](https://github.com/furkanerd1/hr-management-system/wiki/Technical-Analysis-%E2%80%90-API-Documentation) – REST API endpoints and usage details.
## Project Structure

```text
com.furkanerd.hr_management_system
├── config            # Security and Swagger config classes
├── constants         # Constant values used across the project
├── controller        # REST API controllers handling HTTP requests
├── exception
│   ├── base          # Base classes for all custom exceptions
│   ├── custom        # Specific custom exception classes
│   └── handler       # Global exception handlers (e.g., @ControllerAdvice)
├── mapper            # MapStruct mapper interfaces for DTO <-> Entity conversion
├── model
│   ├── dto
│   │   ├── request   # DTO classes for API requests
│   │   └── response  # DTO classes for API responses
│   ├── entity        # JPA entity classes mapping to database tables
│   └── enums         # Enum types used in entities and DTOs
├── repository        # Spring Data JPA repositories for data access
├── security          # JWT authentication, role-based authorization, and security 
├── service
│   ├── employee      # Domain-specific package for employee operations
│   │   ├── impl      # Implementation classes of service interfaces
│   │   └──           # Service interfaces defining business logic
│   └── ...           # Other domain packages (attendance, salary, leave, etc.)
├── specification     # Specifications for dynamic filtering and queries
└── util              # Utility/helper classes used across the project
```
## Tech Stack
- Java 21
- Spring Boot 3.5.4
- Spring Security (JWT using jjwt library)
- Spring Data JPA & Hibernate
- PostgreSQL
- Maven
- Lombok
- MapStruct
- Swagger / Springdoc OpenAPI
- JUnit & Spring Boot Test Starter
- Spring Boot Starter Mail
## Getting Started

1. Clone Repository
```bash
git clone https://github.com/username/hr-management-system.git 
cd hr-management-system
```
2. Setup Environment File
```env
# PostgreSQL Configuration
POSTGRES_USER=your_postgres_username
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=your_database_name
POSTGRES_PORT=5432

# JWT Configuration
JWT_SECRET=your_jwt_secret_here
JWT_EXPIRATION=3600000

# Mail Configuration
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password
```
3. Run with Docker - Docker Compose Configuration
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:latest
    container_name: hrms-postgres
    environment:
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
    ports:
      - "${POSTGRES_PORT}:5432"
    volumes:
      - hrms_data:/var/lib/postgresql/data

```
```bash
docker-compose up -d
```
4. Configure your database connection in application.yml.
5. Run the project:
```bash
mvn spring-boot:run
 ```
6. Access the Swagger
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs

## Feedbacks

Your feedback is valuable to me  
- furkanerdd1@gmail.com
- https://www.linkedin.com/in/furkanerd1/

  

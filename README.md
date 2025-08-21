# HR Management System REST API

This project is a robust, Spring Boot-based HR Management System backend service, developed as a personal learning and growth project. It focuses on implementing industry best practices, including secure authentication (JWT), role-based authorization, clean architecture with DTOs, and consistent API design. The current version covers core functionalities like employee management, performance reviews, and leave requests.

## Key Principles Implemented

- **Security**: JWT-based authentication, hierarchical role-based authorization (HR > MANAGER > EMPLOYEE), and secure password management.
- **Clean Architecture**: Use of Data Transfer Objects (DTOs) and service layers for clean separation of concerns.
- **RESTful API**: Consistent response formats, custom exception handling, and detailed API documentation via Swagger.

## Current Features

- **Employee Management**: Basic CRUD operations and status updates (soft-delete using INACTIVE). Users can view and update their own profile via `/me` endpoint.
- **Department & Position Management**: Basic CRUD operations.
- **Leave Requests**: Create, approve, and reject requests.
- **Attendance Tracking**: Record employees' daily check-ins and check-outs.
- **Salary & Performance Reviews**: Record employee salaries and performance evaluations.

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

## Getting Started

1. Clone the repository:  
   ```bash
   git clone https://github.com/username/hr-management-system.git 
     ```
2. Navigate into the project folder:
   ```bash
    cd hr-management-system
   ```
3. Docker Setup (Optional) : You can also run PostgreSQL using Docker:
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
          networks:
            - hrms-network
      
      volumes:
        hrms_data:
      
      networks:
        hrms-network:
          driver: bridge
    ```
4. Configure your database connection in application.yml.

5. Run the project:
    ```bash
    mvn spring-boot:run
   ```
5. Access the Swagger UI at http://localhost:8080/swagger-ui.html.

## Roadmap

- Improvements: Add pagination, sorting, and filtering for list endpoints.
- Notification System: Integrate email and in-app notifications for leave approvals and other events.
- Extra Security: Implement forgot-password and rate limiting mechanisms.
- Testing (Future): Add unit and integration tests for services and controllers using JUnit & Mockito.
- Automation: Set up CI/CD pipelines.

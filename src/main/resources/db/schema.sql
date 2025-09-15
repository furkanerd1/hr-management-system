-- Enum Types
CREATE TYPE role AS ENUM ('EMPLOYEE', 'MANAGER', 'HR');
CREATE TYPE status AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE leaveType AS ENUM ('VACATION', 'SICK', 'UNPAID', 'MATERNITY');
CREATE TYPE leaveStatus AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE notificationType AS ENUM ('LEAVE', 'PERFORMANCE', 'GENERAL', 'ANNOUNCEMENT');
CREATE TYPE announcementType AS ENUM ('HOLIDAY', 'POLICY', 'EVENT', 'GENERAL');

-- Tables
DROP TABLE IF EXISTS Notification CASCADE;
DROP TABLE IF EXISTS PerformanceReview CASCADE;
DROP TABLE IF EXISTS Salary CASCADE;
DROP TABLE IF EXISTS LeaveRequest CASCADE;
DROP TABLE IF EXISTS Attendance CASCADE;
DROP TABLE IF EXISTS Announcement CASCADE;
DROP TABLE IF EXISTS Employee CASCADE;
DROP TABLE IF EXISTS Position CASCADE;
DROP TABLE IF EXISTS Department CASCADE;

CREATE TABLE Department
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Position
(
    id          UUID PRIMARY KEY,
    title       VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Employee
(
    id                   UUID PRIMARY KEY,
    first_name           VARCHAR(50)         NOT NULL,
    last_name            VARCHAR(50)         NOT NULL,
    email                VARCHAR(100) UNIQUE NOT NULL,
    password_hash        VARCHAR(255),
    phone_number         VARCHAR(20) UNIQUE,
    hire_date            DATE                NOT NULL,
    birth_date           DATE                NOT NULL,
    address              TEXT,
    department_id        UUID REFERENCES Department (id),
    position_id          UUID REFERENCES Position (id),
    manager_id           UUID REFERENCES Employee (id),
    role                 role                NOT NULL,
    status               status              NOT NULL,
    must_change_password BOOLEAN             NOT NULL DEFAULT TRUE,
    vacation_balance     INTEGER             NOT NULL DEFAULT 20,
    maternity_balance    INTEGER             NOT NULL DEFAULT 112,
    created_at           TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Attendance
(
    id             UUID PRIMARY KEY,
    employee_id    UUID      NOT NULL REFERENCES Employee (id),
    date           DATE      NOT NULL,
    check_in_time  TIME      NOT NULL,
    check_out_time TIME,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, date)
);

CREATE TABLE LeaveRequest
(
    id             UUID PRIMARY KEY,
    employee_id    UUID        NOT NULL REFERENCES Employee (id),
    leave_type     leaveType   NOT NULL,
    start_date     DATE        NOT NULL,
    end_date       DATE,
    total_days     INTEGER,
    reason         TEXT,
    status         leaveStatus NOT NULL,
    approved_by_id UUID REFERENCES Employee (id),
    approved_at    TIMESTAMP,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Salary
(
    id             UUID PRIMARY KEY,
    employee_id    UUID           NOT NULL REFERENCES Employee (id),
    base_salary    DECIMAL(10, 2) NOT NULL,
    bonus          DECIMAL(10, 2),
    effective_date DATE           NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, effective_date)
);

CREATE TABLE PerformanceReview
(
    id          UUID PRIMARY KEY,
    employee_id UUID      NOT NULL REFERENCES Employee (id),
    reviewer_id UUID      NOT NULL REFERENCES Employee (id),
    rating      INTEGER,
    comments    TEXT,
    review_date DATE      NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Notification
(
    id          UUID PRIMARY KEY,
    employee_id UUID             NOT NULL REFERENCES Employee (id),
    message     TEXT             NOT NULL,
    type        notificationType NOT NULL,
    is_read     BOOLEAN          NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Announcement
(
    id            UUID PRIMARY KEY,
    title         VARCHAR(255)     NOT NULL,
    content       TEXT             NOT NULL,
    type          announcementType NOT NULL,
    created_by_id UUID REFERENCES Employee (id),
    created_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);
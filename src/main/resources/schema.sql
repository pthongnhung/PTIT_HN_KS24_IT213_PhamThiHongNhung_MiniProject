CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS department (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS employee (
    id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(30),
    department_id BIGINT,
    position VARCHAR(100),
    salary DECIMAL(15,2),
    status VARCHAR(30),
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES department(id)
);

CREATE TABLE IF NOT EXISTS leave_request (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    reason TEXT,
    status VARCHAR(30),
    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(id)
);

CREATE TABLE IF NOT EXISTS company_document (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    size BIGINT,
    uploaded_at TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING'
);

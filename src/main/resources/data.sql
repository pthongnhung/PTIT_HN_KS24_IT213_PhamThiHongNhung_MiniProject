-- Department Seed
INSERT INTO department (id, name, description)
VALUES 
(1, 'IT', 'Phòng Công nghệ thông tin'),
(2, 'HR', 'Phòng Nhân sự'),
(3, 'Marketing', 'Phòng Marketing'),
(4, 'Accounting', 'Phòng Kế toán')
ON CONFLICT (id) DO NOTHING;

-- Employee Seed
INSERT INTO employee (id, employee_code, full_name, email, phone, department_id, position, salary, status)
VALUES
(1, 'NV001', 'Nguyễn Văn An', 'an@example.com', '0900000001', 1, 'Java Developer', 25000000, 'ACTIVE'),
(2, 'NV002', 'Trần Thị Bình', 'binh@example.com', '0900000002', 2, 'HR Specialist', 20000000, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Leave Request Seed
INSERT INTO leave_request (id, employee_id, start_date, end_date, reason, status)
VALUES
(1, 1, '2026-05-01', '2026-05-03', 'Nghỉ phép năm đi du lịch cùng gia đình', 'APPROVED'),
(2, 1, '2026-08-10', '2026-08-10', 'Nghỉ ốm đi khám bệnh', 'APPROVED'),
(3, 1, '2026-09-15', '2026-09-16', 'Có việc gia đình đột xuất', 'PENDING')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence values to allow sequential auto-increments to function properly
SELECT setval('department_id_seq', COALESCE((SELECT MAX(id)+1 FROM department), 1), false);
SELECT setval('employee_id_seq', COALESCE((SELECT MAX(id)+1 FROM employee), 1), false);
SELECT setval('leave_request_id_seq', COALESCE((SELECT MAX(id)+1 FROM leave_request), 1), false);

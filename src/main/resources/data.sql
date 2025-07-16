-- Clear existing users
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;

-- Sample Admin (password: admin123)
INSERT INTO users (
    id, first_name, last_name, email, password, role, active, contact_number, profile_photo,
    student_id, department, enrollment_date
) VALUES (
    1, 'Admin', 'User', 'admin@school.edu',
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- bcrypt hash for admin123
    'ADMIN', 1, '09171234567', NULL,
    NULL, NULL, NULL
);

-- Sample Student (password: student123)
INSERT INTO users (
    id, first_name, last_name, email, password, role, active, contact_number, profile_photo,
    student_id, department, enrollment_date
) VALUES (
    2, 'John', 'Doe', 'john.doe@school.edu',
    '$2a$10$bKPhR9WtR5/gZ.lUZ3Xk0eNxTPQ9QSUjHmIOzLGYqL/B7e1XlG1s2', -- bcrypt hash for student123
    'STUDENT', 1, '09181234567', NULL,
    'S10001', 'Computer Science', '2023-09-01'
);

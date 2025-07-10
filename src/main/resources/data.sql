-- Clear existing data (optional)
DELETE FROM students_courses;
DELETE FROM students;
DELETE FROM courses;
DELETE FROM users;

-- Reset auto-increment counters
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;

-- Admin user (password: admin123)
INSERT INTO users (id, first_name, last_name, email, password, role)
VALUES (1, 'Admin', 'User', 'admin@school.edu', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ADMIN');

-- Sample student (password: student123)
INSERT INTO users (id, first_name, last_name, email, password, role)
VALUES (2, 'John', 'Doe', 'john.doe@school.edu', '$2a$10$bKPhR9WtR5/gZ.lUZ3Xk0eNxTPQ9QSUjHmIOzLGYqL/B7e1XlG1s2', 'STUDENT');

INSERT INTO students (id, student_id, department, enrollment_date)
VALUES (2, 'S10001', 'Computer Science', '2023-09-01');

-- Sample courses
INSERT INTO courses (id, code, title, description, credit_hours)
VALUES
(1, 'CS101', 'Introduction to Computer Science', 'Fundamentals of programming and computer science', 3),
(2, 'MATH201', 'Calculus I', 'Differential and integral calculus', 4),
(3, 'ENG101', 'English Composition', 'Developing writing skills for academic purposes', 3);

-- Enrollment
INSERT INTO students_courses (student_id, course_id)
VALUES (2, 1), (2, 3);
-- Clean existing data (if any)
TRUNCATE TABLE loans RESTART IDENTITY CASCADE;
TRUNCATE TABLE members RESTART IDENTITY CASCADE;
TRUNCATE TABLE books RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- INSERT USERS (Administrators)
INSERT INTO users (username, password, email, full_name, role, is_active)
VALUES ('admin', 'admin123', 'admin@libronova.com', 'Carlos Rodriguez', 'ADMIN', TRUE),
       ('librarian1', 'lib123', 'librarian1@libronova.com', 'Maria Garcia', 'LIBRARIAN', TRUE),
       ('librarian2', 'lib123', 'librarian2@libronova.com', 'Juan Martinez', 'LIBRARIAN', TRUE);

-- INSERT MEMBERS (Library Members)
INSERT INTO members (membership_number, full_name, email, phone, status, registration_date)
VALUES ('MEM-2024-001', 'Ana Sofia Lopez', 'ana.lopez@email.com', '555-0101', 'ACTIVE', '2024-01-15'),
       ('MEM-2024-002', 'Pedro Hernandez', 'pedro.hernandez@email.com', '555-0102', 'ACTIVE', '2024-01-20'),
       ('MEM-2024-003', 'Laura Ramirez', 'laura.ramirez@email.com', '555-0103', 'ACTIVE', '2024-02-01'),
       ('MEM-2024-004', 'Diego Morales', 'diego.morales@email.com', '555-0104', 'ACTIVE', '2024-02-10'),
       ('MEM-2024-005', 'Sofia Torres', 'sofia.torres@email.com', '555-0105', 'INACTIVE', '2024-03-01'),
       ('MEM-2024-006', 'Miguel Angel Ruiz', 'miguel.ruiz@email.com', '555-0106', 'ACTIVE', '2024-03-15');

-- INSERT BOOKS (Catalog)
INSERT INTO books (isbn, title, author, publication_year, genre, total_copies, available_copies, is_active)
VALUES
-- Programming Books
('9780134685991', 'Effective Java', 'Joshua Bloch', 2018, 'Programming', 3, 2, TRUE),
('9780596009205', 'Head First Java', 'Kathy Sierra', 2005, 'Programming', 4, 3, TRUE),
('9780132350884', 'Clean Code', 'Robert C. Martin', 2008, 'Programming', 5, 4, TRUE),
('9780201633610', 'Design Patterns', 'Gang of Four', 1994, 'Programming', 2, 1, TRUE),

-- Classic Literature
('9780141439518', 'Pride and Prejudice', 'Jane Austen', 1813, 'Classic', 3, 3, TRUE),
('9780451524935', '1984', 'George Orwell', 1949, 'Fiction', 4, 3, TRUE),
('9780743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 1925, 'Classic', 2, 2, TRUE),
('9780060935467', 'To Kill a Mockingbird', 'Harper Lee', 1960, 'Classic', 3, 2, TRUE),

-- Science Fiction
('9780765326355', 'The Way of Kings', 'Brandon Sanderson', 2010, 'Fantasy', 2, 1, TRUE),
('9780765311788', 'Mistborn', 'Brandon Sanderson', 2006, 'Fantasy', 3, 3, TRUE),
('9780441172719', 'Dune', 'Frank Herbert', 1965, 'Science Fiction', 2, 1, TRUE),

-- Self-Help
('9781476746913', 'Atomic Habits', 'James Clear', 2018, 'Self-Help', 5, 4, TRUE),
('9780735211292', 'Atomic Habits', 'James Clear', 2018, 'Self-Help', 3, 3, TRUE),

-- Out of stock book (for testing)
('9780307887443', 'The Fault in Our Stars', 'John Green', 2012, 'Young Adult', 2, 0, TRUE);

-- INSERT LOANS (Active and Historical)
-- Active loans (currently borrowed)
INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date, status, fine_amount)
VALUES (1, 1, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '10 days', NULL, 'ACTIVE', 0.00),
       (4, 2, CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '12 days', NULL, 'ACTIVE', 0.00),
       (9, 3, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '8 days', NULL, 'ACTIVE', 0.00),
       (11, 4, CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '13 days', NULL, 'ACTIVE', 0.00);

-- Overdue loans (past due date, not returned yet)
INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date, status, fine_amount)
VALUES (8, 1, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '5 days', NULL, 'OVERDUE', 5.00);

-- Returned loans (historical data)
INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date, status, fine_amount)
VALUES (2, 1, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '14 days',
        'RETURNED', 0.00),
       (3, 2, CURRENT_DATE - INTERVAL '25 days', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '9 days',
        'RETURNED', 0.00),
       (5, 3, CURRENT_DATE - INTERVAL '35 days', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '18 days',
        'RETURNED', 2.00),
       (6, 4, CURRENT_DATE - INTERVAL '40 days', CURRENT_DATE - INTERVAL '25 days', CURRENT_DATE - INTERVAL '24 days',
        'RETURNED', 0.00);
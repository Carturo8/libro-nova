-- Clear all tables and reset primary key sequences
TRUNCATE TABLE loans RESTART IDENTITY CASCADE;
TRUNCATE TABLE members RESTART IDENTITY CASCADE;
TRUNCATE TABLE books RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- SEED USERS TABLE
INSERT INTO users (username, password, email, full_name, role, is_active)
VALUES ('admin', 'adminpass', 'admin@libronova.com', 'Administrator', 'ADMIN', TRUE),
       ('librarian', 'libpass', 'librarian@libronova.com', 'Laura Palmer', 'LIBRARIAN', TRUE),
       ('johndoe', 'pass123', 'john.doe@email.com', 'John Doe', 'MEMBER', TRUE),
       ('janesmith', 'pass456', 'jane.smith@email.com', 'Jane Smith', 'MEMBER', TRUE),
       ('peterjones', 'pass789', 'peter.jones@email.com', 'Peter Jones', 'MEMBER', FALSE);

-- SEED MEMBERS TABLE
INSERT INTO members (user_id, membership_number, phone, status, registration_date)
VALUES ((SELECT id FROM users WHERE username = 'johndoe'), 'MEM-2024-001', '555-1234', 'ACTIVE', '2024-01-15'),
       ((SELECT id FROM users WHERE username = 'janesmith'), 'MEM-2024-002', '555-5678', 'ACTIVE', '2024-02-20'),
       ((SELECT id FROM users WHERE username = 'peterjones'), 'MEM-2023-003', '555-9999', 'INACTIVE', '2023-11-10');

-- SEED BOOKS TABLE
INSERT INTO books (isbn, title, author, publication_year, genre, total_copies, available_copies)
VALUES ('9780345391803', 'The Lord of the Rings', 'J.R.R. Tolkien', 1954, 'Fantasy', 5, 4),
       ('9780451524935', '1984', 'George Orwell', 1949, 'Dystopian', 3, 2),
       ('9780441013593', 'Dune', 'Frank Herbert', 1965, 'Sci-Fi', 2, 1),
       ('9780141439518', 'Pride and Prejudice', 'Jane Austen', 1813, 'Romance', 4, 4),
       ('9780345339683', 'The Hobbit', 'J.R.R. Tolkien', 1937, 'Fantasy', 1, 1);

-- SEED LOANS TABLE
INSERT INTO loans (book_id, member_id, loan_date, due_date, return_date, status, fine_amount)
VALUES
    -- Active loan for John Doe
    ((SELECT id FROM books WHERE isbn = '9780441013593'),
     (SELECT id FROM members WHERE membership_number = 'MEM-2024-001'),
     CURRENT_DATE - INTERVAL '10 days',
     CURRENT_DATE + INTERVAL '5 days',
     NULL, 'ACTIVE', 0.00),
    -- Overdue loan for Jane Smith
    ((SELECT id FROM books WHERE isbn = '9780345391803'),
     (SELECT id FROM members WHERE membership_number = 'MEM-2024-002'),
     CURRENT_DATE - INTERVAL '20 days',
     CURRENT_DATE - INTERVAL '5 days',
     NULL, 'OVERDUE', 5.00),
    -- Returned loan for John Doe
    ((SELECT id FROM books WHERE isbn = '9780451524935'),
     (SELECT id FROM members WHERE membership_number = 'MEM-2024-001'),
     '2024-04-01', '2024-04-15',
     '2024-04-14', 'RETURNED', 0.00);
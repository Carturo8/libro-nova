-- DROP TABLES IF THEY EXIST
DROP TABLE IF EXISTS loans CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- USERS TABLE
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    full_name  VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- MEMBERS TABLE
CREATE TABLE members
(
    id                SERIAL PRIMARY KEY,
    membership_number VARCHAR(20)  NOT NULL UNIQUE,
    full_name         VARCHAR(100) NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    phone             VARCHAR(20),
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    registration_date DATE         NOT NULL DEFAULT CURRENT_DATE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- BOOKS TABLE
CREATE TABLE books
(
    id               SERIAL PRIMARY KEY,
    isbn             VARCHAR(13)  NOT NULL UNIQUE,
    title            VARCHAR(200) NOT NULL,
    author           VARCHAR(100) NOT NULL,
    publication_year INTEGER,
    genre            VARCHAR(50),
    total_copies     INTEGER      NOT NULL DEFAULT 1,
    available_copies INTEGER      NOT NULL DEFAULT 1,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- LOANS TABLE
CREATE TABLE loans
(
    id          SERIAL PRIMARY KEY,
    book_id     INTEGER        NOT NULL,
    member_id   INTEGER        NOT NULL,
    loan_date   DATE           NOT NULL DEFAULT CURRENT_DATE,
    due_date    DATE           NOT NULL,
    return_date DATE,
    status      VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    fine_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books (id),
    FOREIGN KEY (member_id) REFERENCES members (id)
);
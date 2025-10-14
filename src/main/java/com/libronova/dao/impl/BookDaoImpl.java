package com.libronova.dao.impl;

import com.libronova.config.DatabaseConfig;
import com.libronova.dao.BookDao;
import com.libronova.errors.DataAccessException;
import com.libronova.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    @Override
    public Book create(Book book) throws DataAccessException {
        String sql = "INSERT INTO books (isbn, title, author, publication_year, genre, " +
                "total_copies, available_copies, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setObject(4, book.getPublicationYear());
            stmt.setString(5, book.getGenre());
            stmt.setInt(6, book.getTotalCopies());
            stmt.setInt(7, book.getAvailableCopies());
            stmt.setBoolean(8, book.isActive());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating book failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                    return book;
                } else {
                    throw new DataAccessException("Creating book failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating book: " + e.getMessage(), e);
        }
    }

    @Override
    public Book findById(Integer id) throws DataAccessException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding book by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Book findByIsbn(String isbn) throws DataAccessException {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding book by ISBN: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findAll() throws DataAccessException {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findAllActive() throws DataAccessException {
        String sql = "SELECT * FROM books WHERE is_active = true ORDER BY title";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            return books;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving active books: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> searchByTitle(String title) throws DataAccessException {
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) AND is_active = true ORDER BY title";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            return books;

        } catch (SQLException e) {
            throw new DataAccessException("Error searching books by title: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Book book) throws DataAccessException {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, publication_year = ?, " +
                "genre = ?, total_copies = ?, available_copies = ?, is_active = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setObject(4, book.getPublicationYear());
            stmt.setString(5, book.getGenre());
            stmt.setInt(6, book.getTotalCopies());
            stmt.setInt(7, book.getAvailableCopies());
            stmt.setBoolean(8, book.isActive());
            stmt.setInt(9, book.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error updating book: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DataAccessException {
        String sql = "UPDATE books SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting book: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateAvailableCopies(Integer bookId, Integer availableCopies) throws DataAccessException {
        String sql = "UPDATE books SET available_copies = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, availableCopies);
            stmt.setInt(2, bookId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error updating available copies: " + e.getMessage(), e);
        }
    }

    // Helper method to map ResultSet to Book object.
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));

        Integer year = (Integer) rs.getObject("publication_year");
        book.setPublicationYear(year);

        book.setGenre(rs.getString("genre"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            book.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            book.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return book;
    }
}
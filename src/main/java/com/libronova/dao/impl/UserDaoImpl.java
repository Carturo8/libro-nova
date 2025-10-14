package com.libronova.dao.impl;

import com.libronova.config.DatabaseConfig;
import com.libronova.dao.UserDao;
import com.libronova.errors.DataAccessException;
import com.libronova.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public User create(User user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email, full_name, role, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            stmt.setBoolean(6, user.isActive());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating user failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    return user;
                } else {
                    throw new DataAccessException("Creating user failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(Integer id) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByUsername(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by username: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        String sql = "SELECT * FROM users ORDER BY full_name";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            return users;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all users: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAllActive() throws DataAccessException {
        String sql = "SELECT * FROM users WHERE is_active = true ORDER BY full_name";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            return users;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving active users: " + e.getMessage(), e);
        }
    }

    @Override
    public User validateCredentials(String username, String password) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = true";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error validating credentials: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(User user) throws DataAccessException {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ?, " +
                "role = ?, is_active = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DataAccessException {
        String sql = "UPDATE users SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting user: " + e.getMessage(), e);
        }
    }

    // Helper method to map ResultSet to User object.
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}
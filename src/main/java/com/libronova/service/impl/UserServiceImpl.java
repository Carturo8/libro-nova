package com.libronova.service.impl;

import com.libronova.config.DatabaseConfig;
import com.libronova.dao.MemberDao;
import com.libronova.dao.UserDao;
import com.libronova.dao.impl.MemberDaoImpl;
import com.libronova.dao.impl.UserDaoImpl;
import com.libronova.errors.*;
import com.libronova.model.Member;
import com.libronova.model.User;
import com.libronova.service.MemberService;
import com.libronova.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final MemberDao memberDao;
    private final MemberService memberService;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.memberDao = new MemberDaoImpl();
        this.memberService = new MemberServiceImpl();
    }

    @Override
    public User registerMember(User user, Member memberDetails) {
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            validateUserData(user);

            user.setRole("MEMBER");
            user.setActive(true);
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new ConflictException("User", "username", user.getUsername());
            }
            if (userDao.findByEmail(user.getEmail()) != null) {
                throw new ConflictException("User", "email", user.getEmail());
            }

            User createdUser = userDao.create(user, conn);
            memberDetails.setUserId(createdUser.getId());
            memberDetails.setMembershipNumber(memberService.generateMembershipNumber());
            memberDetails.setStatus("ACTIVE");

            if (memberDetails.getRegistrationDate() == null) {
                memberDetails.setRegistrationDate(LocalDate.now());
            }

            memberDao.create(memberDetails, conn);
            conn.commit();
            createdUser.setMember(memberDetails);
            return createdUser;

        } catch (ConflictException | BadRequestException e) {
            rollback(conn);
            throw e;
        } catch (DataAccessException e) {
            rollback(conn);
            throw new ServiceException("Error accessing data during member registration.", e);
        } catch (SQLException e) {
            rollback(conn);
            throw new ServiceException("Error registering member due to a database issue.", e);
        } finally {
            close(conn);
        }
    }

    @Override
    public User createStaff(User user) {
        try {
            validateUserData(user);
            if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !"LIBRARIAN".equalsIgnoreCase(user.getRole())) {
                throw new BadRequestException("Staff user role must be ADMIN or LIBRARIAN.");
            }
            user.setActive(true);
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new ConflictException("User", "username", user.getUsername());
            }
            if (userDao.findByEmail(user.getEmail()) != null) {
                throw new ConflictException("User", "email", user.getEmail());
            }
            return userDao.create(user);

        } catch (ConflictException | BadRequestException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new ServiceException("Error accessing data during staff user creation.", e);
        } catch (Exception e) {
            throw new ServiceException("An unexpected error occurred during staff user creation.", e);
        }
    }

    @Override
    public User login(String usernameOrEmail, String password) {
        try {
            if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
                throw new BadRequestException("Username or Email cannot be empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new BadRequestException("Password cannot be empty");
            }
            User user = userDao.validateCredentials(usernameOrEmail, password);
            if (user == null) {
                throw new UnauthorizedException("Invalid username or password");
            }
            if (!user.isActive()) {
                throw new UnauthorizedException("User account is inactive");
            }
            return user;

        } catch (DataAccessException e) {
            throw new ServiceException("Error during login", e);
        }
    }

    @Override
    public User getUserById(Integer id) {
        try {
            User user = userDao.findById(id);
            if (user == null) {
                throw new NotFoundException("User", id);
            }
            return user;

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving user", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDao.findAll();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving all users", e);
        }
    }

    @Override
    public List<User> getUsersByRole(String role) {
        try {
            if (role == null || role.trim().isEmpty()) {
                throw new BadRequestException("Role cannot be empty");
            }
            return userDao.findAllByRole(role);

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving users by role", e);
        }
    }

    @Override
    public List<User> getAllActiveUsers() {
        try {
            return userDao.findAllActive();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving active users", e);
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            User existingUser = userDao.findById(user.getId());
            if (existingUser == null) {
                throw new NotFoundException("User", user.getId());
            }
            validateUserData(user);
            User userWithSameUsername = userDao.findByUsername(user.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.getId().equals(user.getId())) {
                throw new ConflictException("User", "username", user.getUsername());
            }
            User userWithSameEmail = userDao.findByEmail(user.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(user.getId())) {
                throw new ConflictException("User", "email", user.getEmail());
            }
            boolean updated = userDao.update(user);
            if (!updated) {
                throw new ServiceException("Failed to update user");
            }
            return userDao.findById(user.getId());

        } catch (DataAccessException e) {
            throw new ServiceException("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(Integer id) {
        try {
            User user = userDao.findById(id);
            if (user == null) {
                throw new NotFoundException("User", id);
            }
            boolean deleted = userDao.delete(id);
            if (!deleted) {
                throw new ServiceException("Failed to delete user");
            }

        } catch (DataAccessException e) {
            throw new ServiceException("Error deleting user", e);
        }
    }

    private void validateUserData(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username", "cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password", "cannot be empty");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new BadRequestException("Email", "is invalid");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name", "cannot be empty");
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();

            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
            }
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();

            } catch (SQLException ex) {
                System.err.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
}
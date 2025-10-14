package com.libronova.service.impl;

import com.libronova.dao.UserDao;
import com.libronova.dao.impl.UserDaoImpl;
import com.libronova.errors.*;
import com.libronova.model.User;
import com.libronova.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User createUser(User user) {

        try {
            validateUserData(user);

            User existingUserByUsername = userDao.findByUsername(user.getUsername());
            if (existingUserByUsername != null) {
                throw new ConflictException("User", "username", user.getUsername());
            }

            User existingUserByEmail = userDao.findByEmail(user.getEmail());
            if (existingUserByEmail != null) {
                throw new ConflictException("User", "email", user.getEmail());
            }

            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("ADMIN");
            }

            return userDao.create(user);

        } catch (DataAccessException e) {
            throw new ServiceException("Error creating user", e);
        }
    }

    @Override
    public User login(String username, String password) {

        try {
            if (username == null || username.trim().isEmpty()) {
                throw new BadRequestException("Username cannot be empty");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new BadRequestException("Password cannot be empty");
            }

            User user = userDao.validateCredentials(username, password);
            if (user == null) {
                throw new UnauthorizedException("Invalid username or password");
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

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email", "cannot be empty");
        }

        if (!user.getEmail().contains("@")) {
            throw new BadRequestException("Email", "invalid format");
        }

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name", "cannot be empty");
        }
    }
}
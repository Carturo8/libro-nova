package com.libronova.controller;

import com.libronova.errors.*;
import com.libronova.model.User;
import com.libronova.service.UserService;
import com.libronova.service.impl.UserServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserServiceImpl();
    }

    public User createUser(User user) {
        try {
            logger.info("Attempting to create a new user with username: {}", user.getUsername());
            User createdUser = userService.createUser(user);
            logger.info("User '{}' created successfully with ID {}.", createdUser.getUsername(), createdUser.getId());
            return createdUser;

        } catch (ConflictException | BadRequestException e) {
            logger.warn("User creation failed: {}", e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred during user creation.", e);
            return null;
        }
    }

    public User login(String username, String password) {
        try {
            logger.info("Login attempt for user: {}", username);
            User user = userService.login(username, password);
            logger.info("User '{}' logged in successfully.", username);
            return user;

        } catch (UnauthorizedException | BadRequestException e) {
            logger.warn("Login failed for user '{}': {}", username, e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred during login for user '{}'", username, e);
            return null;
        }
    }

    public User getUserById(Integer id) {
        try {
            logger.debug("Attempting to find user by ID: {}", id);
            return userService.getUserById(id);

        } catch (NotFoundException e) {
            logger.warn("Could not find user with ID {}: {}", id, e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching user with ID {}", id, e);
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            logger.debug("Attempting to fetch all users.");
            return userService.getAllUsers();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching all users.", e);
            return Collections.emptyList();
        }
    }

    public List<User> getAllActiveUsers() {
        try {
            logger.debug("Attempting to fetch all active users.");
            return userService.getAllActiveUsers();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching active users.", e);
            return Collections.emptyList();
        }
    }

    public User updateUser(User user) {
        try {
            logger.info("Attempting to update user with ID: {}", user.getId());
            User updatedUser = userService.updateUser(user);
            logger.info("User with ID {} updated successfully.", user.getId());
            return updatedUser;

        } catch (NotFoundException | ConflictException | BadRequestException e) {
            logger.warn("User update failed for user ID {}: {}", user.getId(), e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while updating user with ID {}", user.getId(), e);
            return null;
        }
    }

    public boolean deleteUser(Integer id) {
        try {
            logger.info("Attempting to delete user with ID: {}", id);
            userService.deleteUser(id);
            logger.info("User with ID {} marked as inactive successfully.", id);
            return true;

        } catch (NotFoundException e) {
            logger.warn("Could not delete user with ID {}: {}", id, e.getMessage());
            return false;
        } catch (ServiceException e) {
            logger.error("A service error occurred while deleting user with ID {}", id, e);
            return false;
        }
    }
}
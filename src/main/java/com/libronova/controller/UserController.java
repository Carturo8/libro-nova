package com.libronova.controller;

import com.libronova.errors.*;
import com.libronova.model.Member;
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

    public User registerMember(User user, Member memberDetails) {
        try {
            logger.info("Attempting to register new member with username: {}", user.getUsername());
            User createdUser = userService.registerMember(user, memberDetails);
            logger.info("Member '{}' registered successfully with ID {}.", createdUser.getFullName(), createdUser.getId());
            return createdUser;

        } catch (ConflictException | BadRequestException | ServiceException e) {
            logger.warn("Member registration failed: {}", e.getMessage());
            return null;
        }
    }

    public User createStaff(User user) {
        try {
            logger.info("Attempting to create new staff user with username: {}", user.getUsername());
            User createdUser = userService.createStaff(user);
            logger.info("Staff user '{}' (Role: {}) created successfully with ID {}.", createdUser.getFullName(), createdUser.getRole(), createdUser.getId());
            return createdUser;

        } catch (ConflictException | BadRequestException | ServiceException e) {
            logger.warn("Staff user creation failed: {}", e.getMessage());
            return null;
        }
    }

    public User login(String usernameOrEmail, String password) {
        try {
            logger.info("Login attempt for user: {}", usernameOrEmail);
            User user = userService.login(usernameOrEmail, password);
            logger.info("User '{}' logged in successfully.", usernameOrEmail);
            return user;

        } catch (UnauthorizedException | BadRequestException | ServiceException e) {
            logger.warn("Login failed for user '{}': {}", usernameOrEmail, e.getMessage());
            return null;
        }
    }

    public User getUserById(Integer id) {
        try {
            logger.debug("Attempting to find user by ID: {}", id);
            return userService.getUserById(id);

        } catch (NotFoundException | ServiceException e) {
            logger.warn("Could not find user with ID {}: {}", id, e.getMessage());
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

    public List<User> getUsersByRole(String role) {
        try {
            logger.debug("Attempting to fetch users by role: {}", role);
            return userService.getUsersByRole(role);

        } catch (BadRequestException | ServiceException e) {
            logger.warn("Failed to fetch users by role '{}': {}", role, e.getMessage());
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

        } catch (NotFoundException | ConflictException | BadRequestException | ServiceException e) {
            logger.warn("User update failed for user ID {}: {}", user.getId(), e.getMessage());
            return null;
        }
    }

    public boolean deleteUser(Integer id) {
        try {
            logger.info("Attempting to delete user with ID: {}", id);
            userService.deleteUser(id);
            logger.info("User with ID {} deleted successfully.", id);
            return true;

        } catch (NotFoundException | ServiceException e) {
            logger.warn("Could not delete user with ID {}: {}", id, e.getMessage());
            return false;
        }
    }
}
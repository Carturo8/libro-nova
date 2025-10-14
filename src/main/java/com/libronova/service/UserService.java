package com.libronova.service;

import com.libronova.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User login(String username, String password);
    User getUserById(Integer id);
    List<User> getAllUsers();
    List<User> getAllActiveUsers();
    User updateUser(User user);
    void deleteUser(Integer id);
}
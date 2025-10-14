package com.libronova.service;

import com.libronova.model.Member;
import com.libronova.model.User;

import java.util.List;

public interface UserService {
    User registerMember(User user, Member memberDetails);
    User createStaff(User user);
    User login(String usernameOrEmail, String password);
    User getUserById(Integer id);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    List<User> getAllActiveUsers();
    User updateUser(User user);
    void deleteUser(Integer id);
}
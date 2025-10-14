package com.libronova.view;

import com.libronova.controller.UserController;
import com.libronova.model.User;

import javax.swing.JOptionPane;
import java.util.List;

public class UserView {

    private final UserController userController;

    public UserView() {
        this.userController = new UserController();
    }

    public void showUserManagementMenu() {
        String[] options = {"List All Users", "Find User by ID", "Update User", "Delete User", "Back to Main Menu"};
        int choice = -1;

        while (choice != 4) {
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an option for User Management:",
                    "User Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    listAllUsers();
                    break;
                case 1:
                    findUserById();
                    break;
                case 2:
                    updateUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    break;
                default:
                    choice = 4;
                    break;
            }
        }
    }

    private void listAllUsers() {
        List<User> users = userController.getAllUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No users found.", "User List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("List of all users:\n\n");
        for (User user : users) {
            sb.append("ID: ").append(user.getId())
              .append(", Name: ").append(user.getFullName())
              .append(", Username: ").append(user.getUsername())
              .append(", Email: ").append(user.getEmail())
              .append(", Role: ").append(user.getRole())
              .append(", Active: ").append(user.isActive())
              .append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "User List", JOptionPane.PLAIN_MESSAGE);
    }

    private void findUserById() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the user ID:", "Find User", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            User user = userController.getUserById(id);

            if (user != null) {
                String userInfo = String.format("ID: %d\nName: %s\nUsername: %s\nEmail: %s\nRole: %s\nActive: %s",
                        user.getId(), user.getFullName(), user.getUsername(), user.getEmail(), user.getRole(), user.isActive());
                JOptionPane.showMessageDialog(null, userInfo, "User Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "User with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the user to update:", "Update User", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            User user = userController.getUserById(id);

            if (user == null) {
                JOptionPane.showMessageDialog(null, "User with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fullName = JOptionPane.showInputDialog(null, "Enter new full name (current: " + user.getFullName() + "):", user.getFullName());
            String email = JOptionPane.showInputDialog(null, "Enter new email (current: " + user.getEmail() + "):", user.getEmail());
            String role = JOptionPane.showInputDialog(null, "Enter new role (current: " + user.getRole() + "):", user.getRole());

            user.setFullName(fullName);
            user.setEmail(email);
            user.setRole(role);

            User updatedUser = userController.updateUser(user);

            if (updatedUser != null) {
                JOptionPane.showMessageDialog(null, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update user. Check logs for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the user to delete (mark as inactive):", "Delete User", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete user with ID " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = userController.deleteUser(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(null, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete user. The user might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

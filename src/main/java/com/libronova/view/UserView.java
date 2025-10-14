package com.libronova.view;

import com.libronova.controller.MemberController;
import com.libronova.controller.UserController;
import com.libronova.model.Member;
import com.libronova.model.User;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.util.List;

public class UserView {

    private final UserController userController;
    private final MemberController memberController; // Needed for updating member details

    public UserView() {
        this.userController = new UserController();
        this.memberController = new MemberController();
    }

    public void showUserManagementMenu() {
        String[] options = {"List All Users", "List Users by Role", "Find User by ID", "Create Staff User", "Update User", "Delete User", "Back to Main Menu"};
        int choice = -1;

        while (choice != 6) {
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
                    listUsersByRole();
                    break;
                case 2:
                    findUserById();
                    break;
                case 3:
                    createStaffUser();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 6:
                    break;
                default:
                    choice = 6;
                    break;
            }
        }
    }

    private void listAllUsers() {
        List<User> users = userController.getAllUsers();
        displayUsers(users, "List of All Users");
    }

    private void listUsersByRole() {
        String role = JOptionPane.showInputDialog(null, "Enter role (ADMIN, LIBRARIAN, MEMBER):");
        if (role == null || role.trim().isEmpty()) return;

        List<User> users = userController.getUsersByRole(role.toUpperCase());
        displayUsers(users, "Users with Role: " + role.toUpperCase());
    }

    private void findUserById() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the user ID:", "Find User", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            User user = userController.getUserById(id);

            if (user != null) {
                displayUser(user, "User Found");
            } else {
                JOptionPane.showMessageDialog(null, "User with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createStaffUser() {
        String username = JOptionPane.showInputDialog(null, "Enter new staff username:", "Create Staff User", JOptionPane.PLAIN_MESSAGE);
        if (username == null) return;

        JPasswordField passwordField = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(null, passwordField, "Enter password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        String password = new String(passwordField.getPassword());

        String fullName = JOptionPane.showInputDialog(null, "Enter full name:", "Create Staff User", JOptionPane.PLAIN_MESSAGE);
        if (fullName == null) return;

        String email = JOptionPane.showInputDialog(null, "Enter email:", "Create Staff User", JOptionPane.PLAIN_MESSAGE);
        if (email == null) return;

        String[] roles = {"ADMIN", "LIBRARIAN"};
        String role = (String) JOptionPane.showInputDialog(
                null,
                "Select role for staff user:",
                "Create Staff User",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roles,
                roles[0]
        );
        if (role == null) return;

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setRole(role);

        User createdUser = userController.createStaff(newUser);

        if (createdUser != null) {
            JOptionPane.showMessageDialog(null, "Staff user created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to create staff user. Check logs for details.", "Error", JOptionPane.ERROR_MESSAGE);
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

            String newFullName = JOptionPane.showInputDialog(null, "Enter new full name (current: " + user.getFullName() + "):", user.getFullName());
            String newEmail = JOptionPane.showInputDialog(null, "Enter new email (current: " + user.getEmail() + "):", user.getEmail());
            String newUsername = JOptionPane.showInputDialog(null, "Enter new username (current: " + user.getUsername() + "):", user.getUsername());
            String newRole = JOptionPane.showInputDialog(null, "Enter new role (current: " + user.getRole() + "):", user.getRole());
            boolean newIsActive = JOptionPane.showConfirmDialog(null, "Is user active? (current: " + user.isActive() + ")", "Update User", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

            user.setFullName(newFullName);
            user.setEmail(newEmail);
            user.setUsername(newUsername);
            user.setRole(newRole);
            user.setActive(newIsActive);

            User updatedUser = userController.updateUser(user);

            if (updatedUser != null) {
                // If the user is a MEMBER, also update their member details
                if ("MEMBER".equalsIgnoreCase(updatedUser.getRole()) && updatedUser.getMember() != null) {
                    Member member = updatedUser.getMember();
                    String newPhone = JOptionPane.showInputDialog(null, "Enter new phone number (current: " + member.getPhone() + "):", member.getPhone());
                    String newStatus = JOptionPane.showInputDialog(null, "Enter new member status (ACTIVE/INACTIVE) (current: " + member.getStatus() + "):", member.getStatus());

                    member.setPhone(newPhone);
                    member.setStatus(newStatus);

                    Member updatedMember = memberController.updateMemberDetails(member);
                    if (updatedMember != null) {
                        JOptionPane.showMessageDialog(null, "User and Member details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "User updated, but failed to update Member details. Check logs.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update user. Check logs for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the user to delete:", "Delete User", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete user with ID " + id + "? This will also delete associated member data.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

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

    private void displayUsers(List<User> users, String title) {
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No users found.", title, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder(title + ":\n\n");
        for (User user : users) {
            sb.append(formatUserDisplay(user));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), title, JOptionPane.PLAIN_MESSAGE);
    }

    private void displayUser(User user, String title) {
        JOptionPane.showMessageDialog(null, formatUserDisplay(user), title, JOptionPane.PLAIN_MESSAGE);
    }

    private String formatUserDisplay(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(user.getId()).append("\n");
        sb.append("Username: ").append(user.getUsername()).append("\n");
        sb.append("Full Name: ").append(user.getFullName()).append("\n");
        sb.append("Email: ").append(user.getEmail()).append("\n");
        sb.append("Role: ").append(user.getRole()).append("\n");
        sb.append("Active: ").append(user.isActive()).append("\n");

        if ("MEMBER".equalsIgnoreCase(user.getRole()) && user.getMember() != null) {
            Member member = user.getMember();
            sb.append("--- Member Details ---\n");
            sb.append("Member ID: ").append(member.getId()).append("\n");
            sb.append("Membership Number: ").append(member.getMembershipNumber()).append("\n");
            sb.append("Phone: ").append(member.getPhone() != null ? member.getPhone() : "N/A").append("\n");
            sb.append("Member Status: ").append(member.getStatus()).append("\n");
            sb.append("Registration Date: ").append(member.getRegistrationDate()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}

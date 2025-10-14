package com.libronova.view;

import com.libronova.controller.UserController;
import com.libronova.model.Member;
import com.libronova.model.User;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class AuthView {

    private final UserController userController;

    public AuthView() {
        this.userController = new UserController();
    }

    public void showAuthMenu() {
        String[] options = {"Login", "Register Member", "Exit"};
        int choice = -1;

        while (choice != 2) {
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome to LibroNova! Please choose an option:",
                    "LibroNova Authentication",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    showLoginDialog();
                    break;
                case 1:
                    showRegisterMemberDialog();
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Goodbye!");
                    break;
                default:
                    choice = 2;
                    break;
            }
        }
    }

    private void showLoginDialog() {
        String usernameOrEmail = JOptionPane.showInputDialog(null, "Enter your username or email:", "Login", JOptionPane.PLAIN_MESSAGE);
        if (usernameOrEmail == null) return;

        JPasswordField passwordField = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(null, passwordField, "Enter your password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String password = new String(passwordField.getPassword());

        User authenticatedUser = userController.login(usernameOrEmail, password);

        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(null, "Welcome, " + authenticatedUser.getFullName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            MainView mainView = new MainView();
            mainView.showMainMenu(authenticatedUser);
        } else {
            JOptionPane.showMessageDialog(null, "Login failed. Please check your credentials or register.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterMemberDialog() {
        String username = JOptionPane.showInputDialog(null, "Enter a new username:", "Register Member", JOptionPane.PLAIN_MESSAGE);
        if (username == null) return;

        JPasswordField passwordField = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(null, passwordField, "Enter a password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        String password = new String(passwordField.getPassword());

        String fullName = JOptionPane.showInputDialog(null, "Enter your full name:", "Register Member", JOptionPane.PLAIN_MESSAGE);
        if (fullName == null) return;

        String email = JOptionPane.showInputDialog(null, "Enter your email:", "Register Member", JOptionPane.PLAIN_MESSAGE);
        if (email == null) return;

        String phone = JOptionPane.showInputDialog(null, "Enter your phone number (optional):");

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);

        Member newMemberDetails = new Member();
        newMemberDetails.setPhone(phone);

        User createdUser = userController.registerMember(newUser, newMemberDetails);

        if (createdUser != null) {
            JOptionPane.showMessageDialog(null, "Registration successful! You can now log in.\nYour Membership Number: " + createdUser.getMember().getMembershipNumber(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Registration failed. The username or email might already exist, or your data is invalid.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

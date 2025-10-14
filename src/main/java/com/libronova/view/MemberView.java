package com.libronova.view;

import com.libronova.controller.MemberController;
import com.libronova.controller.UserController;
import com.libronova.model.Member;
import com.libronova.model.User;

import javax.swing.JOptionPane;
import java.util.List;

public class MemberView {

    private final MemberController memberController;
    private final UserController userController;

    public MemberView() {
        this.memberController = new MemberController();
        this.userController = new UserController();
    }

    public void showMemberManagementMenu() {
        String[] options = {"List All Members", "Find Member by ID", "Find by Membership Number", "Update Member Details", "Delete Member", "Back to Main Menu"};
        int choice = -1;

        while (choice != 5) {
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an option for Member Management:",
                    "Member Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    listAllMembers();
                    break;
                case 1:
                    findMemberById();
                    break;
                case 2:
                    findMemberByMembershipNumber();
                    break;
                case 3:
                    updateMemberDetails();
                    break;
                case 4:
                    deleteMember();
                    break;
                case 5:
                    break;
                default:
                    choice = 5;
                    break;
            }
        }
    }

    private void listAllMembers() {
        List<User> membersAsUsers = memberController.getAllMembers();
        if (membersAsUsers.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No members found.", "Member List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("List of all members:\n\n");
        for (User user : membersAsUsers) {
            sb.append(formatMemberUserDisplay(user));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Member List", JOptionPane.PLAIN_MESSAGE);
    }

    private void findMemberById() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the member ID:", "Find Member", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Member member = memberController.getMemberById(id);

            if (member != null) {
                User user = userController.getUserById(member.getUserId());
                if (user != null) {
                    displayMemberUser(user, "Member Found");
                } else {
                    JOptionPane.showMessageDialog(null, "Associated user for member ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Member with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findMemberByMembershipNumber() {
        String number = JOptionPane.showInputDialog(null, "Enter the membership number:", "Find Member by Number", JOptionPane.PLAIN_MESSAGE);
        if (number == null || number.trim().isEmpty()) return;

        Member member = memberController.getMemberByMembershipNumber(number);

        if (member != null) {
            User user = userController.getUserById(member.getUserId());
            if (user != null) {
                displayMemberUser(user, "Member Found");
            } else {
                JOptionPane.showMessageDialog(null, "Associated user for membership number " + number + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Member with number " + number + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMemberDetails() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the member to update:", "Update Member Details", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Member member = memberController.getMemberById(id);

            if (member == null) {
                JOptionPane.showMessageDialog(null, "Member with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Only update Member-specific details
            String newPhone = JOptionPane.showInputDialog(null, "Enter new phone (current: " + (member.getPhone() != null ? member.getPhone() : "N/A") + "):", member.getPhone());
            String newStatus = JOptionPane.showInputDialog(null, "Enter new status (ACTIVE/INACTIVE) (current: " + member.getStatus() + "):", member.getStatus());

            member.setPhone(newPhone);
            member.setStatus(newStatus);

            Member updatedMember = memberController.updateMemberDetails(member);

            if (updatedMember != null) {
                JOptionPane.showMessageDialog(null, "Member details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update member details. Check logs for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the member to delete:", "Delete Member", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete member with ID " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = memberController.deleteMember(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(null, "Member deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete member. The member might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayMemberUser(User user, String title) {
        JOptionPane.showMessageDialog(null, formatMemberUserDisplay(user), title, JOptionPane.PLAIN_MESSAGE);
    }

    private String formatMemberUserDisplay(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- User Details ---\n");
        sb.append("ID: ").append(user.getId()).append("\n");
        sb.append("Username: ").append(user.getUsername()).append("\n");
        sb.append("Full Name: ").append(user.getFullName()).append("\n");
        sb.append("Email: ").append(user.getEmail()).append("\n");
        sb.append("Role: ").append(user.getRole()).append("\n");
        sb.append("Active: ").append(user.isActive()).append("\n");

        if (user.getMember() != null) {
            Member member = user.getMember();
            sb.append("\n--- Member Details ---\n");
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
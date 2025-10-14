package com.libronova.view;

import com.libronova.controller.MemberController;
import com.libronova.model.Member;

import javax.swing.JOptionPane;
import java.util.List;

public class MemberView {

    private final MemberController memberController;

    public MemberView() {
        this.memberController = new MemberController();
    }

    public void showMemberManagementMenu() {
        String[] options = {"List All Members", "Find Member by ID", "Find by Membership Number", "Add New Member", "Update Member", "Delete Member", "Back to Main Menu"};
        int choice = -1;

        while (choice != 6) {
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
                    addNewMember();
                    break;
                case 4:
                    updateMember();
                    break;
                case 5:
                    deleteMember();
                    break;
                case 6:
                    break;
                default:
                    choice = 6;
                    break;
            }
        }
    }

    private void listAllMembers() {
        List<Member> members = memberController.getAllMembers();
        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No members found.", "Member List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("List of all members:\n\n");
        for (Member member : members) {
            sb.append(String.format("ID: %d, Name: %s, Number: %s, Email: %s, Status: %s\n",
                    member.getId(), member.getFullName(), member.getMembershipNumber(), member.getEmail(), member.getStatus()));
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
                String memberInfo = String.format("ID: %d\nName: %s\nNumber: %s\nEmail: %s\nPhone: %s\nStatus: %s\nRegistration: %s",
                        member.getId(), member.getFullName(), member.getMembershipNumber(), member.getEmail(),
                        member.getPhone(), member.getStatus(), member.getRegistrationDate());
                JOptionPane.showMessageDialog(null, memberInfo, "Member Found", JOptionPane.INFORMATION_MESSAGE);
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
            String memberInfo = String.format("ID: %d\nName: %s\nNumber: %s\nEmail: %s\nPhone: %s\nStatus: %s\nRegistration: %s",
                    member.getId(), member.getFullName(), member.getMembershipNumber(), member.getEmail(),
                    member.getPhone(), member.getStatus(), member.getRegistrationDate());
            JOptionPane.showMessageDialog(null, memberInfo, "Member Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Member with number " + number + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewMember() {
        String fullName = JOptionPane.showInputDialog(null, "Enter Full Name:", "Add New Member", JOptionPane.PLAIN_MESSAGE);
        String email = JOptionPane.showInputDialog(null, "Enter Email:", "Add New Member", JOptionPane.PLAIN_MESSAGE);
        String phone = JOptionPane.showInputDialog(null, "Enter Phone Number:", "Add New Member", JOptionPane.PLAIN_MESSAGE);

        if (fullName == null || email == null) return;

        Member newMember = new Member();
        newMember.setFullName(fullName);
        newMember.setEmail(email);
        newMember.setPhone(phone);

        Member createdMember = memberController.createMember(newMember);

        if (createdMember != null) {
            JOptionPane.showMessageDialog(null, "Member added successfully!\nMembership Number: " + createdMember.getMembershipNumber(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to add member. Email might already exist or data is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the member to update:", "Update Member", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Member member = memberController.getMemberById(id);

            if (member == null) {
                JOptionPane.showMessageDialog(null, "Member with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fullName = JOptionPane.showInputDialog(null, "Enter new full name (current: " + member.getFullName() + "):", member.getFullName());
            String email = JOptionPane.showInputDialog(null, "Enter new email (current: " + member.getEmail() + "):", member.getEmail());
            String phone = JOptionPane.showInputDialog(null, "Enter new phone (current: " + member.getPhone() + "):", member.getPhone());

            member.setFullName(fullName);
            member.setEmail(email);
            member.setPhone(phone);

            Member updatedMember = memberController.updateMember(member);

            if (updatedMember != null) {
                JOptionPane.showMessageDialog(null, "Member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update member. Check logs for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the member to delete (mark as inactive):", "Delete Member", JOptionPane.PLAIN_MESSAGE);
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
}

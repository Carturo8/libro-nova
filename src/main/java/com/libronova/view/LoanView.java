package com.libronova.view;

import com.libronova.controller.LoanController;
import com.libronova.model.Loan;

import javax.swing.JOptionPane;
import java.util.List;

public class LoanView {

    private final LoanController loanController;

    public LoanView() {
        this.loanController = new LoanController();
    }

    public void showLoanManagementMenu() {
        String[] options = {"Create New Loan", "Return a Book", "List All Loans", "List Overdue Loans", "Find Loans by Member", "Back to Main Menu"};
        int choice = -1;

        while (choice != 5) {
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an option for Loan Management:",
                    "Loan Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    createLoan();
                    break;
                case 1:
                    returnBook();
                    break;
                case 2:
                    listAllLoans();
                    break;
                case 3:
                    listOverdueLoans();
                    break;
                case 4:
                    findLoansByMember();
                    break;
                case 5:
                    break;
                default:
                    choice = 5;
                    break;
            }
        }
    }

    private void createLoan() {
        try {
            String memberIdStr = JOptionPane.showInputDialog(null, "Enter Member ID:", "Create Loan", JOptionPane.PLAIN_MESSAGE);
            if (memberIdStr == null) return;

            String bookIdStr = JOptionPane.showInputDialog(null, "Enter Book ID:", "Create Loan", JOptionPane.PLAIN_MESSAGE);
            if (bookIdStr == null) return;

            int memberId = Integer.parseInt(memberIdStr);
            int bookId = Integer.parseInt(bookIdStr);

            Loan createdLoan = loanController.createLoan(bookId, memberId, null);

            if (createdLoan != null) {
                JOptionPane.showMessageDialog(null, "Loan created successfully!\nDue Date: " + createdLoan.getDueDate(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create loan. Check member status, book availability, or if the member has overdue loans.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        String loanIdStr = JOptionPane.showInputDialog(null, "Enter the Loan ID to return:", "Return Book", JOptionPane.PLAIN_MESSAGE);
        if (loanIdStr == null) return;

        try {
            int loanId = Integer.parseInt(loanIdStr);
            Loan returnedLoan = loanController.returnBook(loanId);

            if (returnedLoan != null) {
                String message = "Book returned successfully!";
                if (returnedLoan.getFineAmount() != null && returnedLoan.getFineAmount().doubleValue() > 0) {
                    message += "\nFine to be paid: $" + returnedLoan.getFineAmount();
                }
                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to return book. The loan may not exist or may have already been returned.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listAllLoans() {
        List<Loan> loans = loanController.getAllLoans();
        displayLoans(loans, "List of All Loans");
    }

    private void listOverdueLoans() {
        List<Loan> loans = loanController.getOverdueLoans();
        displayLoans(loans, "List of Overdue Loans");
    }

    private void findLoansByMember() {
        String memberIdStr = JOptionPane.showInputDialog(null, "Enter Member ID:", "Find Loans by Member", JOptionPane.PLAIN_MESSAGE);
        if (memberIdStr == null) return;

        try {
            int memberId = Integer.parseInt(memberIdStr);
            List<Loan> loans = loanController.getLoansByMember(memberId);
            displayLoans(loans, "Loans for Member ID: " + memberId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayLoans(List<Loan> loans, String title) {
        if (loans.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No loans found.", title, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder(title + ":\n\n");
        for (Loan loan : loans) {
            sb.append(String.format("ID: %d, BookID: %d, MemberID: %d, Status: %s, LoanDate: %s, DueDate: %s, ReturnDate: %s\n",
                    loan.getId(), loan.getBookId(), loan.getMemberId(), loan.getStatus(),
                    loan.getLoanDate(), loan.getDueDate(), loan.getReturnDate() == null ? "N/A" : loan.getReturnDate()));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), title, JOptionPane.PLAIN_MESSAGE);
    }
}

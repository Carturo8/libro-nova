package com.libronova.view;

import com.libronova.model.User;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class MainView {

    public void showMainMenu(User currentUser) {
        List<String> optionsList = new ArrayList<>();
        optionsList.add("Manage Books");
        optionsList.add("Manage Members");
        optionsList.add("Manage Loans");

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            optionsList.add("Manage Users");
        }
        optionsList.add("Logout");

        String[] options = optionsList.toArray(new String[0]);
        int choice = -1;
        int logoutIndex = options.length - 1; // CORREGIDO: Usar .length en lugar de .size()

        while (choice != logoutIndex) {
            String menuTitle = "LibroNova Main Menu | Welcome, " + currentUser.getFullName();
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Please select an option:",
                    menuTitle,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == -1) {
                choice = logoutIndex;
                continue;
            }

            String selectedOption = options[choice];

            switch (selectedOption) {
                case "Manage Books":
                    BookView bookView = new BookView();
                    bookView.showBookManagementMenu();
                    break;
                case "Manage Members":
                    MemberView memberView = new MemberView();
                    memberView.showMemberManagementMenu();
                    break;
                case "Manage Loans":
                    LoanView loanView = new LoanView();
                    loanView.showLoanManagementMenu();
                    break;
                case "Manage Users":
                    UserView userView = new UserView();
                    userView.showUserManagementMenu();
                    break;
                case "Logout":
                    break;
            }
        }
        JOptionPane.showMessageDialog(null, "You have been logged out.");
    }
}
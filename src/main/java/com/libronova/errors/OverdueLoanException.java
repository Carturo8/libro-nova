package com.libronova.errors;

public class OverdueLoanException extends RuntimeException {
    public OverdueLoanException(String memberName, int overdueCount) {
        super("Member '" + memberName + "' has " + overdueCount + " overdue loan(s) and cannot borrow more books");
    }

    public OverdueLoanException(String message) {
        super(message);
    }
}
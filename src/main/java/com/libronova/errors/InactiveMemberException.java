package com.libronova.errors;

public class InactiveMemberException extends RuntimeException {
    public InactiveMemberException(String memberName) {
        super("Member '" + memberName + "' is not active and cannot request loans");
    }

    public InactiveMemberException(String memberName, String status) {
        super("Member '" + memberName + "' has status " + status + " and cannot request loans");
    }
}
package com.libronova.service;

import com.libronova.model.Loan;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    Loan createLoan(Integer bookId, Integer memberId, Integer loanDays);
    Loan getLoanById(Integer id);
    List<Loan> getAllLoans();
    List<Loan> getAllActiveLoans();
    List<Loan> getLoansByMember(Integer memberId);
    List<Loan> getActiveLoansByMember(Integer memberId);
    List<Loan> getOverdueLoans();
    Loan returnBook(Integer loanId);
    boolean hasOverdueLoans(Integer memberId);
    BigDecimal calculateFine(Integer loanId);
    void updateOverdueLoansStatus();
}
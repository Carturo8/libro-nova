package com.libronova.dao;

import com.libronova.errors.DataAccessException;
import com.libronova.model.Loan;

import java.util.List;

public interface LoanDao {
    Loan create(Loan loan) throws DataAccessException;
    Loan findById(Integer id) throws DataAccessException;
    List<Loan> findAll() throws DataAccessException;
    List<Loan> findByMemberId(Integer memberId) throws DataAccessException;
    List<Loan> findActiveLoansByMemberId(Integer memberId) throws DataAccessException;
    List<Loan> findOverdueLoans() throws DataAccessException;
    List<Loan> findActiveLoans() throws DataAccessException;
    boolean update(Loan loan) throws DataAccessException;
    boolean returnLoan(Integer loanId) throws DataAccessException;
}
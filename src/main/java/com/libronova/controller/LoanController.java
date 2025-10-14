package com.libronova.controller;

import com.libronova.errors.*;
import com.libronova.model.Loan;
import com.libronova.service.LoanService;
import com.libronova.service.impl.LoanServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
    private final LoanService loanService;

    public LoanController() {
        this.loanService = new LoanServiceImpl();
    }

    public Loan createLoan(Integer bookId, Integer memberId, Integer loanDays) {
        try {
            logger.info("Attempting to create loan for book ID {} and member ID {}.", bookId, memberId);
            Loan createdLoan = loanService.createLoan(bookId, memberId, loanDays);
            logger.info("Loan created successfully with ID {}.", createdLoan.getId());
            return createdLoan;

        } catch (NotFoundException | InactiveMemberException | InsufficientStockException | OverdueLoanException e) {
            logger.warn("Loan creation failed: {}", e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred during loan creation.", e);
            return null;
        }
    }

    public Loan getLoanById(Integer id) {
        try {
            logger.debug("Attempting to find loan by ID: {}", id);
            return loanService.getLoanById(id);

        } catch (NotFoundException e) {
            logger.warn("Could not find loan with ID {}: {}", id, e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching loan with ID {}.", id, e);
            return null;
        }
    }

    public List<Loan> getAllLoans() {
        try {
            logger.debug("Attempting to fetch all loans.");
            return loanService.getAllLoans();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching all loans.", e);
            return Collections.emptyList();
        }
    }

    public List<Loan> getLoansByMember(Integer memberId) {
        try {
            logger.debug("Attempting to fetch loans for member ID: {}", memberId);
            return loanService.getLoansByMember(memberId);

        } catch (NotFoundException e) {
            logger.warn("Cannot get loans for member ID {}: {}", memberId, e.getMessage());
            return Collections.emptyList();
        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching loans for member ID {}.", memberId, e);
            return Collections.emptyList();
        }
    }

    public List<Loan> getOverdueLoans() {
        try {
            logger.debug("Attempting to fetch all overdue loans.");
            return loanService.getOverdueLoans();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching overdue loans.", e);
            return Collections.emptyList();
        }
    }

    public Loan returnBook(Integer loanId) {
        try {
            logger.info("Attempting to return book for loan ID: {}", loanId);
            Loan returnedLoan = loanService.returnBook(loanId);
            logger.info("Book for loan ID {} returned successfully.", loanId);
            return returnedLoan;

        } catch (NotFoundException | BadRequestException e) {
            logger.warn("Book return failed for loan ID {}: {}", loanId, e.getMessage());
            return null;
        } catch (ServiceException e) {
            logger.error("A service error occurred while returning book for loan ID {}.", loanId, e);
            return null;
        }
    }

    public void updateOverdueLoansStatus() {
        try {
            logger.info("Attempting to update status of all overdue loans.");
            loanService.updateOverdueLoansStatus();
            logger.info("Overdue loan status update process completed.");

        } catch (ServiceException e) {
            logger.error("A service error occurred while updating overdue loan statuses.", e);
        }
    }
}

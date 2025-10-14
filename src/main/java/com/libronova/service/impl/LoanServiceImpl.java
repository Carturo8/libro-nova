package com.libronova.service.impl;

import com.libronova.dao.BookDao;
import com.libronova.dao.LoanDao;
import com.libronova.dao.MemberDao;
import com.libronova.dao.UserDao;
import com.libronova.dao.impl.BookDaoImpl;
import com.libronova.dao.impl.LoanDaoImpl;
import com.libronova.dao.impl.MemberDaoImpl;
import com.libronova.dao.impl.UserDaoImpl;
import com.libronova.errors.*;
import com.libronova.model.Book;
import com.libronova.model.Loan;
import com.libronova.model.Member;
import com.libronova.model.User;
import com.libronova.service.LoanService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoanServiceImpl implements LoanService {

    private final LoanDao loanDao;
    private final BookDao bookDao;
    private final MemberDao memberDao;
    private final UserDao userDao;

    public LoanServiceImpl() {
        this.loanDao = new LoanDaoImpl();
        this.bookDao = new BookDaoImpl();
        this.memberDao = new MemberDaoImpl();
        this.userDao = new UserDaoImpl(); // INICIALIZADO
    }

    @Override
    public Loan createLoan(Integer bookId, Integer memberId, Integer loanDays) {

        try {
            Book book = bookDao.findById(bookId);
            if (book == null) {
                throw new NotFoundException("Book", bookId);
            }

            Member member = memberDao.findById(memberId);
            if (member == null) {
                throw new NotFoundException("Member", memberId);
            }

            User user = userDao.findById(member.getUserId());
            if (user == null) {
                throw new ServiceException("Associated user not found for member ID: " + memberId);
            }

            if (!"ACTIVE".equalsIgnoreCase(member.getStatus())) {
                throw new InactiveMemberException(user.getFullName(), member.getStatus()); // CORREGIDO
            }

            if (book.getAvailableCopies() <= 0) {
                throw new InsufficientStockException(book.getTitle());
            }

            if (hasOverdueLoans(memberId)) {
                List<Loan> overdueLoans = getOverdueLoansByMember(memberId);
                throw new OverdueLoanException(user.getFullName(), overdueLoans.size()); // CORREGIDO
            }

            if (loanDays == null || loanDays <= 0) {
                loanDays = 15;
            }

            Loan loan = new Loan();
            loan.setBookId(bookId);
            loan.setMemberId(memberId);
            loan.setLoanDate(LocalDate.now());
            loan.setDueDate(LocalDate.now().plusDays(loanDays));
            loan.setStatus("ACTIVE");
            loan.setFineAmount(BigDecimal.ZERO);

            Loan createdLoan = loanDao.create(loan);

            int newAvailableCopies = book.getAvailableCopies() - 1;
            bookDao.updateAvailableCopies(bookId, newAvailableCopies);

            return createdLoan;

        } catch (DataAccessException e) {
            throw new ServiceException("Error creating loan", e);
        }
    }

    @Override
    public Loan getLoanById(Integer id) {

        try {
            Loan loan = loanDao.findById(id);
            if (loan == null) {
                throw new NotFoundException("Loan", id);
            }

            return loan;

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving loan", e);
        }
    }

    @Override
    public List<Loan> getAllLoans() {

        try {
            return loanDao.findAll();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving all loans", e);
        }
    }

    @Override
    public List<Loan> getAllActiveLoans() {

        try {
            return loanDao.findActiveLoans();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving active loans", e);
        }
    }

    @Override
    public List<Loan> getLoansByMember(Integer memberId) {

        try {
            Member member = memberDao.findById(memberId);
            if (member == null) {
                throw new NotFoundException("Member", memberId);
            }

            return loanDao.findByMemberId(memberId);

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving loans by member", e);
        }
    }

    @Override
    public List<Loan> getActiveLoansByMember(Integer memberId) {

        try {
            Member member = memberDao.findById(memberId);
            if (member == null) {
                throw new NotFoundException("Member", memberId);
            }
            return loanDao.findActiveLoansByMemberId(memberId);

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving active loans by member", e);
        }
    }

    @Override
    public List<Loan> getOverdueLoans() {

        try {
            return loanDao.findOverdueLoans();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving overdue loans", e);
        }
    }

    @Override
    public Loan returnBook(Integer loanId) {

        try {
            Loan loan = loanDao.findById(loanId);
            if (loan == null) {
                throw new NotFoundException("Loan", loanId);
            }

            if ("RETURNED".equalsIgnoreCase(loan.getStatus())) {
                throw new BadRequestException("This loan has already been returned");
            }

            Book book = bookDao.findById(loan.getBookId());
            if (book == null) {
                throw new NotFoundException("Book", loan.getBookId());
            }

            loan.setReturnDate(LocalDate.now());
            loan.setStatus("RETURNED");

            BigDecimal fine = calculateFine(loanId);
            loan.setFineAmount(fine);

            loanDao.update(loan);

            int newAvailableCopies = book.getAvailableCopies() + 1;
            bookDao.updateAvailableCopies(loan.getBookId(), newAvailableCopies);

            return loanDao.findById(loanId);

        } catch (DataAccessException e) {
            throw new ServiceException("Error returning book", e);
        }
    }

    @Override
    public boolean hasOverdueLoans(Integer memberId) {

        try {
            List<Loan> overdueLoans = getOverdueLoansByMember(memberId);
            return !overdueLoans.isEmpty();

        } catch (DataAccessException e) {
            throw new ServiceException("Error checking overdue loans", e);
        }
    }

    @Override
    public BigDecimal calculateFine(Integer loanId) {

        try {
            Loan loan = loanDao.findById(loanId);
            if (loan == null) {
                throw new NotFoundException("Loan", loanId);
            }

            LocalDate returnDate = loan.getReturnDate() != null ? loan.getReturnDate() : LocalDate.now();
            LocalDate dueDate = loan.getDueDate();

            if (returnDate.isAfter(dueDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
                return BigDecimal.valueOf(daysOverdue);
            }

            return BigDecimal.ZERO;

        } catch (DataAccessException e) {
            throw new ServiceException("Error calculating fine", e);
        }
    }

    @Override
    public void updateOverdueLoansStatus() {

        try {
            List<Loan> overdueLoans = loanDao.findOverdueLoans();
            for (Loan loan : overdueLoans) {
                if ("ACTIVE".equalsIgnoreCase(loan.getStatus())) {
                    loan.setStatus("OVERDUE");
                    BigDecimal fine = calculateFine(loan.getId());
                    loan.setFineAmount(fine);
                    loanDao.update(loan);
                }
            }

        } catch (DataAccessException e) {
            throw new ServiceException("Error updating overdue loans status", e);
        }
    }

    private List<Loan> getOverdueLoansByMember(Integer memberId) throws DataAccessException {
        List<Loan> allLoans = loanDao.findActiveLoansByMemberId(memberId);

        return allLoans.stream()
                .filter(loan -> loan.getDueDate().isBefore(LocalDate.now()))
                .toList();
    }
}

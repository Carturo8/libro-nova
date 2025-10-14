package com.libronova.dao.impl;

import com.libronova.config.DatabaseConfig;
import com.libronova.dao.LoanDao;
import com.libronova.errors.DataAccessException;
import com.libronova.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDaoImpl implements LoanDao {

    @Override
    public Loan create(Loan loan) throws DataAccessException {
        String sql = "INSERT INTO loans (book_id, member_id, loan_date, due_date, " +
                "return_date, status, fine_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));

            if (loan.getReturnDate() != null) {
                stmt.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, loan.getStatus());
            stmt.setBigDecimal(7, loan.getFineAmount());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating loan failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getInt(1));
                    return loan;
                } else {
                    throw new DataAccessException("Creating loan failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating loan: " + e.getMessage(), e);
        }
    }

    @Override
    public Loan findById(Integer id) throws DataAccessException {
        String sql = "SELECT * FROM loans WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding loan by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> findAll() throws DataAccessException {
        String sql = "SELECT * FROM loans ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all loans: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> findByMemberId(Integer memberId) throws DataAccessException {
        String sql = "SELECT * FROM loans WHERE member_id = ? ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding loans by member ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> findActiveLoansByMemberId(Integer memberId) throws DataAccessException {
        String sql = "SELECT * FROM loans WHERE member_id = ? AND status = 'ACTIVE' ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding active loans by member ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> findOverdueLoans() throws DataAccessException {
        String sql = "SELECT * FROM loans WHERE status IN ('ACTIVE', 'OVERDUE') AND due_date < CURRENT_DATE " +
                "ORDER BY due_date";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding overdue loans: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> findActiveLoans() throws DataAccessException {
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE' ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding active loans: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Loan loan) throws DataAccessException {
        String sql = "UPDATE loans SET book_id = ?, member_id = ?, loan_date = ?, due_date = ?, " +
                "return_date = ?, status = ?, fine_amount = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));

            if (loan.getReturnDate() != null) {
                stmt.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, loan.getStatus());
            stmt.setBigDecimal(7, loan.getFineAmount());
            stmt.setInt(8, loan.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error updating loan: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean returnLoan(Integer loanId) throws DataAccessException {
        String sql = "UPDATE loans SET return_date = CURRENT_DATE, status = 'RETURNED' WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loanId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error returning loan: " + e.getMessage(), e);
        }
    }

    // Helper method to map ResultSet to Loan object.
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setMemberId(rs.getInt("member_id"));

        Date loanDate = rs.getDate("loan_date");
        if (loanDate != null) {
            loan.setLoanDate(loanDate.toLocalDate());
        }

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            loan.setDueDate(dueDate.toLocalDate());
        }

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }

        loan.setStatus(rs.getString("status"));
        loan.setFineAmount(rs.getBigDecimal("fine_amount"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            loan.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            loan.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return loan;
    }
}
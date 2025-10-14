package com.libronova.dao.impl;

import com.libronova.config.DatabaseConfig;
import com.libronova.dao.MemberDao;
import com.libronova.errors.DataAccessException;
import com.libronova.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDaoImpl implements MemberDao {

    @Override
    public Member create(Member member) throws DataAccessException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return create(member, conn);

        } catch (SQLException e) {
            throw new DataAccessException("Error getting connection for member creation: " + e.getMessage(), e);
        }
    }

    @Override
    public Member create(Member member, Connection conn) throws DataAccessException {
        String sql = "INSERT INTO members (user_id, membership_number, phone, status, registration_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, member.getUserId());
            stmt.setString(2, member.getMembershipNumber());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getStatus());
            stmt.setDate(5, Date.valueOf(member.getRegistrationDate()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating member failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getInt(1));
                    return member;
                } else {
                    throw new DataAccessException("Creating member failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error creating member: " + e.getMessage(), e);
        }
    }

    @Override
    public Member findById(Integer id) throws DataAccessException {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding member by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Member findByUserId(Integer userId) throws DataAccessException {
        String sql = "SELECT * FROM members WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding member by user ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Member findByMembershipNumber(String membershipNumber) throws DataAccessException {
        String sql = "SELECT * FROM members WHERE membership_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, membershipNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding member by membership number: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Member> findAll() throws DataAccessException {
        String sql = "SELECT * FROM members ORDER BY registration_date DESC";
        List<Member> members = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }

            return members;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all members: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Member> findAllActive() throws DataAccessException {
        String sql = "SELECT * FROM members WHERE status = 'ACTIVE' ORDER BY registration_date DESC";
        List<Member> members = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }

            return members;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving active members: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Member member) throws DataAccessException {
        String sql = "UPDATE members SET membership_number = ?, phone = ?, status = ?, registration_date = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getMembershipNumber());
            stmt.setString(2, member.getPhone());
            stmt.setString(3, member.getStatus());
            stmt.setDate(4, Date.valueOf(member.getRegistrationDate()));
            stmt.setInt(5, member.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error updating member: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DataAccessException {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting member: " + e.getMessage(), e);
        }
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setUserId(rs.getInt("user_id"));
        member.setMembershipNumber(rs.getString("membership_number"));
        member.setPhone(rs.getString("phone"));
        member.setStatus(rs.getString("status"));

        Date registrationDate = rs.getDate("registration_date");
        if (registrationDate != null) {
            member.setRegistrationDate(registrationDate.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            member.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            member.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return member;
    }
}

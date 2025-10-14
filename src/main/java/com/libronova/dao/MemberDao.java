package com.libronova.dao;

import com.libronova.errors.DataAccessException;
import com.libronova.model.Member;

import java.sql.Connection;
import java.util.List;

public interface MemberDao {
    Member create(Member member, Connection conn) throws DataAccessException;
    Member create(Member member) throws DataAccessException;
    Member findById(Integer id) throws DataAccessException;
    Member findByUserId(Integer userId) throws DataAccessException;
    Member findByMembershipNumber(String membershipNumber) throws DataAccessException;
    List<Member> findAll() throws DataAccessException;
    List<Member> findAllActive() throws DataAccessException;
    boolean update(Member member) throws DataAccessException;
    boolean delete(Integer id) throws DataAccessException;
}
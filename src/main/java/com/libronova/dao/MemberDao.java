package com.libronova.dao;

import com.libronova.errors.DataAccessException;
import com.libronova.model.Member;

import java.util.List;

public interface MemberDao {
    Member create(Member member) throws DataAccessException;
    Member findById(Integer id) throws DataAccessException;
    Member findByMembershipNumber(String membershipNumber) throws DataAccessException;
    Member findByEmail(String email) throws DataAccessException;
    List<Member> findAll() throws DataAccessException;
    List<Member> findAllActive() throws DataAccessException;
    boolean update(Member member) throws DataAccessException;
    boolean delete(Integer id) throws DataAccessException;
}
package com.libronova.dao;

import com.libronova.errors.DataAccessException;
import com.libronova.model.User;

import java.util.List;

public interface UserDao {
    User create(User user) throws DataAccessException;
    User findById(Integer id) throws DataAccessException;
    User findByUsername(String username) throws DataAccessException;
    User findByEmail(String email) throws DataAccessException;
    List<User> findAll() throws DataAccessException;
    List<User> findAllActive() throws DataAccessException;
    User validateCredentials(String username, String password) throws DataAccessException;
    boolean update(User user) throws DataAccessException;
    boolean delete(Integer id) throws DataAccessException;
}
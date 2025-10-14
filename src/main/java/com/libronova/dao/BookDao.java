package com.libronova.dao;

import com.libronova.errors.DataAccessException;
import com.libronova.model.Book;

import java.util.List;

public interface BookDao {
    Book create(Book book) throws DataAccessException;
    Book findById(Integer id) throws DataAccessException;
    Book findByIsbn(String isbn) throws DataAccessException;
    List<Book> findAll() throws DataAccessException;
    List<Book> findAllActive() throws DataAccessException;
    List<Book> searchByTitle(String title) throws DataAccessException;
    boolean update(Book book) throws DataAccessException;
    boolean delete(Integer id) throws DataAccessException;
    boolean updateAvailableCopies(Integer bookId, Integer availableCopies) throws DataAccessException;
}
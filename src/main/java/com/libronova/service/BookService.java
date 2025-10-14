package com.libronova.service;

import com.libronova.model.Book;

import java.util.List;

public interface BookService {
    Book createBook(Book book);
    Book getBookById(Integer id);
    Book getBookByIsbn(String isbn);
    List<Book> getAllBooks();
    List<Book> getAllActiveBooks();
    List<Book> searchBooksByTitle(String title);
    Book updateBook(Book book);
    void deleteBook(Integer id);
    boolean hasAvailableCopies(Integer bookId);
    void decreaseAvailableCopies(Integer bookId);
    void increaseAvailableCopies(Integer bookId);
}
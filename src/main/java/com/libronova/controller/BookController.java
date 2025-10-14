package com.libronova.controller;

import com.libronova.errors.*;
import com.libronova.model.Book;
import com.libronova.service.BookService;
import com.libronova.service.impl.BookServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController() {
        this.bookService = new BookServiceImpl();
    }

    public Book createBook(Book book) {
        try {
            logger.info("Attempting to create a new book with ISBN: {}", book.getIsbn());
            Book createdBook = bookService.createBook(book);
            logger.info("Book '{}' created successfully with ID {}.", createdBook.getTitle(), createdBook.getId());
            return createdBook;

        } catch (ConflictException | BadRequestException | ServiceException e) {
            logger.warn("Book creation failed: {}", e.getMessage());
            return null;
        }
    }

    public Book getBookById(Integer id) {
        try {
            logger.debug("Attempting to find book by ID: {}", id);
            return bookService.getBookById(id);

        } catch (NotFoundException | ServiceException e) {
            logger.warn("Could not find book with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public Book getBookByIsbn(String isbn) {
        try {
            logger.debug("Attempting to find book by ISBN: {}", isbn);
            return bookService.getBookByIsbn(isbn);

        } catch (NotFoundException | BadRequestException | ServiceException e) {
            logger.warn("Could not find book with ISBN {}: {}", isbn, e.getMessage());
            return null;
        }
    }

    public List<Book> getAllBooks() {
        try {
            logger.debug("Attempting to fetch all books.");
            return bookService.getAllBooks();

        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching all books.", e);
            return Collections.emptyList();
        }
    }

    public List<Book> getAllActiveBooks() {
        try {
            logger.debug("Attempting to fetch all active books.");
            return bookService.getAllActiveBooks();
        } catch (ServiceException e) {
            logger.error("A service error occurred while fetching all active books.", e);
            return Collections.emptyList();
        }
    }

    public List<Book> searchBooksByTitle(String title) {
        try {
            logger.debug("Attempting to search books by title: '{}'", title);
            return bookService.searchBooksByTitle(title);

        } catch (BadRequestException | ServiceException e) {
            logger.warn("Book search failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Book updateBook(Book book) {
        try {
            logger.info("Attempting to update book with ID: {}", book.getId());
            Book updatedBook = bookService.updateBook(book);
            logger.info("Book with ID {} updated successfully.", book.getId());
            return updatedBook;

        } catch (NotFoundException | ConflictException | BadRequestException | ServiceException e) {
            logger.warn("Book update failed for book ID {}: {}", book.getId(), e.getMessage());
            return null;
        }
    }

    public boolean deleteBook(Integer id) {
        try {
            logger.info("Attempting to delete book with ID: {}", id);
            bookService.deleteBook(id);
            logger.info("Book with ID {} marked as inactive successfully.", id);
            return true;

        } catch (NotFoundException | ServiceException e) {
            logger.warn("Could not delete book with ID {}: {}", id, e.getMessage());
            return false;
        }
    }
}

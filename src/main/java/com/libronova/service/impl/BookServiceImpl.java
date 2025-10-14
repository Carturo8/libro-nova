package com.libronova.service.impl;

import com.libronova.dao.BookDao;
import com.libronova.dao.impl.BookDaoImpl;
import com.libronova.errors.*;
import com.libronova.model.Book;
import com.libronova.service.BookService;

import java.util.List;

public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    public BookServiceImpl() {
        this.bookDao = new BookDaoImpl();
    }

    @Override
    public Book createBook(Book book) {
        try {
            // Validate required fields
            validateBookData(book);

            // Validate ISBN uniqueness
            Book existingBook = bookDao.findByIsbn(book.getIsbn());
            if (existingBook != null) {
                throw new ConflictException("Book", "ISBN", book.getIsbn());
            }

            // Validate stock consistency
            if (book.getAvailableCopies() > book.getTotalCopies()) {
                throw new BadRequestException("Available copies cannot exceed total copies");
            }

            // Set default values
            if (book.getTotalCopies() == null) {
                book.setTotalCopies(1);
            }
            if (book.getAvailableCopies() == null) {
                book.setAvailableCopies(book.getTotalCopies());
            }

            return bookDao.create(book);

        } catch (DataAccessException e) {
            throw new ServiceException("Error creating book", e);
        }
    }

    @Override
    public Book getBookById(Integer id) {

        try {
            Book book = bookDao.findById(id);
            if (book == null) {
                throw new NotFoundException("Book", id);
            }

            return book;

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving book", e);
        }
    }

    @Override
    public Book getBookByIsbn(String isbn) {

        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new BadRequestException("ISBN cannot be empty");
            }

            Book book = bookDao.findByIsbn(isbn);
            if (book == null) {
                throw new NotFoundException("Book with ISBN " + isbn + " not found");
            }

            return book;

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving book by ISBN", e);
        }
    }

    @Override
    public List<Book> getAllBooks() {

        try {
            return bookDao.findAll();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving all books", e);
        }
    }

    @Override
    public List<Book> getAllActiveBooks() {

        try {
            return bookDao.findAllActive();

        } catch (DataAccessException e) {
            throw new ServiceException("Error retrieving active books", e);
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {

        try {
            if (title == null || title.trim().isEmpty()) {
                throw new BadRequestException("Search title cannot be empty");
            }

            return bookDao.searchByTitle(title);

        } catch (DataAccessException e) {
            throw new ServiceException("Error searching books by title", e);
        }
    }

    @Override
    public Book updateBook(Book book) {

        try {
            // Validate book exists
            Book existingBook = bookDao.findById(book.getId());
            if (existingBook == null) {
                throw new NotFoundException("Book", book.getId());
            }

            // Validate required fields
            validateBookData(book);

            // Validate ISBN uniqueness (excluding current book)
            Book bookWithSameIsbn = bookDao.findByIsbn(book.getIsbn());
            if (bookWithSameIsbn != null && !bookWithSameIsbn.getId().equals(book.getId())) {
                throw new ConflictException("Book", "ISBN", book.getIsbn());
            }

            // Validate stock consistency
            if (book.getAvailableCopies() > book.getTotalCopies()) {
                throw new BadRequestException("Available copies cannot exceed total copies");
            }

            boolean updated = bookDao.update(book);
            if (!updated) {
                throw new ServiceException("Failed to update book");
            }

            return bookDao.findById(book.getId());

        } catch (DataAccessException e) {
            throw new ServiceException("Error updating book", e);
        }
    }

    @Override
    public void deleteBook(Integer id) {

        try {
            Book book = bookDao.findById(id);
            if (book == null) {
                throw new NotFoundException("Book", id);
            }

            boolean deleted = bookDao.delete(id);
            if (!deleted) {
                throw new ServiceException("Failed to delete book");
            }

        } catch (DataAccessException e) {
            throw new ServiceException("Error deleting book", e);
        }
    }

    @Override
    public boolean hasAvailableCopies(Integer bookId) {

        try {
            Book book = bookDao.findById(bookId);
            if (book == null) {
                throw new NotFoundException("Book", bookId);
            }

            return book.getAvailableCopies() > 0;

        } catch (DataAccessException e) {
            throw new ServiceException("Error checking book availability", e);
        }
    }

    @Override
    public void decreaseAvailableCopies(Integer bookId) {

        try {
            Book book = bookDao.findById(bookId);
            if (book == null) {
                throw new NotFoundException("Book", bookId);
            }

            if (book.getAvailableCopies() <= 0) {
                throw new InsufficientStockException(book.getTitle());
            }

            int newAvailableCopies = book.getAvailableCopies() - 1;
            boolean updated = bookDao.updateAvailableCopies(bookId, newAvailableCopies);

            if (!updated) {
                throw new ServiceException("Failed to update available copies");
            }

        } catch (DataAccessException e) {
            throw new ServiceException("Error decreasing available copies", e);
        }
    }

    @Override
    public void increaseAvailableCopies(Integer bookId) {

        try {
            Book book = bookDao.findById(bookId);
            if (book == null) {
                throw new NotFoundException("Book", bookId);
            }

            int newAvailableCopies = book.getAvailableCopies() + 1;

            // Validate not exceeding total copies
            if (newAvailableCopies > book.getTotalCopies()) {
                throw new BadRequestException("Cannot increase available copies beyond total copies");
            }

            boolean updated = bookDao.updateAvailableCopies(bookId, newAvailableCopies);

            if (!updated) {
                throw new ServiceException("Failed to update available copies");
            }

        } catch (DataAccessException e) {
            throw new ServiceException("Error increasing available copies", e);
        }
    }

    private void validateBookData(Book book) {

        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new BadRequestException("ISBN", "cannot be empty");
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title", "cannot be empty");
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new BadRequestException("Author", "cannot be empty");
        }

        if (book.getTotalCopies() != null && book.getTotalCopies() < 0) {
            throw new BadRequestException("Total copies", "cannot be negative");
        }

        if (book.getAvailableCopies() != null && book.getAvailableCopies() < 0) {
            throw new BadRequestException("Available copies", "cannot be negative");
        }
    }
}
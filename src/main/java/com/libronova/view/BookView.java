package com.libronova.view;

import com.libronova.controller.BookController;
import com.libronova.model.Book;

import javax.swing.JOptionPane;
import java.util.List;

public class BookView {

    private final BookController bookController;

    public BookView() {
        this.bookController = new BookController();
    }

    public void showBookManagementMenu() {
        String[] options = {"List All Books", "Find Book by ID", "Find Book by ISBN", "Search by Title", "Add New Book", "Update Book", "Delete Book", "Back to Main Menu"};
        int choice = -1;

        while (choice != 7) {
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an option for Book Management:",
                    "Book Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0:
                    listAllBooks();
                    break;
                case 1:
                    findBookById();
                    break;
                case 2:
                    findBookByIsbn();
                    break;
                case 3:
                    searchByTitle();
                    break;
                case 4:
                    addNewBook();
                    break;
                case 5:
                    updateBook();
                    break;
                case 6:
                    deleteBook();
                    break;
                case 7:
                    break;
                default:
                    choice = 7;
                    break;
            }
        }
    }

    public void showAvailableBooksForMember() {
        List<Book> books = bookController.getAllActiveBooks();
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No active books found.", "Available Books", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Available Books:\n\n");
        for (Book book : books) {
            sb.append(String.format("ID: %d, Title: %s, Author: %s, ISBN: %s, Available: %d\n",
                    book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                    book.getAvailableCopies()));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Available Books", JOptionPane.PLAIN_MESSAGE);
    }

    private void listAllBooks() {
        List<Book> books = bookController.getAllBooks();
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No books found.", "Book List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("List of all books:\n\n");
        for (Book book : books) {
            sb.append(String.format("ID: %d, Title: %s, Author: %s, ISBN: %s, Available: %d/%d, Active: %s\n",
                    book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                    book.getAvailableCopies(), book.getTotalCopies(), book.isActive()));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Book List", JOptionPane.PLAIN_MESSAGE);
    }

    private void findBookById() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the book ID:", "Find Book", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Book book = bookController.getBookById(id);

            if (book != null) {
                String bookInfo = String.format("ID: %d\nTitle: %s\nAuthor: %s\nISBN: %s\nYear: %d\nGenre: %s\nCopies: %d/%d\nActive: %s",
                        book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPublicationYear(),
                        book.getGenre(), book.getAvailableCopies(), book.getTotalCopies(), book.isActive());
                JOptionPane.showMessageDialog(null, bookInfo, "Book Found", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Book with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findBookByIsbn() {
        String isbn = JOptionPane.showInputDialog(null, "Enter the book ISBN:", "Find Book by ISBN", JOptionPane.PLAIN_MESSAGE);
        if (isbn == null || isbn.trim().isEmpty()) return;

        Book book = bookController.getBookByIsbn(isbn);

        if (book != null) {
            String bookInfo = String.format("ID: %d\nTitle: %s\nAuthor: %s\nISBN: %s\nYear: %d\nGenre: %s\nCopies: %d/%d\nActive: %s",
                    book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPublicationYear(),
                    book.getGenre(), book.getAvailableCopies(), book.getTotalCopies(), book.isActive());
            JOptionPane.showMessageDialog(null, bookInfo, "Book Found", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(null, "Book with ISBN " + isbn + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByTitle() {
        String title = JOptionPane.showInputDialog(null, "Enter a title to search for:", "Search Books", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;

        List<Book> books = bookController.searchBooksByTitle(title);
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No books found matching that title.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Search results:\n\n");
        for (Book book : books) {
            sb.append(String.format("ID: %d, Title: %s, Author: %s, Available: %d/%d\n",
                    book.getId(), book.getTitle(), book.getAuthor(), book.getAvailableCopies(), book.getTotalCopies()));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Search Results", JOptionPane.PLAIN_MESSAGE);
    }

    private void addNewBook() {
        try {
            String isbn = JOptionPane.showInputDialog(null, "Enter ISBN:", "Add New Book", JOptionPane.PLAIN_MESSAGE);
            String title = JOptionPane.showInputDialog(null, "Enter Title:", "Add New Book", JOptionPane.PLAIN_MESSAGE);
            String author = JOptionPane.showInputDialog(null, "Enter Author:", "Add New Book", JOptionPane.PLAIN_MESSAGE);
            int pubYear = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Publication Year:", "Add New Book", JOptionPane.PLAIN_MESSAGE));
            String genre = JOptionPane.showInputDialog(null, "Enter Genre:", "Add New Book", JOptionPane.PLAIN_MESSAGE);
            int totalCopies = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Total Copies:", "Add New Book", JOptionPane.PLAIN_MESSAGE));

            Book newBook = new Book();
            newBook.setIsbn(isbn);
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setPublicationYear(pubYear);
            newBook.setGenre(genre);
            newBook.setTotalCopies(totalCopies);
            newBook.setAvailableCopies(totalCopies);
            newBook.setActive(true);

            Book createdBook = bookController.createBook(newBook);

            if (createdBook != null) {
                JOptionPane.showMessageDialog(null, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add book. ISBN might already exist or data is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number format for year or copies.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the book to update:", "Update Book", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Book book = bookController.getBookById(id);

            if (book == null) {
                JOptionPane.showMessageDialog(null, "Book with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String title = JOptionPane.showInputDialog(null, "Enter new title (current: " + book.getTitle() + "):", book.getTitle());
            String author = JOptionPane.showInputDialog(null, "Enter new author (current: " + book.getAuthor() + "):", book.getAuthor());
            int totalCopies = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter new total copies (current: " + book.getTotalCopies() + "):", book.getTotalCopies()));
            int availableCopies = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter new available copies (current: " + book.getAvailableCopies() + "):", book.getAvailableCopies()));

            book.setTitle(title);
            book.setAuthor(author);
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(availableCopies);

            Book updatedBook = bookController.updateBook(book);

            if (updatedBook != null) {
                JOptionPane.showMessageDialog(null, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update book. Check logs for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID or number format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the book to delete (mark as inactive):", "Delete Book", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete book with ID " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = bookController.deleteBook(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(null, "Book deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete book. It might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.bookstore.service.impl;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // Temporary storage for shared links
    private final Map<String, Long> shareableLinks = new HashMap<>();

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> addBooks(List<Book> books) {
        return bookRepository.saveAll(books);
    }
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Book existingBook = getBookById(id);
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book existingBook = getBookById(id);  // Check if the book exists before deletion
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> searchBooks(Long id, String title, String author) {
        if (id != null) {
            return bookRepository.findById(id)
                    .map(List::of)
                    .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        } else if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        } else {
            throw new InvalidInputException("At least one search parameter must be provided.");
        }
    }

    @Override
    public String generateShareableLink(Long bookId , String uniqueId) {
        // Validate book existence
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        // Generate a unique link

        shareableLinks.put(uniqueId, bookId);

        // Return the link
        return "http://yourdomain.com/api/books/shared/" + uniqueId;
    }


    @Override
    public Book getBookByShareableLink(String uniqueId) {
        // Get the book ID from the link
        Long bookId = shareableLinks.get(uniqueId);
        if (bookId == null) {
            throw new RuntimeException("Invalid or expired shareable link.");
        }

        // Retrieve the book
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
    }
}

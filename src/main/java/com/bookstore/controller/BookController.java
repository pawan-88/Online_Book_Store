package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        Validation.validateBook(book);
        Book book1 = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body("Book added Successfully: " + book1);
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> addBooks(@RequestBody List<Book> books) {
        books.forEach(Validation::validateBook);
        List<Book> addedBooks = bookService.addBooks(books);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedBooks.size() + " books added successfully.");
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Validation.validateId(id);
        Book book = bookService.getBookById(id);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book book1 = bookService.updateBook(id, book);
        return ResponseEntity.status(HttpStatus.OK).body("Updated Successfully: "+ book1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        Validation.validateId(id);
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Book with ID " + id + " deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ID " + id + " not found.");
        }
    }

    // Search API
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        Validation.validateSearchParameters(id, title, author, "Book");
        List<Book> books = bookService.searchBooks(id, title, author);
        return ResponseEntity.ok(books);
    }

    // Generate book link
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String,String>> shareBook(@PathVariable Long id) {
        try {
            String uniqueId = UUID.randomUUID().toString();
            String shareableLink = bookService.generateShareableLink(id,uniqueId);
            // Return the link and unique ID in the response body
            Map<String, String> response = new HashMap<>();
            response.put("shareableLink", shareableLink);
            response.put("uniqueId", uniqueId);

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Error: " + ex.getMessage()));
        }
    }

    // Get book name by using Unique Id
    @GetMapping("/shared/{uniqueId}")
    public ResponseEntity<Book> getSharedBook(@PathVariable String uniqueId) {
        try {
            Book book = bookService.getBookByShareableLink(uniqueId);
            return ResponseEntity.ok(book);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Validation.validateBook(book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.addBook(book));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Book>> addBooks(@RequestBody List<Book> books) {
        books.forEach(Validation::validateBook);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.addBooks(books));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Validation.validateId(id);
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Validation.validateId(id);
        bookService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
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

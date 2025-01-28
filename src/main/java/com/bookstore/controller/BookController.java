package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.BaseResponse;
import com.bookstore.util.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    @Autowired
    public void BookController(BookService bookService) {
        this.bookService = bookService;
    }

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<BookDTO>> addBook(@RequestBody Book book) {
        String requestId = null;
        try {
            BookDTO savedBook = bookService.addBook(book);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new BaseResponse<>(
                            requestId,
                            savedBook,
                            new BaseResponse.ResponseMessage("201", "Book added successfully.", null)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
                    )
            );
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<BaseResponse<List<Book>>> addBooks(@RequestBody List<Book> books) {
        String requestId = null;
        try {
            books.forEach(Validation::validateBook);
            List<Book> addedBooks = bookService.addBooks(books);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new BaseResponse<>(
                            requestId,
                            addedBooks,
                            new BaseResponse.ResponseMessage("201", addedBooks.size() + " books added successfully.", null)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
                    )
            );
        }
    }

    // API: Get Book Location
//    @GetMapping("/{id}/location")
//    public ResponseEntity<BaseResponse<String>> getBookLocation(@PathVariable Long id) {
//        String requestId = null;
//        try {
//            Book book = bookService.getBookById(id);
//            if (book.getBookLocation() == null) {
//                throw new RuntimeException("Book location not assigned.");
//            }
//            return ResponseEntity.ok(
//                    new BaseResponse<>(
//                            requestId,
//                            book.getBookLocation(),
//                            new BaseResponse.ResponseMessage("200", "Book location retrieved successfully.", null)
//                    )
//            );
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new BaseResponse<>(
//                            requestId,
//                            null,
//                            new BaseResponse.ResponseMessage("404", e.getMessage(), null)
//                    )
//            );
//        }
//    }

// API: Allocate Book to Rack
//    @PostMapping("/{id}/allocate")
//    public ResponseEntity<BaseResponse<String>> allocateBookToRack(
//            @PathVariable Long id,
//            @RequestParam Long rackId
//    ) {
//        String requestId = null;
//        try {
//            bookService.allocateBookToRack(id, rackId);
//            return ResponseEntity.ok(
//                    new BaseResponse<>(
//                            requestId,
//                            "Book allocated to rack successfully.",
//                            new BaseResponse.ResponseMessage("200", "Book allocated to rack successfully.", null)
//                    )
//            );
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new BaseResponse<>(
//                            requestId,
//                            null,
//                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
//                    )
//            );
//        }
//    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Book>>> getAllBooks() {
        String requestId = null;
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(
                new BaseResponse<>(
                        requestId,
                        books,
                        new BaseResponse.ResponseMessage("200", "Books retrieved successfully.", null)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BookDTO>> getBookById(@PathVariable Long id) {
        String requestId = null;
        try {
            Validation.validateId(id);
            BookDTO bookdto = bookService.getBookById(id);
            return ResponseEntity.ok(
                    new BaseResponse<>(
                            requestId,
                            bookdto,
                            new BaseResponse.ResponseMessage("200", "Book retrieved successfully.", null)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("404", e.getMessage(), null)
                    )
            );
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<BaseResponse<Book>> updateBook(@PathVariable Long id, @RequestBody Book book) {
//        String requestId = null;
//        try {
//            Book updatedBook = bookService.updateBook(id, book);
//            return ResponseEntity.ok(
//                    new BaseResponse<>(
//                            requestId,
//                            updatedBook,
//                            new BaseResponse.ResponseMessage("200", "Book updated successfully.", null)
//                    )
//            );
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new BaseResponse<>(
//                            requestId,
//                            null,
//                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
//                    )
//            );
//        }
//    }
//
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteBook(@PathVariable Long id) {
        String requestId = null;
        try {
            Validation.validateId(id); // Ensure the ID is valid
            boolean isSoftDeleted = bookService.deleteBook(id);

            if (isSoftDeleted) {
                return ResponseEntity.ok(
                        new BaseResponse<>(
                                requestId,
                                "Book with ID " + id + " is now inactive.",
                                new BaseResponse.ResponseMessage("200", "Book marked as inactive.", null)
                        )
                );
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("404", e.getMessage(), null)
                    )
            );
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<Book>>> searchBooks(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author
    ) {
        String requestId = null;
        try {
            Validation.validateSearchParameters(id, title, author, "Book");
            List<Book> books = bookService.searchBooks(id, title, author);

            return ResponseEntity.ok(
                    new BaseResponse<>(
                            requestId,
                            books,
                            new BaseResponse.ResponseMessage("200", "Books retrieved successfully.", null)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
                    )
            );
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<BaseResponse<Map<String, String>>> shareBook(@PathVariable Long id) {
        String requestId = null;
        try {
            String uniqueId = null;
            String shareableLink = bookService.generateShareableLink(id, uniqueId);

            Map<String, String> response = new HashMap<>();
            response.put("shareableLink", shareableLink);
            response.put("uniqueId", uniqueId);

            return ResponseEntity.ok(
                    new BaseResponse<>(
                            requestId,
                            response,
                            new BaseResponse.ResponseMessage("200", "Shareable link generated successfully.", null)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("404", e.getMessage(), null)
                    )
            );
        }
    }

    @GetMapping("/shared/{uniqueId}")
    public ResponseEntity<BaseResponse<Book>> getSharedBook(@PathVariable String uniqueId) {
        String requestId = null;
        try {
            Book book = bookService.getBookByShareableLink(uniqueId);
            return ResponseEntity.ok(
                    new BaseResponse<>(
                            requestId,
                            book,
                            new BaseResponse.ResponseMessage("200", "Book retrieved successfully.", null)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("404", e.getMessage(), null)
                    )
            );
        }
    }
}

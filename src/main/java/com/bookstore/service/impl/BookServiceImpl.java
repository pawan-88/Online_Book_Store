package com.bookstore.service.impl;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Block;
import com.bookstore.model.Book;
import com.bookstore.model.Rack;
import com.bookstore.model.Warehouse;
import com.bookstore.repository.BlockRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.RackRepository;
import com.bookstore.repository.WarehouseRepository;
import com.bookstore.service.BookService;
import com.bookstore.util.Validation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final RackRepository rackRepository;
    private final WarehouseRepository warehouseRepository;
    private final BlockRepository blockRepository;

    // Temporary storage for shared links
    private final Map<String, Long> shareableLinks = new HashMap<>();

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, RackRepository rackRepository, WarehouseRepository warehouseRepository, BlockRepository blockRepository) {
        this.bookRepository = bookRepository;
        this.rackRepository = rackRepository;
        this.warehouseRepository = warehouseRepository;
        this.blockRepository = blockRepository;
    }

    @Transactional
    @Override
    public Book addBook(Book book) {
        // Handle Warehouse
        Warehouse warehouse = warehouseRepository.findByName(book.getWarehouse().getName());
        if (warehouse == null) {
            warehouse = new Warehouse();
            warehouse.setName(book.getWarehouse().getName());
            warehouse.setLocation(book.getWarehouse().getLocation());
            warehouse = warehouseRepository.save(warehouse); // Save warehouse first
        } else if (warehouse.getLocation() == null) {
            warehouse.setLocation(book.getWarehouse().getLocation()); // Update existing warehouse with a location
            warehouse = warehouseRepository.save(warehouse); // Save updated warehouse
        }

        // Handle Block
        Block block = blockRepository.findByNameAndWarehouseId(book.getBlock().getName(), warehouse.getId());
        if (block == null) {
            block = new Block();
            block.setName(book.getBlock().getName());
            block.setWarehouse(warehouse); // Associate block with warehouse
            block = blockRepository.save(block); // Save block first
        }

        // Handle Rack
        Rack rack = rackRepository.findByRackNumberAndBlockId(book.getRack().getRackNumber(), block.getId());
        if (rack == null) {
            // Create a new Rack if not found
            rack = new Rack();
            rack.setRackNumber(book.getRack().getRackNumber());
            rack.setBlock(block); // Associate rack with block
            rack.setBook(book);   // Associate rack with book
            rack.setIsAvailable(false); // Rack is now associated with a book, mark as unavailable
            rack = rackRepository.save(rack); // Save the new Rack
        } else {
            // Update the existing Rack to associate with the new Book
            rack.setBook(book);    // Set the new book
            rack.setIsAvailable(false); // Mark rack as unavailable
            rack = rackRepository.save(rack); // Save the updated Rack
        }

        // Associate saved entities with the Book
        book.setWarehouse(warehouse);
        book.setBlock(block);
        book.setId(book.getId());
        book.setRack(rack);

        // Save the Book
        return bookRepository.save(book); // Save the book after all relationships are established
    }

    // Allocate Book to Rack
//    @Override
//    public Book allocateBookToRack(Long bookId, Long rackId) {
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
//        Rack rack = rackRepository.findById(rackId)
//                .orElseThrow(() -> new RuntimeException("Rack not found with ID: " + rackId));
//        if (!rack.isAvailable()) {
//            throw new RuntimeException("Rack is not available.");
//        }
//
//        // Update rack and book
//        rack.setAvailable(false);
//        rack.setBook(book);
//        book.setBookLocation(
//                "Warehouse:" + rack.getBlock().getWarehouse().getId() +
//                        ", Block:" + rack.getBlock().getName() +
//                        ", Rack:" + rack.getRackNumber()
//        );
//        rackRepository.save(rack);
//        bookRepository.save(book);
//        return book;
//    }


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
    @Transactional
    public Boolean deleteBook(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (!"INACTIVE".equals(book.getStatus())) {
                book.setStatus("INACTIVE"); // Mark the book as inactive
                bookRepository.save(book); // Update the book in the database
                return true;
            } else {
                throw new RuntimeException("Book with ID " + id + " is already inactive.");
            }
        } else {
            throw new RuntimeException("Book with ID " + id + " not found.");
        }
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

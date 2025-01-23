package com.bookstore.service.impl;

import com.bookstore.dto.BlockDTO;
import com.bookstore.dto.BookDTO;
import com.bookstore.dto.RackDTO;
import com.bookstore.dto.WarehouseDTO;
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
    public BookDTO addBook(Book book) {
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
        Book savedBook = bookRepository.save(book); // Save the book after all relationships are established

        // Convert Book entity to BookDTO
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(savedBook.getId());
        bookDTO.setTitle(savedBook.getTitle());
        bookDTO.setAuthor(savedBook.getAuthor());
        bookDTO.setPrice(savedBook.getPrice());
        bookDTO.setDescription(savedBook.getDescription());
        bookDTO.setPublisher(savedBook.getPublisher());
        bookDTO.setPublicationDate(savedBook.getPublicationDate());
        bookDTO.setStatus(savedBook.getStatus());
        // Set Warehouse DTO
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(warehouse.getId());
        warehouseDTO.setName(warehouse.getName());
        warehouseDTO.setLocation(warehouse.getLocation());
        bookDTO.setWarehouse(warehouseDTO);

        // Set Block DTO
        BlockDTO blockDTO = new BlockDTO();
        blockDTO.setId(block.getId());
        blockDTO.setName(block.getName());
        blockDTO.setWarehouseId(warehouse.getId()); // Instead of full warehouse details, only pass the ID
        bookDTO.setBlock(blockDTO);

        // Set Rack DTO
        RackDTO rackDTO = new RackDTO();
        rackDTO.setId(rack.getId());
        rackDTO.setRackNumber(rack.getRackNumber());
        rackDTO.setBlockId(block.getId()); // Instead of full block details, only pass the ID
        bookDTO.setRack(rackDTO);

        return bookDTO; // Return the BookDTO instead of the Book entity
    }

    @Override
    public List<Book> addBooks(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        // Constructing the DTO with necessary information
        BookDTO bookDto = new BookDTO();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setPublicationDate(book.getPublicationDate());
        bookDto.setStatus(book.getStatus());

        // Add warehouse details with just the required information
        WarehouseDTO warehouseDto = new WarehouseDTO();
        warehouseDto.setId(book.getRack().getBlock().getWarehouse().getId());
        warehouseDto.setName(book.getRack().getBlock().getWarehouse().getName());
        warehouseDto.setLocation(book.getRack().getBlock().getWarehouse().getLocation());
        bookDto.setWarehouse(warehouseDto);

        // Add block details
        BlockDTO blockDto = new BlockDTO();
        blockDto.setId(book.getRack().getBlock().getId());
        blockDto.setName(book.getRack().getBlock().getName());
        blockDto.setWarehouseId(book.getRack().getBlock().getWarehouse().getId());
        bookDto.setBlock(blockDto);

        // Add rack details
        RackDTO rackDto = new RackDTO();
        rackDto.setId(book.getRack().getId());
        rackDto.setRackNumber(book.getRack().getRackNumber());
        rackDto.setBlockId(book.getRack().getBlock().getId());
        bookDto.setRack(rackDto);

        return bookDto;
    }

//    @Override
//    public BookDTO updateBook(Long id, BookDTO book) {
//        BookDTO existingBook = getBookById(id);
//        existingBook.setTitle(book.getTitle());
//        existingBook.setAuthor(book.getAuthor());
//        existingBook.setPrice(book.getPrice());
//        return bookRepository.save(existingBook);
//    }

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

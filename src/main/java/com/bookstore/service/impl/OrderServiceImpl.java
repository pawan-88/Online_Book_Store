package com.bookstore.service.impl;

import com.bookstore.dto.*;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.exception.OrderNotFoundException;
import com.bookstore.model.*;
import com.bookstore.repository.*;
import com.bookstore.service.OrderService;
import com.bookstore.util.Validation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OrderBooksRepository orderBooksRepository;
    private final WarehouseRepository warehouseRepository;
    private final BlockRepository blockRepository;
    private final RackRepository rackRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BookRepository bookRepository, OrderBooksRepository orderBooksRepository, WarehouseRepository warehouseRepository, BlockRepository blockRepository, RackRepository rackRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.orderBooksRepository = orderBooksRepository;
        this.warehouseRepository = warehouseRepository;
        this.blockRepository = blockRepository;
        this.rackRepository = rackRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }


    @Override
    @Transactional
    public OrderDTO placeOrder(Order order) {
        // Validate the order
        Validation.validateOrder(order);

        // Calculate total price based on the books in the order
        double totalPrice = order.getBooks().stream()
                .map(book -> bookRepository.findById(book.getId())
                        .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId())))
                .mapToDouble(Book::getPrice)
                .sum();
        order.setTotalPrice(totalPrice);

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Create the OrderDTO to store books and other details
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(savedOrder.getId());
        orderDTO.setTotalPrice(savedOrder.getTotalPrice());

        List<OrderBookDTO> orderBookDTOList = new ArrayList<>();

        // Save each book in the order_books table with book_name
        order.getBooks().forEach(book-> {
            Book fetchedBook = bookRepository.findById(book.getId())
                    .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId()));
            // Handle Warehouse
            Warehouse warehouse = null;
            if (fetchedBook.getWarehouse() != null) { // Check if Warehouse is provided in the Book
                if (fetchedBook.getWarehouse().getName() != null) {
                    // Try to find the Warehouse by name
                    warehouse = warehouseRepository.findByName(fetchedBook.getWarehouse().getName());
                    if (warehouse == null) {
                        // If no Warehouse exists, create a new one
                        warehouse = new Warehouse();
                        warehouse.setName(fetchedBook.getWarehouse().getName());
                        warehouse.setLocation(fetchedBook.getWarehouse().getLocation());
                        warehouse = warehouseRepository.save(warehouse); // Save the new Warehouse
                    } else if (warehouse.getLocation() == null && fetchedBook.getWarehouse().getLocation() != null) {
                        // Update existing Warehouse if location is missing
                        warehouse.setLocation(fetchedBook.getWarehouse().getLocation());
                        warehouse = warehouseRepository.save(warehouse); // Save the updated Warehouse
                    }
                    // Associate the Warehouse with the Book
                    book.setWarehouse(warehouse);
                }
            }

            // Handle Block
            Block block = blockRepository.findByNameAndWarehouseId(fetchedBook.getBlock().getName(),warehouse.getId());
            if (fetchedBook.getBlock() != null && warehouse != null) {
                block = blockRepository.findByNameAndWarehouseId(fetchedBook.getBlock().getName(), warehouse.getId());
                if (block == null) {
                    block = new Block();
                    block.setName(book.getBlock().getName());
                    block.setWarehouse(warehouse); // Associate block with warehouse
                    block = blockRepository.save(block); // Save block first
                }
                book.setBlock(block); // Associate the block with the book
            }
            // Handle Rack
            Rack rack = null;
        // Check if the fetchedBook has a Rack and if the Block is valid
            if (fetchedBook.getRack() != null && block != null) {
                // Try to find the rack by its rack number and block ID
                rack = rackRepository.findByRackNumberAndBlockId(fetchedBook.getRack().getRackNumber(), block.getId());
                // If the rack is not found, create a new one
                if (rack == null) {
                    rack = new Rack();
                    rack.setRackNumber(fetchedBook.getRack().getRackNumber()); // Use the fetchedBook's rack
                    rack.setBlock(block); // Associate rack with block
                    // Check if the book is null before associating
                    if (fetchedBook == null) {
                        throw new IllegalArgumentException("Book is null when trying to associate with the Rack.");
                    }
                    rack.setBook(fetchedBook); // Associate the rack with the book
                    rack.setAvailable(false); // Mark the rack as unavailable
                    rack = rackRepository.save(rack); // Save the new Rack
                } else {
                    // If the rack exists, update it to associate with the new book
                    if (fetchedBook == null) {
                        throw new IllegalArgumentException("Book is null when trying to update the existing Rack.");
                    }
                    rack.setBook(fetchedBook); // Set the book to the existing rack
                    rack.setAvailable(false); // Mark rack as unavailable
                    rack = rackRepository.save(rack); // Save the updated Rack
                }
                // Finally, associate the rack with the book
                fetchedBook.setRack(rack);
            } else {
                // Handle cases where Rack or Block is missing
                if (fetchedBook.getRack() == null) {
                    System.out.println("Rack information is missing for Book ID: " + fetchedBook.getId());
                }
                if (block == null) {
                    System.out.println("Block information is missing for the Rack.");
                }
            }
            // Create and set the OrderBookDTO
            OrderBookDTO orderBookDTO = new OrderBookDTO();
            orderBookDTO.setId(fetchedBook.getId());
            orderBookDTO.setTitle(fetchedBook.getTitle());
            orderBookDTO.setAuthor(fetchedBook.getAuthor());
            orderBookDTO.setPrice(fetchedBook.getPrice());
            orderBookDTO.setPublisher(fetchedBook.getPublisher());
            orderBookDTO.setPublicationDate(fetchedBook.getPublicationDate());
            orderBookDTO.setDescription(fetchedBook.getDescription());
            orderBookDTO.setStatus(fetchedBook.getStatus());

            // Set Warehouse DTO
            if (warehouse != null) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setId(warehouse.getId());
                warehouseDTO.setName(warehouse.getName());
                warehouseDTO.setLocation(warehouse.getLocation());
                orderBookDTO.setWarehouse(warehouseDTO);
            }

            // Set Block DTO
            if (block != null) {
                BlockDTO blockDTO = new BlockDTO();
                blockDTO.setId(block.getId());
                blockDTO.setName(block.getName());
                blockDTO.setWarehouseId(block.getWarehouse().getId()); // Pass warehouse ID
                orderBookDTO.setBlock(blockDTO);
            }

            // Set Rack DTO
            if (rack != null) {
                RackDTO rackDTO = new RackDTO();
                rackDTO.setId(rack.getId());
                rackDTO.setRackNumber(rack.getRackNumber());
                rackDTO.setBlockId(rack.getBlock().getId()); // Pass block ID
                orderBookDTO.setRack(rackDTO);
            }
            // Add to the DTO list for response
            orderBookDTOList.add(orderBookDTO);

            // Create and save the OrderBook entity
            OrderBook orderBooks = new OrderBook();
            orderBooks.setOrder(savedOrder);
            orderBooks.setBook(fetchedBook);
            orderBooks.setBookName(fetchedBook.getTitle());
            orderBooksRepository.save(orderBooks);
        });

        // Convert OrderBookDTO to BookDTO for the OrderDTO
        List<BookDTO> bookDTOList = orderBookDTOList.stream()
                .map(this::convertToBookDTO) // Convert each OrderBookDTO to BookDTO
                .collect(Collectors.toList());

        // Set the books in the OrderDTO
        orderDTO.setBooks(bookDTOList);

        return orderDTO;
    }

    private BookDTO convertToBookDTO(OrderBookDTO orderBookDTO) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId((orderBookDTO.getId()));
        bookDTO.setTitle(orderBookDTO.getTitle());
        bookDTO.setAuthor(orderBookDTO.getAuthor());
        bookDTO.setPrice(orderBookDTO.getPrice());
        bookDTO.setPublisher(orderBookDTO.getPublisher());
        bookDTO.setPublicationDate(orderBookDTO.getPublicationDate());
        bookDTO.setDescription(orderBookDTO.getDescription());
        bookDTO.setStatus(orderBookDTO.getStatus());
        bookDTO.setWarehouse(orderBookDTO.getWarehouse());
        bookDTO.setBlock(orderBookDTO.getBlock());
        bookDTO.setRack(orderBookDTO.getRack());
        return bookDTO;
    }

//    @Override
//    public Order placeOrder(Order order) {
//        // Validate the order
//        Validation.validateOrder(order);
//
//        // Calculate total price based on the books in the order
//        double totalPrice = order.getBooks().stream()
//                .map(book -> bookRepository.findById(book.getId())
//                        .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId())))
//                .mapToDouble(Book::getPrice)
//                .sum();
//
//        order.setTotalPrice(totalPrice);
//
//        // Create and save OrderBooks
//        List<OrderBook> orderBooks = new ArrayList<>();
//        for (Book book : order.getBooks()) {
//            Book fetchedBook = bookRepository.findById(book.getId())
//                    .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId()));
//
//            OrderBook orderBook = new OrderBook();
//            orderBook.setOrder(order);
//            orderBook.setBook(fetchedBook);
//            orderBook.setBookName(fetchedBook.getTitle()); // Set book name
//
//            orderBooks.add(orderBook);
//        }
//
//        order.setOrderBooks(orderBooks);
//
//        // Save the order and associated orderBooks
//        return orderRepository.save(order);
//    }

    @Override
    public boolean cancelOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
        return false;
    }

    @Override
    public List<Order> searchOrders(Long id, String bookName) {
        if (id != null) {
            return orderRepository.findById(id)
                    .map(List::of)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        } else if (bookName != null) {
            return orderRepository.findByBookTitleContainingIgnoreCase(bookName);
        } else {
            throw new InvalidInputException("At least one search parameter must be provided.");
        }
    }
}

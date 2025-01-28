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

        // Process each book in the order
        order.getBooks().forEach(book -> {
            // Fetch the persistent Book object from the database
            Book fetchedBook = bookRepository.findById(book.getId())
                    .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId()));

            // Handle Warehouse
            Warehouse warehouse = null;
            if (fetchedBook.getWarehouse() != null && fetchedBook.getWarehouse().getName() != null) {
                warehouse = warehouseRepository.findByName(fetchedBook.getWarehouse().getName());
                if (warehouse == null) {
                    warehouse = new Warehouse();
                    warehouse.setName(fetchedBook.getWarehouse().getName());
                    warehouse.setLocation(fetchedBook.getWarehouse().getLocation());
                    warehouse = warehouseRepository.save(warehouse);
                }
            }
            // Handle Block
            Block block = null;
            if (fetchedBook.getBlock() != null && warehouse != null) {
                block = blockRepository.findByNameAndWarehouseId(fetchedBook.getBlock().getName(), warehouse.getId());
                if (block == null) {
                    block = new Block();
                    block.setName(fetchedBook.getBlock().getName());
                    block.setWarehouse(warehouse);
                    block = blockRepository.save(block);
                }
            }
            // Handle Rack
            Rack rack = null;
            if (fetchedBook.getRack() != null && block != null) {
                rack = rackRepository.findByRackNumberAndBlockId(fetchedBook.getRack().getRackNumber(), block.getId());
                if (rack == null) {
                    rack = new Rack();
                    rack.setRackNumber(fetchedBook.getRack().getRackNumber());
                    rack.setBlock(block);
                    rack.setBook(fetchedBook); // Associate persistent book
                    rack.setAvailable(false);
                    rack = rackRepository.save(rack);
                } else {
                    rack.setBook(fetchedBook); // Set the fetched persistent book
                    rack.setAvailable(false);
                    rack = rackRepository.save(rack);
                }
                fetchedBook.setRack(rack); // Associate the rack with the fetched book
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

            if (warehouse != null) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setId(warehouse.getId());
                warehouseDTO.setName(warehouse.getName());
                warehouseDTO.setLocation(warehouse.getLocation());
                orderBookDTO.setWarehouse(warehouseDTO);
            }

            if (block != null) {
                BlockDTO blockDTO = new BlockDTO();
                blockDTO.setId(block.getId());
                blockDTO.setName(block.getName());
                blockDTO.setWarehouseId(block.getWarehouse().getId());
                orderBookDTO.setBlock(blockDTO);
            }

            if (rack != null) {
                RackDTO rackDTO = new RackDTO();
                rackDTO.setId(rack.getId());
                rackDTO.setRackNumber(rack.getRackNumber());
                rackDTO.setBlockId(rack.getBlock().getId());
                orderBookDTO.setRack(rackDTO);
            }

            orderBookDTOList.add(orderBookDTO);

            // Create and save the OrderBook entity
            OrderBook orderBooks = new OrderBook();
            orderBooks.setOrder(savedOrder);
            orderBooks.setBook(fetchedBook);
            orderBooks.setBookName(fetchedBook.getTitle());
            orderBooksRepository.save(orderBooks);

            // **Delete the book from the book table**
//            bookRepository.delete(fetchedBook);
        });

        // Convert OrderBookDTO to BookDTO for the OrderDTO
        List<BookDTO> bookDTOList = orderBookDTOList.stream()
                .map(this::convertToBookDTO)
                .collect(Collectors.toList());

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

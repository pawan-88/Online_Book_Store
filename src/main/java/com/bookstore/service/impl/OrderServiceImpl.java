package com.bookstore.service.impl;

import com.bookstore.exceptions.BookNotFoundException;
import com.bookstore.exceptions.OrderNotFoundException;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderBook;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderBooksRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OrderBooksRepository orderBooksRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BookRepository bookRepository, OrderBooksRepository orderBooksRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.orderBooksRepository = orderBooksRepository;
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
    public Order placeOrder(Order order) {
        // Calculate total price based on the books in the order
        double totalPrice = order.getBooks().stream()
                .map(book -> bookRepository.findById(book.getId())
                        .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId())))
                .mapToDouble(Book::getPrice)
                .sum();

        order.setTotalPrice(totalPrice);

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Save each book in the order_books table with book_name
        order.getBooks().forEach(book -> {
            Book fetchedBook = bookRepository.findById(book.getId())
                    .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + book.getId()));

            OrderBook orderBooks = new OrderBook();
            orderBooks.setOrder(savedOrder);
            orderBooks.setBook(fetchedBook);
            orderBooks.setBookName(fetchedBook.getTitle()); // Set the book name

            // Save to order_books table
            orderBooksRepository.save(orderBooks);
        });

        return savedOrder;
    }

    @Override
    public void cancelOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}

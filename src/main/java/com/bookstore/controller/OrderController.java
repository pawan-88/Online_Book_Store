package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.util.Validation;
import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> order = orderService.getAllOrders();
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Validation.validateId(id);
        Order order = orderService.getOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        Validation.validateOrder(order);   // Validate input types
        Order placedOrder = orderService.placeOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Order with ID " + placedOrder.getId() + " placed successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        Validation.validateId(id);
        boolean canceled = orderService.cancelOrder(id);
        if (canceled) {
            return ResponseEntity.status(HttpStatus.OK).body("Order with ID " + id + " has been canceled.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with ID " + id + " not found.");
        }
    }

    // Search API
    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String bookName) {

        Validation.validateSearchParameters(id, bookName, null, "Order");
        List<Order> orders = orderService.searchOrders(id, bookName);
        return ResponseEntity.ok(orders);
    }
}


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
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Validation.validateId(id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        Validation.validateOrder(order);   // Validate input types
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        Validation.validateId(id);
        orderService.cancelOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
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


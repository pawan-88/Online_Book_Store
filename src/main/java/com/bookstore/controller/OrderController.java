package com.bookstore.controller;

import com.bookstore.dto.OrderDTO;
import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import com.bookstore.service.impl.OrderServiceImpl;
import com.bookstore.util.BaseResponse;
import com.bookstore.util.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService, OrderServiceImpl orderServiceImpl) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<OrderDTO>> placeOrder(@RequestBody Order order) {
        String requestId = null;
        try {
            Validation.validateOrder(order);
            OrderDTO placedOrder = orderService.placeOrder(order);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new BaseResponse<>(
                            requestId,
                            placedOrder,
                            new BaseResponse.ResponseMessage("201", "Order placed successfully.", null)
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

    @GetMapping
    public ResponseEntity<BaseResponse<List<Order>>> getAllOrders() {
        String requestId = null;
        List<Order> orders = orderService.getAllOrders();

        return ResponseEntity.ok(
                new BaseResponse<>(
                        requestId,
                        orders,
                        new BaseResponse.ResponseMessage("200", "Orders retrieved successfully.", null)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Order>> getOrderById(@PathVariable Long id) {
        String requestId = null;
        try {
            Validation.validateId(id);
            Order order = orderService.getOrderById(id);

            return ResponseEntity.ok(
                    new BaseResponse<>(
                            requestId,
                            order,
                            new BaseResponse.ResponseMessage("200", "Order retrieved successfully.", null)
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

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> cancelOrder(@PathVariable Long id) {
        String requestId = null;
        try {
            Validation.validateId(id);
            boolean canceled = orderService.cancelOrder(id);

            if (canceled) {
                return ResponseEntity.ok(
                        new BaseResponse<>(
                                requestId,
                                "Order with ID " + id + " canceled successfully.",
                                new BaseResponse.ResponseMessage("200", "Order canceled successfully.", null)
                        )
                );
            } else {
                throw new RuntimeException("Order with ID " + id + " not found.");
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
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<Order>>> searchOrders(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String bookName) {

        String requestId = UUID.randomUUID().toString();
        try {
            Validation.validateSearchParameters(id, bookName, null, "Order");
            List<Order> orders = orderService.searchOrders(id, bookName);

            return ResponseEntity.ok(
                    new BaseResponse<>(
                            requestId,
                            orders,
                            new BaseResponse.ResponseMessage("200", "Orders retrieved successfully.", null)
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
}

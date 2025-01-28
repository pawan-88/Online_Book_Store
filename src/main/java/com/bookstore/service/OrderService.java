package com.bookstore.service;

import com.bookstore.dto.OrderDTO;
import com.bookstore.model.Order;
import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();
    Order getOrderById(Long id);
    OrderDTO placeOrder(Order order);
    boolean cancelOrder(Long id);
    List<Order> searchOrders(Long id, String bookName);
}

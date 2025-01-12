package com.bookstore.repository;

import com.bookstore.model.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderBooksRepository extends JpaRepository<OrderBook,Long> {
}

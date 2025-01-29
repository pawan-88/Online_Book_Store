package com.bookstore.repository;

import com.bookstore.model.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderBooksRepository extends JpaRepository<OrderBook,Long> {

    Optional<OrderBook> findByOrderIdAndBookId(Long orderId, Long bookId);
}

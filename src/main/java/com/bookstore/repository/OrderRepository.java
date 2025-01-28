package com.bookstore.repository;

import com.bookstore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT o FROM Order o JOIN o.books b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Order> findByBookTitleContainingIgnoreCase(@Param("title") String title);
}

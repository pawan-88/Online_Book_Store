package com.bookstore.repository;

import com.bookstore.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query(value = "SELECT * FROM cart WHERE user_id = :userId", nativeQuery = true)
    Optional<Cart> findCartByUserId(@Param("userId") Long userId); // Find cart for a specific user


}

package com.bookstore.repository;

import com.bookstore.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM user_table WHERE username = :username", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM user_table WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    // Native Query to insert a new user and return the generated ID
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_table (username, password, email) VALUES (:username, :password, :email)" , nativeQuery = true)
    void registerUser(@Param("username") String username,
                      @Param("password") String password,
                      @Param("email") String email);

}

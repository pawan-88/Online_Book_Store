package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM book WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    @Query(value = "SELECT * FROM book WHERE LOWER(author) LIKE LOWER(CONCAT('%', :author, '%'))", nativeQuery = true)
    List<Book> findByAuthorContainingIgnoreCase(@Param("author") String author);

    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.status = 'ACTIVE'")
    Optional<Book> findActiveBookById(@Param("id") Long id);

    Optional<Book> findByIdAndStatus(Long id, String status);

//    List<Book> findAllActiveBooks();
}

package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.model.Book;

import java.util.List;

public interface BookService {

    BookDTO addBook(Book book);

    List<Book> getAllBooks();

    BookDTO getBookById(Long id);

//   Book allocateBookToRack(Long id, Long rackId);

//    BookDTO updateBook(Long id, BookDTO book);

    Boolean deleteBook(Long id);

    List<Book> searchBooks(Long id, String title, String author);

    List<Book> addBooks(List<Book> books);

    String generateShareableLink(Long bookId,  String uniqueId);

    Book getBookByShareableLink(String uniqueId);
}

package com.bookstore.util;

import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Book;
import com.bookstore.model.Order;


public class Validation {

    public static void validateBook(Book book) {
        if (book == null) {
            throw new InvalidInputException("Book cannot be null.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Book title cannot be empty.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidInputException("Author name cannot be empty.");
        }
        if (book.getPrice() <= 0) {
            throw new InvalidInputException("Price must be a positive number.");
        }
    }


    public static void validateOrder(Order order) {
        if (order == null) {
            throw new InvalidInputException("Order cannot be null.");
        }
        if (order.getBooks() == null || order.getBooks().isEmpty()) {
            throw new InvalidInputException("Order must contain at least one book.");
        }
        if (order.getTotalPrice() < 0) {
            throw new InvalidInputException("Order total price cannot be negative.");
        }
    }

    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidInputException(id + " ID must be a positive number.");
        }
    }

    public static void validateSearchParameters(Long id, String name, String author, String entity) {
        if (id == null && (name == null || name.trim().isEmpty()) && (author == null || author.trim().isEmpty())) {
            throw new InvalidInputException("At least one search parameter must be provided for " + entity + ".");
        }
    }
}

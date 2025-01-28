package com.bookstore.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private Double totalPrice;
    private List<BookDTO> books;

    // Constructor
    public OrderDTO(Long id, List<BookDTO> books, Double totalPrice) {
        this.id = id;
        this.books = books;
        this.totalPrice = totalPrice;
    }

    public OrderDTO() {

    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

package com.bookstore.dto;

import com.bookstore.model.Book;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private double price;
    private String description;
    private String publisher;
    private LocalDate publicationDate;
    private WarehouseDTO warehouse;
    private BlockDTO block;
    private RackDTO rack;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public WarehouseDTO getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseDTO warehouse) {
        this.warehouse = warehouse;
    }

    public BlockDTO getBlock() {
        return block;
    }

    public void setBlock(BlockDTO block) {
        this.block = block;
    }

    public RackDTO getRack() {
        return rack;
    }

    public void setRack(RackDTO rack) {
        this.rack = rack;
    }
}

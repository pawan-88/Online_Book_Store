package com.bookstore.dto;


public class BookRequest {
    private String title;
    private String author;
    private Double price;
    private String description;
    private String publisher;
    private String publicationDate;
    private WarehouseRequest warehouse;
    private BlockRequest block;
    private RackRequest rack;
    private String status;

    // Getters and setters


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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public WarehouseRequest getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseRequest warehouse) {
        this.warehouse = warehouse;
    }

    public BlockRequest getBlock() {
        return block;
    }

    public void setBlock(BlockRequest block) {
        this.block = block;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RackRequest getRack() {
        return rack;
    }

    public void setRack(RackRequest rack) {
        this.rack = rack;
    }

    public static class WarehouseRequest {
        private String name;
        private String location;
        // Getters and setters
    }

    public static class BlockRequest {
        private String name;
        // Getters and setters
    }

    public static class RackRequest {
        private String rackNumber;
        // Getters and setters
    }
}

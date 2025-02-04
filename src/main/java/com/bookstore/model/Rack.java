package com.bookstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rack")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rackNumber;

    @ManyToOne
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @OneToOne
    @JoinColumn(name = "book_id", nullable = true) // Allow null for available racks
    private Book book;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "current_load", nullable = false)
    private Integer currentLoad = 0;

    public Rack(String rackNumber, Block block, Book book, Boolean isAvailable) {
        this.rackNumber = rackNumber;
        this.block = block;
        this.book = book;
        this.isAvailable = isAvailable;
    }

    public Integer getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(Integer currentLoad) {
        this.currentLoad = currentLoad;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Rack() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(String rackNumber) {
        this.rackNumber = rackNumber;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}

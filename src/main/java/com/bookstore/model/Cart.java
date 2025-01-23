package com.bookstore.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_table")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)  // Assuming a Cart belongs to one User
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Reference to the User entity

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Method to add CartItem directly to Cart
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this); // Set cart reference
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null); // Remove cart reference
    }
}

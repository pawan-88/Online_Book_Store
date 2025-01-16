package com.bookstore.service;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.model.CartItem;

import java.util.List;

public interface CartService {

    void addBookToCart(CartItemRequestDTO cartItemRequestDTO);

    void removeBookFromCart(Long bookId);

    List<CartItem> viewCart();
}

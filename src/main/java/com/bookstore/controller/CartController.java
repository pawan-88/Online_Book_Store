package com.bookstore.controller;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.exception.CartException;
import com.bookstore.model.CartItem;
import com.bookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequestDTO cartItemRequestDTO) {
        try {
            cartService.addBookToCart(cartItemRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Book added to cart");
        } catch (CartException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long bookId) {
        try {
            cartService.removeBookFromCart(bookId);
            return ResponseEntity.status(HttpStatus.OK).body("Book removed from cart");
        } catch (CartException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> viewCart() {
        return ResponseEntity.ok(cartService.viewCart());
    }
}

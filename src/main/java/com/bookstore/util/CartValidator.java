package com.bookstore.util;

import com.bookstore.exception.CartException;
import com.bookstore.model.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public void validateCartItem(CartItem cartItem) {
        if (cartItem == null) {
            throw new CartException("CartItem cannot be null.");
        }
        if (cartItem.getBook() == null) {
            throw new CartException("Book information is required.");
        }
        if (cartItem.getQuantity() <= 0) {
            throw new CartException("Quantity must be greater than zero.");
        }
    }
    }

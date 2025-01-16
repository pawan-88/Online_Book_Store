package com.bookstore.service.impl;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.exception.CartException;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.CartService;
import com.bookstore.util.CartValidator;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final BookRepository bookRepository;
    private final CartValidator cartValidator;
    private final Cart cart = new Cart();

    public CartServiceImpl(BookRepository bookRepository, CartValidator cartValidator) {
        this.bookRepository = bookRepository;
        this.cartValidator = cartValidator;
        this.cart.setItems(new ArrayList<>()); //// Initialize cart items
    }

    @Override
    public void addBookToCart(CartItemRequestDTO cartItemRequestDTO) {
        logger.info("Adding to cart: {}", cartItemRequestDTO);
        // Validate cart item request (if needed)
        if (cartItemRequestDTO.getQuantity() <= 0) {
            throw new CartException("Quantity must be greater than zero");
        }
        // Check if the book exists in the database
        Book book = bookRepository.findById(cartItemRequestDTO.getBookId())
                .orElseThrow(() -> new CartException("Book not found"));
        // Create CartItem using book and quantity
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(cartItemRequestDTO.getQuantity());
        // Validate CartItem (optional)
        cartValidator.validateCartItem(cartItem);
        // Add to cart
        cart.getItems().add(cartItem);
    }

    @Override
    public void removeBookFromCart(Long bookId) {
        // Find and remove the book from the cart
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new CartException("Book not found in cart"));

        cart.getItems().remove(itemToRemove);
    }

    @Override
    public List<CartItem> viewCart() {
        return cart.getItems();
    }
}

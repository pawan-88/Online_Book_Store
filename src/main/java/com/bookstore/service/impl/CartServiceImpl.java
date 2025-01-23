package com.bookstore.service.impl;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.exception.CartException;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
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
    private final CartRepository cartRepository;
    private final Cart cart = new Cart();

    public CartServiceImpl(BookRepository bookRepository, CartValidator cartValidator, CartRepository cartRepository) {
        this.bookRepository = bookRepository;
        this.cartValidator = cartValidator;
        this.cartRepository = cartRepository;
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
        // Find or create a cart (for simplicity, assuming a single cart for the user)
        Cart cart = cartRepository.findCartByUserId(cartItemRequestDTO.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        // Create and validate the CartItem
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(cartItemRequestDTO.getQuantity());
        cartItem.setCart(cart);

        cartValidator.validateCartItem(cartItem);

        // Add the CartItem to the Cart
        cart.getItems().add(cartItem);
        cartRepository.save(cart); // Persist changes to the database
        logger.info("Cart updated with new item: {}", cartItem);
    }

//    @Override
//    public void removeBookFromCart(Long bookId) {
//        // Find the cart (assuming single cart for simplicity)
//        Cart cart = cartRepository.findActiveCart()
//                .orElseThrow(() -> new CartException("No active cart found"));
//
//        // Find and remove the CartItem by bookId
//        CartItem itemToRemove = cart.getItems().stream()
//                .filter(item -> item.getBook().getId().equals(bookId))
//                .findFirst()
//                .orElseThrow(() -> new CartException("Book not found in cart"));
//
//        cart.getItems().remove(itemToRemove);
//        cartRepository.save(cart); // Persist changes to the database
//    }
//
//    @Override
//    public List<CartItem> viewCart() {
//        Cart cart = cartRepository.findActiveCart()
//                .orElseThrow(() -> new CartException("No active cart found"));
//        return cart.getItems();
//    }
}

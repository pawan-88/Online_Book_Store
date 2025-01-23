package com.bookstore.controller;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.exception.CartException;
import com.bookstore.service.CartService;
import com.bookstore.util.BaseResponse;
import com.bookstore.util.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<String>> addToCart(@RequestBody CartItemRequestDTO cartItemRequestDTO) {
        String requestId = null;
        try {
            Validation.validateCartItem(cartItemRequestDTO);
            cartService.addBookToCart(cartItemRequestDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new BaseResponse<>(
                            requestId,
                            "Book added to cart successfully.",
                            new BaseResponse.ResponseMessage("201", "Book added successfully.", null)
                    )
            );
        } catch (CartException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BaseResponse<>(
                            requestId,
                            null,
                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
                    )
            );
        }
    }

//    @DeleteMapping("/{bookId}")
//    public ResponseEntity<BaseResponse<String>> removeFromCart(@PathVariable Long bookId) {
//        String requestId = null;
//        try {
//            Validation.validateId(bookId);
//            cartService.removeBookFromCart(bookId);
//
//            return ResponseEntity.ok(
//                    new BaseResponse<>(
//                            requestId,
//                            "Book removed from cart successfully.",
//                            new BaseResponse.ResponseMessage("200", "Book removed successfully.", null)
//                    )
//            );
//        } catch (CartException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    new BaseResponse<>(
//                            requestId,
//                            null,
//                            new BaseResponse.ResponseMessage("400", e.getMessage(), null)
//                    )
//            );
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<BaseResponse<List<CartItem>>> viewCart() {
//        String requestId = null;
//        List<CartItem> cartItems = cartService.viewCart();
//
//        return ResponseEntity.ok(
//                new BaseResponse<>(
//                        requestId,
//                        cartItems,
//                        new BaseResponse.ResponseMessage("200", "Cart items retrieved successfully.", null)
//                )
//        );
//    }
}

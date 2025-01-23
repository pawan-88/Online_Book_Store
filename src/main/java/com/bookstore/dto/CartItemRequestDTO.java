package com.bookstore.dto;

import jakarta.validation.constraints.Min;
import org.antlr.v4.runtime.misc.NotNull;

public class CartItemRequestDTO {

    @NotNull
    private Long bookId;

    @Min(1)
    private Integer  quantity;

    private Long userId; // Add userId for multi-user scenarios

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

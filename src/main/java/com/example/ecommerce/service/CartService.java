package com.example.ecommerce.service;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.User;

import java.util.List;

public interface CartService {
    CartItem addToCart(User user, Long productId, Integer quantity);
    List<CartItem> getCartItems(User user);
    void removeFromCart(User user, Long productId);
    CartItem updateCartItemQuantity(User user, Long productId, Integer quantity);
    void clearCart(User user);
} 
package com.example.ecommerce.service.impl;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Optional<CartItem> existingItem = cartRepository.findByUserAndProduct_Id(user, productId);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            if (product.getQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock");
            }
            
            item.setQuantity(newQuantity);
            return cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPriceAtAdd(product.getPrice());
            return cartRepository.save(newItem);
        }
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void removeFromCart(User user, Long productId) {
        cartRepository.deleteByUserAndProduct_Id(user, productId);
    }

    @Override
    @Transactional
    public CartItem updateCartItemQuantity(User user, Long productId, Integer quantity) {
        CartItem item = cartRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product product = item.getProduct();
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        List<CartItem> userItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(userItems);
    }
} 
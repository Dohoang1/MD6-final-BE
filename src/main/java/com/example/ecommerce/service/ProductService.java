package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Optional<Product> findById(Long id);
    Optional<Product> findByIdWithSeller(Long id);
    Product save(Product product);
    void deleteById(Long id);
    
    // Các method tìm kiếm
    Page<Product> findByNameContaining(String name, Pageable pageable);
    List<Product> findByNameContaining(String name);
    List<Product> findByCategory(String category);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    Page<Product> search(String keyword, Pageable pageable);
}
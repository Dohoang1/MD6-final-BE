package com.example.ecommerce.service.impl;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.enums.ProductStatus;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        log.info("Service - Finding all products");
        log.info("Pageable: {}", pageable);
        Page<Product> result = productRepository.findAll(pageable);
        log.info("Found {} products", result.getTotalElements());
        return result;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> findByNameContaining(String name, Pageable pageable) {
        log.info("Searching for products with name containing: '{}'", name);
        return productRepository.findByNameContaining(name.toLowerCase(), pageable);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name.toLowerCase());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> findByPriceBetween(double minPrice, double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public Page<Product> search(String keyword, Pageable pageable) {
        log.info("Searching with keyword: '{}' in both name and category", keyword);
        return productRepository.search(keyword.toLowerCase(), pageable);
    }

    @Override
    public Optional<Product> findByIdWithSeller(Long id) {
        return productRepository.findByIdWithSeller(id);
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @Override
    public Page<Product> findAllApproved(Pageable pageable) {
        log.info("Finding all approved products");
        return productRepository.findAllApproved(pageable);
    }

    @Override
    public Page<Product> searchApproved(String term, Pageable pageable) {
        log.info("Searching approved products with term: '{}'", term);
        return productRepository.searchApproved(term.toLowerCase(), pageable);
    }

    @Override
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    @Override
    public Page<Product> findByCategoryWithPaging(String category, Pageable pageable) {
        return productRepository.findByCategoryWithPaging(category, pageable);
    }

    @Override
    public List<String> getAllSellers() {
        return productRepository.findAllSellers();
    }

    @Override
    public Page<Product> findBySellerWithPaging(String seller, Pageable pageable) {
        return productRepository.findBySellerWithPaging(seller, pageable);
    }

}
package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tìm kiếm theo tên (có phân trang)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:name%")
    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    // Tìm kiếm theo tên (không phân trang)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);
    
    // Tìm kiếm theo category
    @Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category)")
    List<Product> findByCategory(@Param("category") String category);
    
    // Tìm kiếm theo khoảng giá
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
    
    // Tìm kiếm theo tên hoặc category
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:keyword% OR LOWER(p.category) LIKE %:keyword%")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);
}
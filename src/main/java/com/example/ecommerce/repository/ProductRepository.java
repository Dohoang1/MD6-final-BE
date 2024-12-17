package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.id = :id")
    Optional<Product> findByIdWithSeller(@Param("id") Long id);
    
    List<Product> findByStatus(ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED'")
    Page<Product> findAllApproved(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND (LOWER(p.name) LIKE %:term% OR LOWER(p.category) LIKE %:term%)")
    Page<Product> searchApproved(@Param("term") String term, Pageable pageable);

    // Tìm kiếm theo category có phân trang
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))")
    Page<Product> findByCategoryWithPaging(@Param("category") String category, Pageable pageable);

    // Tìm kiếm theo seller có phân trang
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND (:seller IS NULL OR LOWER(p.seller.username) = LOWER(:seller))")
    Page<Product> findBySellerWithPaging(@Param("seller") String seller, Pageable pageable);

    // Lấy danh sách các category duy nhất
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();

    // Lấy danh sách các seller có sản phẩm
    @Query("SELECT DISTINCT p.seller.username FROM Product p WHERE p.status = 'APPROVED' ORDER BY p.seller.username")
    List<String> findAllSellers();
}
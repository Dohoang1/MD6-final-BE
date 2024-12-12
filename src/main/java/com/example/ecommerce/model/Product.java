package com.example.ecommerce.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "products", indexes = {
    @Index(name = "idx_product_price", columnList = "price")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Tên sản phẩm phải từ 2-100 ký tự")
    private String name;

    @Min(value = 0, message = "Số lượng không được âm")
    private int quantity;

    @NotBlank(message = "Mô tả không được để trống")
    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", message = "Giá không được âm")
    @Column(nullable = false)
    private double price;

    @NotBlank(message = "Danh mục không được để trống")
    private String category;

    @ElementCollection
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    public User getSeller() {
        return seller;
    }

    @JsonProperty("sellerUsername")
    public String getSellerUsername() {
        return seller != null ? seller.getUsername() : null;
    }
}
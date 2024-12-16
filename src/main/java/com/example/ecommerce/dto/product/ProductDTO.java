package com.example.ecommerce.dto.product;

import com.example.ecommerce.model.enums.ProductStatus;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private List<String> imageUrls;
    private ProductStatus status;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
}

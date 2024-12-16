package com.example.ecommerce.dto;

import com.example.ecommerce.dto.product.ProductDTO;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;
    private ProductDTO product;
    private int quantity;
    private double priceAtAdd;
}

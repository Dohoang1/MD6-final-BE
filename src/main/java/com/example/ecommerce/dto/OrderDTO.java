package com.example.ecommerce.dto;

import com.example.ecommerce.model.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String fullName;
    private String address;
    private String phone;
    private String email;
    private LocalDateTime deliveryTime;
    private String paymentMethod;
    private List<OrderItemDTO> items;
    private double subtotal;
    private double shippingFee;
    private double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

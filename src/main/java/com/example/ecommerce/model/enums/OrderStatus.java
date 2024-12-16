package com.example.ecommerce.model.enums;

public enum OrderStatus {
    PENDING,    // Chờ xác nhận
    SHIPPING,   // Đang giao hàng
    COMPLETED,  // Đã giao hàng thành công
    REJECTED,   // Đã từ chối
    CANCELLED   // Đã hủy
}

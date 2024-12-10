package com.example.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers")
public class Customer extends User {
    // Có thể thêm các trường riêng của Customer sau này
}

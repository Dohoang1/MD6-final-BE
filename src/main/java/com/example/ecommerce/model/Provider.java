package com.example.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "providers")
public class Provider extends User {
    private String businessName;
    private String businessLicense;
}

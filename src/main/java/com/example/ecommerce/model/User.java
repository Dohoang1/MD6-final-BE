package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import com.example.ecommerce.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @JsonProperty("username")
    private String username;

    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;

    private boolean enabled = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    public String getUsername() {
        return username;
    }
}

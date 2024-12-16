package com.example.ecommerce.service;

import com.example.ecommerce.model.User;

public interface UserService {
    User getCurrentUser();
    User getUserById(Long id);
    User getUserByUsername(String username);
}

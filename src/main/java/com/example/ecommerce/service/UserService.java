package com.example.ecommerce.service;

import com.example.ecommerce.dto.UserDTO;
import com.example.ecommerce.model.User;
import java.util.List;

public interface UserService {
    User getCurrentUser();
    User getUserById(Long id);
    User getUserByUsername(String username);
    
    // Admin management methods
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO toggleUserStatus(Long id);
    
    // Profile management methods
    UserDTO getUserProfile(String username);
    UserDTO updateProfile(String username, UserDTO userDTO);
}

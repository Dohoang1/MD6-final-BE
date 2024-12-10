package com.example.ecommerce.config;

import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.Role;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminConfig implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Kiểm tra nếu chưa có admin thì tạo mới
        if (!userRepository.existsByEmail("admin@unitrade.com")) {
            User admin = new User();
            admin.setEmail("admin@unitrade.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setUsername("Admin");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        }
    }
}

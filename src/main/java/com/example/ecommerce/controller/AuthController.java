package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.Role;
import com.example.ecommerce.payload.LoginRequest;
import com.example.ecommerce.payload.MessageResponse;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("User found: {}", user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed", e);
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Invalid email or password: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            logger.info("Received registration request for email: {}", user.getEmail());
            
            // Kiểm tra email đã tồn tại chưa
            if (userRepository.existsByEmail(user.getEmail())) {
                logger.warn("Email already exists: {}", user.getEmail());
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Email đã được sử dụng!"));
            }

            // Mã hóa mật khẩu
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Đảm bảo role được set đúng
            try {
                if (user.getRole() == null) {
                    user.setRole(Role.CUSTOMER);
                }
                
                // Validate role
                if (!user.getRole().equals(Role.CUSTOMER) && 
                    !user.getRole().equals(Role.PROVIDER)) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Role không hợp lệ!"));
                }
            } catch (Exception e) {
                logger.error("Invalid role: {}", user.getRole());
                return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Role không hợp lệ!"));
            }
            
            // Lưu user mới
            logger.info("Saving new user with email: {}", user.getEmail());
            User savedUser = userRepository.save(user);
            
            // Xóa mật khẩu trước khi trả về response
            savedUser.setPassword(null);
            
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Registration error", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Lỗi đăng ký: " + e.getMessage()));
        }
    }
}

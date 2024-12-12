package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.FileStorageService;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestParam("files") List<MultipartFile> files,
                                           @RequestParam("name") String name,
                                           @RequestParam("price") double price,
                                           @RequestParam("quantity") int quantity,
                                           @RequestParam("description") String description,
                                           @RequestParam("category") String category,
                                           Authentication authentication) {
        try {
            // Debug log
            System.out.println("Authentication: " + authentication);
            
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User seller = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Debug log
            System.out.println("Seller found: " + seller.getUsername());

            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDescription(description);
            product.setCategory(category);
            product.setSeller(seller); // Set seller explicitly

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = fileStorageService.storeFile(file);
                imageUrls.add(fileName);
            }
            product.setImageUrls(imageUrls);

            Product savedProduct = productService.save(product);

            // Debug log
            System.out.println("Saved product with seller: " + savedProduct.getSeller().getUsername());

            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating product: " + e.getMessage());
        }
    }

    // API lấy chi tiết sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> productOpt = productService.findByIdWithSeller(id);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                
                // Debug log
                System.out.println("Found product: " + product.getId());
                if (product.getSeller() != null) {
                    System.out.println("Seller: " + product.getSeller().getUsername());
                } else {
                    System.out.println("No seller found for product");
                }

                Map<String, Object> response = new HashMap<>();
                response.put("id", product.getId());
                response.put("name", product.getName());
                response.put("price", product.getPrice());
                response.put("quantity", product.getQuantity());
                response.put("description", product.getDescription());
                response.put("category", product.getCategory());
                response.put("imageUrls", product.getImageUrls());
                response.put("sellerUsername", product.getSeller() != null ? 
                            product.getSeller().getUsername() : "Không có thông tin người bán");

                // Debug log
                System.out.println("Response: " + response);

                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                           @RequestParam("name") String name,
                                           @RequestParam("price") double price,
                                           @RequestParam("quantity") int quantity,
                                           @RequestParam("description") String description,
                                           @RequestParam("category") String category,
                                           @RequestParam("existingImages") String existingImagesJson) {
        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Cập nhật thông tin cơ bản
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDescription(description);
            product.setCategory(category);

            // Chuyển đổi JSON string thành List
            ObjectMapper mapper = new ObjectMapper();
            List<String> existingImages = mapper.readValue(existingImagesJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));

            // Tạo danh sách ảnh mới bao gồm cả ảnh cũ
            List<String> updatedImageUrls = new ArrayList<>(existingImages);

            // Thêm các ảnh mới (nếu có)
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String fileName = fileStorageService.storeFile(file);
                    updatedImageUrls.add(fileName);
                }
            }

            // Cập nhật danh sách ảnh
            product.setImageUrls(updatedImageUrls);

            Product updatedProduct = productService.save(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Xóa các file ảnh liên quan
            for (String imageUrl : product.getImageUrls()) {
                fileStorageService.deleteFile(imageUrl);
            }

            productService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting product: " + e.getMessage());
        }
    }

    // Thêm endpoint mới cho phân trang
    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getProductsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String search) {
        
        try {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sorting = Sort.by(direction, sortField);
            Pageable pageable = PageRequest.of(page, size, sorting);
            
            Page<Product> productPage;
            if (search != null && !search.trim().isEmpty()) {
                log.info("Searching with term: {}", search.trim());
                productPage = productService.search(search.trim(), pageable);
            } else {
                productPage = productService.findAll(pageable);
            }
            
            return ResponseEntity.ok(productPage);
        } catch (Exception e) {
            log.error("Error processing request:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
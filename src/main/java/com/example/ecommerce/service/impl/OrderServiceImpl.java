package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        User currentUser = userService.getCurrentUser();
        
        Order order = modelMapper.map(orderDTO, Order.class);
        order.setUser(currentUser);
        
        // Tạo các OrderItem và cập nhật số lượng sản phẩm
        List<OrderItem> orderItems = orderDTO.getItems().stream()
            .map(itemDTO -> {
                Product product = productRepository.findById(itemDTO.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                
                // Kiểm tra và cập nhật số lượng sản phẩm
                if (product.getQuantity() < itemDTO.getQuantity()) {
                    throw new IllegalStateException("Not enough stock for product: " + product.getName());
                }
                product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
                productRepository.save(product);
                
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setPriceAtAdd(itemDTO.getPriceAtAdd());
                return orderItem;
            })
            .collect(Collectors.toList());
        
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(order -> modelMapper.map(order, OrderDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        // Nếu đơn hàng bị từ chối hoặc hủy, hoàn lại số lượng sản phẩm
        if ((status == OrderStatus.REJECTED || status == OrderStatus.CANCELLED) && 
            order.getStatus() != OrderStatus.REJECTED && order.getStatus() != OrderStatus.CANCELLED) {
            order.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            });
        }
        // Nếu đơn hàng được xác nhận (chuyển sang SHIPPING), trừ số lượng sản phẩm
        else if (status == OrderStatus.SHIPPING && order.getStatus() == OrderStatus.PENDING) {
            order.getItems().forEach(item -> {
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Not enough stock for product: " + product.getName());
                }
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            });
        }
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getCurrentUserOrders() {
        User currentUser = userService.getCurrentUser();
        return orderRepository.findByUserOrderByCreatedAtDesc(currentUser).stream()
            .map(order -> modelMapper.map(order, OrderDTO.class))
            .collect(Collectors.toList());
    }
}

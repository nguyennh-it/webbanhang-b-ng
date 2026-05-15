package com.example.demo.service;

import com.example.demo.UserRepository.OrderRepository;
import com.example.demo.UserRepository.OrderStatusHistoryRepository;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.OrderStatusHistory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderStatusHistoryRepository orderStatusHistoryRepository;
    public  void updateStatus(String orderId, OrderStatus newStatus,String changedBy){
        Order order=orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
     order.setStatus(newStatus);
     orderRepository.save(order);
     OrderStatusHistory history= OrderStatusHistory.builder()
             .id(UUID.randomUUID().toString())
             .order(order)
             .status(newStatus)
             .changedAt(new Timestamp(System.currentTimeMillis()))
             .changedBy(changedBy)
             .build();
     orderStatusHistoryRepository.save(history);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}

package com.example.demo.service;

import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderStatusHistoryRepository;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.Enum.OrderStatus;
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
    private final OrderDetailRepository orderDetailRepository;
    public  void updateStatus(String orderId, OrderStatus newStatus,String changedBy){
        Order order=orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
     order.setStatus(newStatus);
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

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
        return orderRepository.findAllByOrderByCreatedAtDesc();

    }
    // 1. Hàm lấy thông tin chung của đơn hàng theo ID
    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
    }

    // 2. Hàm lấy danh sách chi tiết các sản phẩm (áo, size, số lượng...) thuộc đơn hàng
// Chú ý: Đổi "OrderDetail" và "orderDetailRepository" thành đúng tên Entity Chi tiết đơn hàng của bạn
    public List<OrderDetail> getOrderDetailsByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

}

package com.example.demo.controller;

import com.example.demo.Enum.PaymentMethod;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
                                //json
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;

    // 1. Tạo payment cho một đơn hàng
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam String orderId,
            @RequestParam PaymentMethod method) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

        Payment payment = paymentService.createInitialPayment(order, method);
        return ResponseEntity.ok(payment);
    }

    // 2. Xem trạng thái payment theo orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getByOrderId(@PathVariable String orderId) {
        Payment payment = paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment cho đơn: " + orderId));
        return ResponseEntity.ok(payment);
    }
}
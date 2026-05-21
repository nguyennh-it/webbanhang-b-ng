package com.example.demo.repository;

import com.example.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_Id(String orderId);

    // Tìm nhanh theo mã giao dịch của cổng thanh toán nếu cần
    Optional<Payment> findByTransactionCode(String transactionCode);
}

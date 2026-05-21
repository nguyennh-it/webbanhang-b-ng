package com.example.demo.entity;

import com.example.demo.Enum.PaymentMethod;
import com.example.demo.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    // CHUẨN HOÁ: Đổi sang Enum để bắt buộc dữ liệu chỉ được là VNPAY, MOMO hoặc COD
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    PaymentMethod paymentMethod;

    @Column(name = "transaction_code", length = 255)
    String transactionCode;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal amount;

    // CHUẨN HOÁ: Đổi sang Enum để tránh gõ sai chính tả trạng thái (PENDING, SUCCESS, FAILED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    PaymentStatus status;

    @Column(name = "paid_at")               // thơ gian thanh toán thành công
    LocalDateTime paidAt;
}
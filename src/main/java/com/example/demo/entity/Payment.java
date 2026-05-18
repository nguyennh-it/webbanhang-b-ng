package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
                                            // thong tin thanh toan
@Entity
@Table(name = "payments") // Khớp chính xác với bảng thông tin thanh toán trong tài liệu thiết kế
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK theo thiết kế
    Long id;

    @OneToOne(fetch = FetchType.LAZY) // Mỗi đơn hàng chỉ có một bản ghi thanh toán tương ứng
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "VARCHAR(255)")
    Order order; // Liên kết sang thực thể Order (Sử dụng String UUID bảo mật)

    @Column(name = "payment_method", nullable = false, length = 50)
    String paymentMethod; // Phương thức thanh toán: VNPAY / MOMO / COD

    @Column(name = "transaction_code", length = 255)
    String transactionCode; // Mã giao dịch trả về từ ngân hàng hoặc cổng thanh toán (Ví dụ: vnp_TransactionNo)

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal amount; // Số tiền khách thực tế đã thanh toán

    @Column(nullable = false, length = 50)
    String status; // Trạng thái giao dịch: PENDING (Chờ), SUCCESS (Thành công), FAILED (Thất bại)

    @Column(name = "paid_at")
    LocalDateTime paidAt; // Thời gian chính xác hệ thống nhận được tiền từ cổng giao dịch
}
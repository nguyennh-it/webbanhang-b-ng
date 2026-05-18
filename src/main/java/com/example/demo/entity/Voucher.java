package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
                                                            //ma giam gia
@Entity
@Table(name = "vouchers") // Ánh xạ chính xác bảng vouchers trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK
    Long id;

    @Column(nullable = false, unique = true, length = 100) // VARCHAR(100) UNIQUE - Mã voucher (VÍ dụ: SUMMER2026)
    String code;

    @Column(nullable = false, length = 255) // VARCHAR(255) - Tên hiển thị của voucher
    String name;

    @Column(name = "discount_type", nullable = false, length = 20) // VARCHAR(20) - PERCENT (Theo %) hoặc FIXED (Số tiền cố định)
    String discountType;

    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2) // DECIMAL(15,2) - Giá trị giảm (% hoặc số tiền)
    BigDecimal discountValue;

    @Column(name = "min_order_amount", nullable = false, precision = 15, scale = 2) // DECIMAL(15,2) - Giá trị đơn hàng tối thiểu để áp dụng
    BigDecimal minOrderAmount;

    @Column(name = "max_discount", nullable = false, precision = 15, scale = 2) // DECIMAL(15,2) - Số tiền giảm tối đa (Đặc biệt quan trọng khi chọn PERCENT)
    BigDecimal maxDiscount;

    @Column(nullable = false) // INT - Số lượng mã phát hành trong kho
    Integer quantity;

    @Column(name = "start_date", nullable = false) // TIMESTAMP - Thời gian bắt đầu có hiệu lực
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false) // TIMESTAMP - Thời gian hết hạn sử dụng
    LocalDateTime endDate;

    @Column(nullable = false, length = 20) // VARCHAR(20) - Trạng thái của mã (ACTIVE / INACTIVE)
    String status;
}
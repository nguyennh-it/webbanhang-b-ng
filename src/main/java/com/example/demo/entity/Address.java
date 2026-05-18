package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
                                                    // dia chi giao hang
@Entity
@Table(name = "addresses") // Ánh xạ chính xác tên bảng trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Khóa ngoại liên kết tới bảng người dùng BIGINT FK
    User user; // Sổ địa chỉ thuộc về tài khoản User nào

    @Column(name = "receiver_name", nullable = false, length = 255) // VARCHAR(255) - Người nhận
    String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20) // VARCHAR(20) - SĐT
    String receiverPhone;

    @Column(nullable = false, length = 100) // VARCHAR(100) - Tỉnh/Thành phố
    String province;

    @Column(nullable = false, length = 100) // VARCHAR(100) - Quận/Huyện
    String district;

    @Column(nullable = false, length = 100) // VARCHAR(100) - Phường/Xã
    String ward;

    @Lob
    @Column(name = "detail_address", columnDefinition = "TEXT", nullable = false) // TEXT - Địa chỉ chi tiết
    String detailAddress;

    @Column(name = "is_default", nullable = false) // BOOLEAN - Địa chỉ mặc định
    Boolean isDefault;
}
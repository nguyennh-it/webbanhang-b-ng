package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
                                            // thuong hieu san pham
@Entity
@Table(name = "brands") // Ánh xạ chính xác tới bảng brands trong database của bạn
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK theo đúng mẫu thiết kế
    Long id;

    @Column(unique = true, nullable = false, length = 255)
    String name; // Tên thương hiệu duy nhất (Ví dụ: Nike, Adidas)

    @Column(length = 500)
    String logo; // Đường dẫn ảnh logo của thương hiệu

    @Column(columnDefinition = "TEXT")
    String description; // Mô tả thông tin thương hiệu

    @Column(length = 100)
    String country; // Quốc gia xuất xứ thương hiệu

    @Column(length = 20)
    String status = "ACTIVE"; // Trạng thái hoạt động mặc định: ACTIVE / INACTIVE
}
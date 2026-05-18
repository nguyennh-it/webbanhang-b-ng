package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "banners") // Ánh xạ chính xác bảng banners trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK
    Long id;

    @Column(nullable = false, length = 255) // VARCHAR(255) - Tiêu đề chương trình/banner
    String title;

    @Column(nullable = false, length = 500) // VARCHAR(500) - Đường dẫn URL hoặc tên file ảnh banner
    String image;

    @Column(length = 500) // VARCHAR(500) - Link điều hướng khi khách click vào (Ví dụ: /products?category=sale)
    String link;

    @Column(name = "sort_order", nullable = false) // INT - Thứ tự hiển thị (Ví dụ: 1, 2, 3 để slider chạy theo đúng ý)
    Integer sortOrder;

    @Column(nullable = false, length = 20) // VARCHAR(20) - Trạng thái hiển thị (ACTIVE / INACTIVE)
    String status;
}
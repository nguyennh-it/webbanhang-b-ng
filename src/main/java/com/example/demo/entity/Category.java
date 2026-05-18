package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
                                                            // danh muc san pham
@Entity
@Table(name = "categories") // Khớp chính xác với tên bảng đã tạo dưới database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT (Long) theo đúng tài liệu thiết kế mẫu
    Long id;

    @Column(nullable = false, length = 255)
    String name; // Tên danh mục (Ví dụ: "Áo bóng đá")

    @Column(unique = true, nullable = false, length = 255)
    String slug; // Đường dẫn SEO URL duy nhất không trùng lặp (Ví dụ: "ao-bong-da")

    @Column(columnDefinition = "TEXT")
    String description; // Mô tả chi tiết danh mục

    @Column(name = "parent_id")
    Long parentId; // ID của danh mục cha (Dùng phân cấp cây thư mục sản phẩm)

    @Column(length = 500)
    String image; // Đường dẫn ảnh đại diện danh mục

    @Column(length = 20)
    String status = "ACTIVE"; // Trạng thái mặc định ban đầu là ACTIVE

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt; // Tự động điền ngày giờ tạo hệ thống dưới database
}
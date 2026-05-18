package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
                                            // danh gia san pham
@Entity
@Table(name = "reviews") // Ánh xạ chính xác bảng reviews trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Khóa ngoại liên kết tới bảng người dùng BIGINT FK
    User user; // Người thực hiện đánh giá sản phẩm

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Khóa ngoại liên kết tới bảng sản phẩm BIGINT FK
    Product product; // Sản phẩm được đánh giá

    @Column(nullable = false) // INT - Số sao đánh giá từ 1 -> 5
    Integer rating;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false) // TEXT - Nội dung bình luận chi tiết
    String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // TIMESTAMP - Ngày tạo đánh giá
    LocalDateTime createdAt;
}
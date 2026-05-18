package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
                                        //Danh sach yeu thich
@Entity
@Table(name = "wishlists") // Ánh xạ chính xác bảng wishlists trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Khóa ngoại liên kết tới bảng người dùng BIGINT FK
    User user; // Người dùng sở hữu sản phẩm yêu thích này

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Khóa ngoại liên kết tới bảng sản phẩm BIGINT FK
    Product product; // Sản phẩm được đưa vào danh sách yêu thích

    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // TIMESTAMP - Thời gian thêm vào danh sách
    LocalDateTime createdAt;
}
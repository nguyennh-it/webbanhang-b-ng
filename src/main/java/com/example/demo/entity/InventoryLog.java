package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
                                                // lich su nhap xuat kho
@Entity
@Table(name = "inventory_logs") // Ánh xạ chính xác bảng inventory_logs trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false) // Khóa ngoại liên kết tới biến thể sản phẩm BIGINT FK
    ProductVariant productVariant; // Bản ghi này thuộc về biến thể sản phẩm (Size/Màu) nào

    @Column(nullable = false, length = 20) // VARCHAR(20) - Loại biến động: IMPORT (Nhập kho) / EXPORT (Xuất kho)
    String type;

    @Column(nullable = false) // INT - Số lượng sản phẩm nhập vào hoặc xuất ra
    Integer quantity;

    @Lob
    @Column(columnDefinition = "TEXT") // TEXT - Ghi chú lý do nhập/xuất (Ví dụ: "Nhập hàng đợt hè", "Xuất kho cho đơn hàng ORD-123")
    String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // TIMESTAMP - Thời gian ghi nhận lịch sử kho
    LocalDateTime createdAt;
}
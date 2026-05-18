package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
                                                            // bien the cua mau sac
@Entity
@Table(name = "product_variants") // Khớp chính xác với tên bảng thiết kế dưới database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Tự sinh chuỗi UUID bảo mật đồng bộ với luồng Cart/Order của bạn
    @Column(columnDefinition = "VARCHAR(255)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Liên kết khóa ngoại trỏ sang sản phẩm chính (BIGINT)
    Product product;

    @Column(unique = true, length = 64)
    String sku; // Mã quản lý kho định danh duy nhất (Ví dụ: NIKE-AIR-W-40)

    @Column(length = 32)
    String size; // Kích cỡ (Ví dụ: M, L, XL, 39, 40)

    @Column(length = 32)
    String color; // Màu sắc (Ví dụ: Đỏ, Đen, Trắng)

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal price; // Giá bán mặc định của biến thể này

    @Column(name = "sale_price", precision = 15, scale = 2)
    BigDecimal salePrice; // Giá giảm khuyến mãi riêng của biến thể (nếu có)

    @Column(name = "stock_quantity", nullable = false)
    Integer stockQuantity = 0; // Số lượng tồn kho thực tế của size/màu này

    @Column(precision = 10, scale = 2)
    BigDecimal weight; // Trọng lượng mặt hàng (phục vụ tính phí vận chuyển tự động)

    @Column(length = 500)
    String image; // Ảnh riêng cho phối màu này (nếu khách chọn màu thì giao diện đổi ảnh)

    @Column(length = 20)
    String status = "ACTIVE"; // Trạng thái biến thể: ACTIVE / INACTIVE

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt; // Tự động ghi nhận thời gian tạo

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt; // Tự động ghi nhận thời gian cập nhật kho/giá
}
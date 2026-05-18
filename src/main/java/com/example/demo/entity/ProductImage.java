package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
                                    //anh phu cua san pham
@Entity
@Table(name = "product_images") // Khớp chính xác với tên bảng thiết kế mẫu dưới database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khóa chính số tự tăng BIGINT PK theo thiết kế
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Liên kết khóa ngoại trỏ sang thực thể Product chính
    Product product;

    @Column(name = "image_url", nullable = false, length = 500)
    String imageUrl; // Đường dẫn link ảnh chi tiết của sản phẩm

    @Column(name = "is_thumbnail")
    Boolean isThumbnail = false; // Đánh dấu đây có phải ảnh đại diện chính hay không

    @Column(name = "display_order")
    Integer displayOrder = 0; // Thứ tự hiển thị của ảnh trong album (Ví dụ: ảnh 1, ảnh 2, ảnh 3)
}
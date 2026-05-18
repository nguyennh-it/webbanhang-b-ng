package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantRequest {
    @NotBlank(message = "Size không được để trống")
    String size;
    String color; // Có thể để trống nếu không có màu sắc
    @Min(value = 0, message = "Số lượng tồn kho không hợp lệ")
    Integer stockQuantity;
    @Min(value = 0, message = "Giá không hợp lệ")
    BigDecimal price; // Giá của biến thể
    BigDecimal salePrice; // Giá khuyến mãi của biến thể
    String image; // Ảnh riêng cho biến thể
}

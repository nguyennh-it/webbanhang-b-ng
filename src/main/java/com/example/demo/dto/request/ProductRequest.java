package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;
    String description;
    @Min(value = 0, message = "INVALID_PRICE")
    double price;
    int stock;
    String category;
    Long categoryId;
    String imageUrl;
    private List<String> sizes;
}

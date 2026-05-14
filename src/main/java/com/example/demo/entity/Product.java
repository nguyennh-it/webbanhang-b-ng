package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Id;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;         //mô tả
    double price;
    int stock;
    String category;
    String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductSize> sizes;

    Long categoryId;
    Long productVariantId;
}
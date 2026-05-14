package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String size;

    private int stock;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
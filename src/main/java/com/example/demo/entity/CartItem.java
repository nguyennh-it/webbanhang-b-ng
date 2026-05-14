package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    Product product;

    int quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    private ProductSize productSize;
}
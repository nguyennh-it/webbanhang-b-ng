package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
//Đại diện cho một sản phẩm trong giỏ hàng của người dùng.
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne                      //một dòng hàng có nhiều trong giỏ
    Product product;

    int quantity;

    @ManyToOne                          //một user có thể có nhiều hàng
    @JoinColumn(name = "user_id")               // biết giỏ hàng này là của ai
    User user;

    // Quan hệ ngược: Cart -> CartItem (mappedBy = "cart")
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    private ProductSize productSize;
}
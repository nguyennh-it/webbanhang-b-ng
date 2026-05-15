package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
                                    //đại diện cho một đơn hàng tổng thể của người dùng.
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "total_price")
    private double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String address;

    @Column(name = "created_at")
    private Timestamp createdAt; // Đổi từ created_at thành createdAt
}
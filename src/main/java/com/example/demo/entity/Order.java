package com.example.demo.entity;

import com.example.demo.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String address;
    @UpdateTimestamp
    private Timestamp updatedAt;
    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private Timestamp createdAt; // Đổi từ created_at thành createdAt
}
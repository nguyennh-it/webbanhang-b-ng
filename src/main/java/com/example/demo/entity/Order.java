package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String id; // varchar(255) trong sơ đồ DB của mày

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double total_price; // Ghi đúng như sơ đồ image_f16b76.jpg
    private String status;
    private String address;
    private Timestamp created_at;
}
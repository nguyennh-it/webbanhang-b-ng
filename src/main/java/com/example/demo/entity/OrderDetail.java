package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
                                //đại diện cho chi tiết từng sản phẩm trong đơn hàng.
@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double price;
    @ManyToOne
    private ProductSize productSize;
}
package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    // Để Integer vì trong sơ đồ DB của mày ID là int
    List<OrderDetail> findByOrderId(String orderId);
    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od WHERE od.product.id = :productId")
    Long countSoldByProductId(@Param("productId") String productId);
}
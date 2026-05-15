package com.example.demo.UserRepository;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    // Để Integer vì trong sơ đồ DB của mày ID là int
    List<OrderDetail> findByOrderId(String orderId);
}
package com.example.demo.UserRepository;


import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByOrderByUpdatedAtDesc();
    List<Order> findAllByOrderByCreatedAtDesc();

}
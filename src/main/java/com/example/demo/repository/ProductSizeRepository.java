package com.example.demo.repository;

import com.example.demo.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository // 1. Bổ sung để Spring nhận diện Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, String> {

    @Modifying
    @Transactional // 2. Bắt buộc phải có đối với câu lệnh UPDATE/DELETE
    @Query("UPDATE ProductSize ps SET ps.stock = ps.stock - :quantity WHERE ps.id = :id AND ps.stock >= :quantity")
    int decreaseStock(@Param("id") String id, @Param("quantity") int quantity);
}
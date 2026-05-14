package com.example.demo.UserRepository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,String> {
    boolean existsByName(String name);
    // Thêm tham số Pageable và đổi kiểu trả về thành Page
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

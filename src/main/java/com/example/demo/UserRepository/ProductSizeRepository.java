package com.example.demo.UserRepository;

import com.example.demo.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRepository extends JpaRepository<ProductSize,String> {
}

package com.example.demo.UserRepository;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    Optional<ProductVariant> findByProductAndSize(Product product, String size);
}

package com.example.demo.UserRepository;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductSize;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    CartItem findByUserAndProduct(User user, Product product);
    List<CartItem> findByUserUsername(String username);
    CartItem findByUserAndProductAndProductSize(
            User user,
            Product product,
            ProductSize productSize
    );
    void deleteByUserUsername(String username);
}
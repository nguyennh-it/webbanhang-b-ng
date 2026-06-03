package com.example.demo.repository;

import com.example.demo.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(String userId);

    @org.springframework.data.jpa.repository.Query("select w from Wishlist w join fetch w.product where w.user.id = :userId")
    List<Wishlist> findByUserIdWithProduct(String userId);

    Optional<Wishlist> findByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    long countByUserId(String userId);
}
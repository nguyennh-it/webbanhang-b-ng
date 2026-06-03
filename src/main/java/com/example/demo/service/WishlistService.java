package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WishlistRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistService {

    WishlistRepository wishlistRepository;
    UserRepository userRepository;
    ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Wishlist> getWishlist(String userId) {
        return wishlistRepository.findByUserIdWithProduct(userId);
    }

    @Transactional
    public boolean addToWishlist(String userId, String productId) {
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) return false;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        wishlistRepository.save(Wishlist.builder().user(user).product(product).build());
        return true;
    }

    @Transactional
    public void removeFromWishlist(String userId, String productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public boolean toggleWishlist(String userId, String productId) {
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return false;
        } else {
            addToWishlist(userId, productId);
            return true;
        }
    }

    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    public long countWishlist(String userId) {
        return wishlistRepository.countByUserId(userId);
    }
}
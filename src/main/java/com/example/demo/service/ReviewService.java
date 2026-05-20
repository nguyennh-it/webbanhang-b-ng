package com.example.demo.service;

import com.example.demo.UserRepository.ProductRepository;
import com.example.demo.UserRepository.ReviewRepository;
import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    public List<Review> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    public Review addReview(ReviewRequest request){
        Product product=productRepository.findById(request.getProductId())
                .orElseThrow(()->new RuntimeException("Không Tìm Thấy Sản Phẩm "));
        Review review=Review.builder()
                .reviewerName(request.getReviewerName())
                .comment(request.getComment())
                .rating(request.getRating())
                .product(product)
                .user(null)
                .build();
        return reviewRepository.save(review);
    }
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}

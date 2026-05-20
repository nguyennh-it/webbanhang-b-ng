package com.example.demo.Controller;

import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/store")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    @PostMapping("/products/{productId}/reviews")
    public String addReview(@PathVariable String productId,
                            @ModelAttribute ReviewRequest request) {
        request.setProductId(productId);
        reviewService.addReview(request);
        return "redirect:/store/products/" + productId;
    }

    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                               @RequestParam String productId) {
        reviewService.deleteReview(reviewId);
        return "redirect:/store/products/" + productId;
    }
}
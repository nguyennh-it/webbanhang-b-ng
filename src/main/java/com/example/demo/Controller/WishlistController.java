package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.WishlistService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/wishlist")
public class WishlistController {

    WishlistService wishlistService;
    UserRepository userRepository;  // ← thêm để tìm userId từ username

    // Helper: lấy userId (String UUID) từ username đang đăng nhập
    private String getUserId(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + username));
        return user.getId();
    }

    @GetMapping
    public String viewWishlist(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String userId = getUserId(userDetails);
        List<Wishlist> items = wishlistService.getWishlist(userId);
        model.addAttribute("wishlistItems", items);
        model.addAttribute("wishlistCount", items.size());
        return "wishlist";
    }

    @PostMapping("/toggle/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggle(
            @PathVariable String productId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = getUserId(userDetails);
        boolean added = wishlistService.toggleWishlist(userId, productId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "added", added,
                "message", added ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích"
        ));
    }

    @PostMapping("/remove/{productId}")
    public String remove(@PathVariable String productId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getUserId(userDetails);
        wishlistService.removeFromWishlist(userId, productId);
        return "redirect:/wishlist";
    }
}
package com.example.demo.controller;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.WishlistService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductViewController {

    ProductService productService;
    ReviewService reviewService;
    CategoryService categoryService;
    WishlistService wishlistService;
    UserRepository userRepository;  // ← thêm

    // Helper lấy userId từ username
    private String getUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        return user.getId();
    }

    @GetMapping("/products")
    public String listProducts(
            Model model,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        int pageSize = 6;
        var pageData = productService.getProducts(page, pageSize, keyword, categoryId);
        model.addAttribute("products", pageData.getContent());
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categories", categoryService.getAllCategories());

        // Lấy wishlistIds nếu đã đăng nhập
        if (userDetails != null) {
            String userId = getUserId(userDetails);
            List<String> wishlistIds = wishlistService.getWishlist(userId)
                    .stream()
                    .map(w -> w.getProduct().getId())
                    .collect(Collectors.toList());
            model.addAttribute("wishlistIds", wishlistIds);
        } else {
            model.addAttribute("wishlistIds", Collections.emptyList());
        }

        return "product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("request", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-product";
    }

    @PostMapping({"", "/", "/add"})
    public String addProduct(@Valid @ModelAttribute("request") ProductRequest request,
                             BindingResult result, Model model) {
        if (result.hasErrors()) return "add-product";
        productService.createProduct(request);
        return "redirect:/store/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        return "edit-product";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable String id,
                                @Valid @ModelAttribute ProductRequest request,
                                BindingResult result) {
        if (result.hasErrors()) return "edit-product";
        productService.updateProduct(id, request);
        return "redirect:/store/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return "redirect:/store/products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable String id, Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("product", productService.getProduct(id));
        model.addAttribute("reviews", reviewService.getReviewsByProductId(id));
        model.addAttribute("reviewRequest", new ReviewRequest());

        if (userDetails != null) {
            String userId = getUserId(userDetails);
            model.addAttribute("isWishlisted", wishlistService.isInWishlist(userId, id));
        } else {
            model.addAttribute("isWishlisted", false);
        }

        return "product-detail";
    }
}
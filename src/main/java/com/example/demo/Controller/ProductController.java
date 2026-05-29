package com.example.demo.controller;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.BrandService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;
    BrandService brandService;
    CategoryService categoryService;

    @PostMapping("/add")
    public String createProduct(@ModelAttribute @Valid ProductRequest request) {
        productService.createProduct(request);
        return "redirect:/store/products";  // sửa lại cho đúng
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ApiResponse<ProductResponse> getProduct(@PathVariable String id) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(id))
                .build();
    }

    @GetMapping("/products/{id}")
    public String viewProductDetail(@PathVariable String id, Model model) {
        var product = productService.getProduct(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    // ĐÃ XÓA method showStorePage ở đây
}
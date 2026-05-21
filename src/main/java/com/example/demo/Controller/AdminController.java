package com.example.demo.controller;

import com.example.demo.service.CategoryService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getProducts(0, 1000, null, null).getTotalElements());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("totalCategories", categoryService.getAllCategories().size());
        return "admin/dashboard";
    }

    @GetMapping("/product")
    public String products(Model model) {
        model.addAttribute("products", productService.getProducts(0, 1000, null, null).getContent());
        return "admin/products";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }
}
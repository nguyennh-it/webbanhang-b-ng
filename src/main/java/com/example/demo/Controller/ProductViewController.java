package com.example.demo.Controller;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductViewController {

    ProductService productService;

    // Hiển thị danh sách sản phẩm
    @GetMapping("/products")
    public String listProducts(
            Model model,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        int pageSize = 6;  // Bạn muốn hiện bao nhiêu sản phẩm trên 1 trang thì sửa ở đây

        // Gọi hàm getProducts mới trả về đối tượng Page
        var pageData = productService.getProducts(page, pageSize, keyword);
        model.addAttribute("products", pageData.getContent());
        // Đẩy dữ liệu ra giao diện
        model.addAttribute("products", pageData.getContent()); // Danh sách sản phẩm của trang hiện tại
        model.addAttribute("totalPages", pageData.getTotalPages()); // Tổng số trang (để vẽ nút 1,2,3)
        model.addAttribute("currentPage", page); // Trang hiện tại (để tô màu nút đang chọn)
        model.addAttribute("keyword", keyword); // Giữ lại từ khóa tìm kiếm trên thanh search

        return "product-list";
    }
    // Hiển thị form thêm sản phẩm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("request", new ProductRequest()); // ← Thymeleaf cần object này
        return "add-product";
    }

    // Xử lý thêm sản phẩm
    @PostMapping({"","/","/add"})
    public String addProduct(@Valid @ModelAttribute("request") ProductRequest request, BindingResult result,Model model) {
        if (result.hasErrors()) {
            return "add-product"; // Quay lại form add để hiện thông báo lỗi
        }
        productService.createProduct(request);
        return "redirect:/store/products";
    }

    // Hiển thị form sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        return "edit-product"; // → templates/edit-product.html
    }

    // Xử lý cập nhật sản phẩm
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable String id, @Valid @ModelAttribute ProductRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "edit-product"; // Quay lại form edit nếu có lỗi
        }
        productService.updateProduct(id, request);
        return "redirect:/store/products";
    }

    // Xử lý xóa sản phẩm
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return "redirect:/store/products";
    }

    // Hiển thị danh sách sản phẩm (Có hỗ trợ tìm kiếm)

}
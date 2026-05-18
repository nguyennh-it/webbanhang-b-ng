package com.example.demo.Controller;
import com.example.demo.dto.request.ProductRequest;
import com.example.demo.service.CategoryService;
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
    CategoryService categoryService;  // ← thêm dòng này dưới ProductService

    @GetMapping("/products")
    public String listProducts(
            Model model,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "categoryId", required = false) Long categoryId) {  // ← thêm categoryId

        int pageSize = 6;
        var pageData = productService.getProducts(page, pageSize, keyword, categoryId);  // ← thêm categoryId
        model.addAttribute("products", pageData.getContent());
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);  // ← thêm
        model.addAttribute("categories", categoryService.getAllCategories());  // ← thêm

        return "product-list";
    }
    // Hiển thị form thêm sản phẩm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("request", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
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
    // Hiển thị chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable String id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        return "product-detail";
    }
}
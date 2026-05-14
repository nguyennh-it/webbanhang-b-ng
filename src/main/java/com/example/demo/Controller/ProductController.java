package com.example.demo.Controller;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // Đổi từ @RestController thành @Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping("/add") // Nên thêm path cụ thể cho form
    public String createProduct(@ModelAttribute @Valid ProductRequest request) {
        // Dùng @ModelAttribute thay vì @RequestBody để nhận dữ liệu từ Form
        productService.createProduct(request);

        // Trả về "redirect" để trình duyệt tự chuyển hướng về trang danh sách, 
        // tránh hiện ra màn hình JSON đen như hình image_3959d7.png
        return "redirect:/store/products";
    }

    // Các hàm trả về JSON cho API vẫn có thể giữ lại nhưng cần thêm @ResponseBody
    @GetMapping("/{id}")
    @ResponseBody
    public ApiResponse<ProductResponse> getProduct(@PathVariable String id){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(id))
                .build();
    }
}
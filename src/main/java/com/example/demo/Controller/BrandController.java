package com.example.demo.controller;

import com.example.demo.entity.Brand;
import com.example.demo.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/brands")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {

    BrandService brandService;

    // 1. Xem danh sách brand
    @GetMapping
    public String getAllBrands(Model model){
        // Giữ nguyên theo ý bạn, ngoài file admin/brands.html sẽ gọi th:each="b : ${brand}"
        model.addAttribute("brand", brandService.getAll());
        return "admin/brands";
    }

    // 2. Hiện form thêm mới
    @GetMapping("/create")
    public String showCreateForm(Model model){
        // Đã sửa: "brands" -> "brand" cho đồng bộ với th:object="${brand}"
        model.addAttribute("brand", new Brand());
        return "admin/brands-create";
    }

    // 3. Xử lý lưu form thêm mới
    @PostMapping("/create")
    public String createBrand(@ModelAttribute Brand brand){
        if (brand.getStatus() == null) {
            brand.setStatus("INACTIVE");
        }
        brandService.create(brand);
        return "redirect:/admin/brands";
    }

    // 4. Hiện form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        // Đã sửa: "brands" -> "brand" để sang trang edit bind dữ liệu cũ lên form chuẩn xác
        model.addAttribute("brand", brandService.getById(id));
        return "admin/brand-edit";
    }

    // 5. Xử lý lưu dữ liệu sau khi sửa
    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable Long id, @ModelAttribute Brand brand){
        if (brand.getStatus() == null) {
            brand.setStatus("INACTIVE");
        }
        brandService.update(id, brand);
        return "redirect:/admin/brands";
    }

    // 6. Xóa brand
    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id){
        brandService.delete(id);
        return "redirect:/admin/brands";
    }
}
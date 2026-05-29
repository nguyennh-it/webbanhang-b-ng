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
        model.addAttribute("brands", brandService.getAll());
        return "admin/brands";
    }

    // 2. Hiện form thêm mới
    @GetMapping("/create")
    public String showCreateForm(Model model){
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
    // ĐÃ SỬA: Thêm ("id")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model){
        model.addAttribute("brand", brandService.getById(id));
        return "admin/brand-edit";
    }

    // 5. Xử lý lưu dữ liệu sau khi sửa
    // ĐÃ SỬA: Thêm ("id")
    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable("id") Long id, @ModelAttribute Brand brand){
        if (brand.getStatus() == null) {
            brand.setStatus("INACTIVE");
        }
        brandService.update(id, brand);
        return "redirect:/admin/brands";
    }

    // 6. Xóa brand
    // ĐÃ SỬA: Thêm ("id")
    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable("id") Long id){
        brandService.delete(id);
        return "redirect:/admin/brands";
    }
}
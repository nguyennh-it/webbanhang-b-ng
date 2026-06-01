package com.example.demo.controller;

import com.example.demo.Enum.OrderStatus; // ← Đảm bảo import đúng Enum này
import com.example.demo.service.CategoryService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // ← Thêm import này để lấy người đổi trạng thái
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ← Thêm import này để truyền thông báo lỗi/thành công

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getProducts(0, 1000, null, null,null).getTotalElements());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("totalCategories", categoryService.getAllCategories().size());
        return "admin/dashboard"; //
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getProducts(0, 1000, null, null,null).getContent());
        return "admin/product-list"; //
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "admin/orders"; //
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories"; //
    }

    // 🚀 BỔ SUNG THÊM HÀM NÀY ĐỂ XỬ LÝ ĐỔI TRẠNG THÁI + TRỪ KHO
    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(@PathVariable("id") String orderId,
                                    @RequestParam("status") OrderStatus status,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Lấy tên Admin đang đăng nhập hệ thống, nếu không lấy được thì để mặc định là "ADMIN"
            String changedBy = (authentication != null) ? authentication.getName() : "ADMIN";

            // Gọi Service thực hiện kiểm tra trạng thái cũ, nếu chuyển sang COMPLETED sẽ tự trừ kho
            orderService.updateStatus(orderId, status, changedBy);

            // Nếu chạy mượt mà không lỗi, tạo thông báo thành công đẩy ra giao diện
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (IllegalStateException e) {
            // Bắt đúng lỗi văng ra từ Service: "Không đủ số lượng tồn kho!"
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }

        // Sau khi xử lý xong, chuyển hướng lộn ngược lại trang danh sách đơn hàng của Admin
        return "redirect:/admin/orders";
    }
}
package com.example.demo.controller;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. Import thư viện log
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j // 2. Thêm Log lên đầu class
public class OrderController {

    private final OrderService orderService;

    // HIỂN THỊ DANH SÁCH
    @GetMapping
    public String viewOrders(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        // ❌ HÀM GET THUẦN TÚY: Né log để tránh rác Terminal khi người dùng F5 xem danh sách đơn hàng liên tục
        model.addAttribute("orders", orderService.getOrdersForUser(auth.getName()));
        return "orders";
    }

    // UPDATE STATUS (Sửa trạng thái đơn hàng)
    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable("id") String id,
                                    @RequestParam("newStatus") String newStatus) {

        // 📍 VÙNG HÀNH ĐỘNG: Ghi nhận admin yêu cầu đổi trạng thái đơn
        log.info("🔄 Admin yêu cầu thay đổi trạng thái đơn hàng ID: [{}] sang thành: [{}]", id, newStatus);

        try {
            orderService.updateStatus(
                    id,
                    OrderStatus.valueOf(newStatus.trim().toUpperCase()),
                    "admin"
            );

            // 📍 VÙNG ĐÚNG: Đổi trạng thái thành công trong Database
            log.info("✅ Cập nhật trạng thái đơn hàng [{}] sang [{}] THÀNH CÔNG.", id, newStatus);

        } catch (IllegalArgumentException e) {
            // 📍 VÙNG SAI (CATCH 1): Lỗi do truyền sai chữ trạng thái (Ví dụ: trạng thái không tồn tại trong Enum)
            log.error("❌ Trạng thái đơn hàng không hợp lệ: [{}]", newStatus);
        } catch (Exception e) {
            // 📍 VÙNG SAI (CATCH 2): Lỗi hệ thống bất ngờ (sập mạng, sập DB)
            log.error("💥 Lỗi nghiêm trọng khi cập nhật trạng thái đơn hàng [{}]: ", id, e);
        }

        return "redirect:/orders";
    }

    // XEM CHI TIẾT ĐƠN HÀNG (ÁO GÌ, SIZE GÌ, SỐ LƯỢNG...)
    @GetMapping("/chi-tiet/{id}")
    public String viewOrderDetail(@PathVariable("id") String id, Model model) {

        // 🛠️ THAY THẾ SYSTEM.OUT.PRINTLN: Chuyển sang dùng log.info chuẩn chỉ
        log.info("🔍 User đang xem chi tiết đơn hàng ID: [{}]", id);

        try {
            // 1. Lấy thông tin chung của đơn hàng (để hiện mã đơn, trạng thái...)
            model.addAttribute("order", orderService.getOrderById(id));

            // 2. Lấy danh sách các sản phẩm (chi tiết) nằm trong đơn hàng đó
            model.addAttribute("orderDetails", orderService.getOrderDetailsByOrderId(id));

        } catch (Exception e) {
            // Đề phòng trường hợp gõ sai ID đơn hàng trên URL dẫn tới không tìm thấy đơn
            log.error("❌ Không lấy được chi tiết đơn hàng ID [{}]. Lý do: {}", id, e.getMessage());
        }

        return "order-detail";
    }
}
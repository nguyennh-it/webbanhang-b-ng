package com.example.demo.Controller;

import com.example.demo.entity.OrderStatus;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // HIỂN THỊ DANH SÁCH
    @GetMapping
    public String viewOrders(Model model) {

        System.out.println("🔥 ORDER CONTROLLER IS CALLED");

        model.addAttribute("orders", orderService.getAllOrders());  //gọi xuống servie lấy toàn bộ ds

        return "orders";
    }

    // UPDATE STATUS
    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable String id,
                                    @RequestParam String newStatus) {

        orderService.updateStatus(
                id,
                OrderStatus.valueOf(newStatus.trim().toUpperCase()),
                "admin"
        );

        return "redirect:/orders";
    }
    // XEM CHI TIẾT ĐƠN HÀNG (ÁO GÌ, SIZE GÌ, SỐ LƯỢNG...)
    @GetMapping("/chi-tiet/{id}")
    public String viewOrderDetail(@PathVariable String id, Model model) {

        System.out.println("🔥 VIEW ORDER DETAIL IS CALLED FOR ID: " + id);

        // 1. Lấy thông tin chung của đơn hàng (để hiện mã đơn, trạng thái...)
        model.addAttribute("order", orderService.getOrderById(id));

        // 2. Lấy danh sách các sản phẩm (chi tiết) nằm trong đơn hàng đó
        // Bạn check xem trong OrderService của bạn tên hàm lấy chi tiết đơn hàng là gì nhé
        // Ví dụ: getOrderDetailsByOrderId(id) hoặc getOrderItemsByOrderId(id)
        model.addAttribute("orderDetails", orderService.getOrderDetailsByOrderId(id));

        return "order-detail"; // Trả về trang giao diện order-detail.html
    }
}
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
}
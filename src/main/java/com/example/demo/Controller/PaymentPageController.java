package com.example.demo.controller;

import com.example.demo.Enum.PaymentMethod;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
                                                        //form
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentPageController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping
    public String showPaymentPage(
            @RequestParam String orderId,
            @RequestParam(required = false) Double finalAmount,
            Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        model.addAttribute("orderId", orderId);
        model.addAttribute("finalAmount", finalAmount); // ← thêm dòng này
        return "payment";
    }

    @PostMapping("/confirm")
    public String confirmPayment(
            @RequestParam String orderId,
            @RequestParam PaymentMethod method,
            Authentication auth) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentService.createInitialPayment(order, method);

        if (method == PaymentMethod.COD) {
            paymentService.updatePaymentSuccess(payment, "COD-" + orderId);
            return "redirect:/cart/thank-you";
        }

        // VNPAY / MOMO: sẽ thêm sau
        return "redirect:/cart/thank-you";
    }
}
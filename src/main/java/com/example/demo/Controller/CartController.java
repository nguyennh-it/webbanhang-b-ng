package com.example.demo.controller;

import com.example.demo.entity.CartItem;
import com.example.demo.service.CartService;
import com.example.demo.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
                                                    //Giỏ Hàng
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final VoucherService voucherService;
    @GetMapping({"","/", "/view"})
    public String viewCart(Model model, Authentication auth) {

        List<CartItem> cartItems;
        if (auth != null) {
            cartItems = cartService.getCartItems(auth.getName());
        } else {
            cartItems = java.util.Collections.emptyList(); // Giỏ trống nếu chưa login
        }

        // Tính tổng tiền của cả giỏ hàng
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("items", cartItems); // Đặt tên là "items"
        model.addAttribute("totalPrice", total); // Gửi tổng tiền sang HTML
        model.addAttribute("isLoggedIn", auth != null); // Flag để template biết có login không
        return "cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(
            @PathVariable String id,
            @RequestParam String sizeId,
            Authentication auth
    ) {

        if (auth == null) return "redirect:/login";

        cartService.addToCart(id, sizeId, auth.getName());

        return "redirect:/cart";
    }

    @PostMapping("/add")
    public String addToCartNoId() {
        return "redirect:/store/products";
    }
    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") String id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }
    @PostMapping("/checkout")
    public String checkout(Authentication auth) {
        if (auth == null) return "redirect:/login";

        try {
                                                                // 1. Gọi service xử lý lưu đơn hàng và xóa giỏ rác cũ
            cartService.checkout(auth.getName());

                                                                // 2. TỰ ĐỘNG CHUYỂN HƯỚNG: Thanh toán thành công, nhảy thẳng sang trang đơn hàng
            return "redirect:/orders";
        } catch (Exception e) {
            // Nếu có lỗi phát sinh (hết hàng, lỗi DB...), quay về giỏ kèm tin nhắn lỗi
            return "redirect:/cart?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
    @GetMapping("/order")
    public  String showOrderForm(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";

        // 1. Lấy thông tin giỏ hàng hiện tại để hiển thị lại ở form thanh toán
        List<CartItem> cartItems = cartService.getCartItems(auth.getName());
        model.addAttribute("items", cartItems);

        // 2. Tính tổng tiền để hiển thị ở form thanh toán
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("items", cartItems);
        model.addAttribute("totalPrice", total);

        // 3. Trả về trang giao diện order-form.html
        return "order-form";
    }
                                                        @PostMapping("/order")
                                                        public String submitOrder(
                                                                @RequestParam String fullName,
                                                                @RequestParam String phone,
                                                                @RequestParam String address,
                                                                @RequestParam String city,
                                                                @RequestParam(required = false) String voucherCode,
                                                                @RequestParam(required = false) Double finalAmount,
                                                                Authentication auth) {
                                                            if (auth == null) return "redirect:/login";

                                                            String orderId = cartService.checkout(auth.getName());

                                                            // Trừ quantity voucher nếu có dùng
                                                            if (voucherCode != null && !voucherCode.isEmpty()) {
                                                                voucherService.consumeVoucher(voucherCode);
                                                            }

                                                            // Truyền finalAmount sang trang thanh toán
                                                            String redirectUrl = "redirect:/payment?orderId=" + orderId;
                                                            if (finalAmount != null) {
                                                                redirectUrl += "&finalAmount=" + finalAmount.longValue();
                                                            }
                                                            return redirectUrl;
    }
        @GetMapping("/thank-you")
        public String thankYou() {
        return "thank-you";
    }
}
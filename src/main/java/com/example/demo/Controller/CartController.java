package com.example.demo.controller;

import com.example.demo.entity.CartItem;
import com.example.demo.service.CartService;
import com.example.demo.service.VoucherService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
                                                    //Giỏ Hàng
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final VoucherService voucherService;
    @GetMapping({"","/", "/view"})
    public String viewCart(Model model, Authentication auth, HttpSession session) {

        List<CartItem> cartItems;
        if (auth != null) {
            cartItems = cartService.getCartItems(auth.getName());
        } else {
            cartItems = (List<CartItem>) session.getAttribute("anonymousCart");
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }
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
            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
            Authentication auth,
            HttpSession session
    ) {

        try {
            if (auth == null) {
                // Lưu tạm giỏ hàng vào session cho khách chưa đăng nhập
                List<CartItem> anonymousCart = (List<CartItem>) session.getAttribute("anonymousCart");
                if (anonymousCart == null) {
                    anonymousCart = new ArrayList<>();
                }

                var product = cartService.getProductById(id);
                var productSize = cartService.getProductSizeById(sizeId);

                if (productSize.getStock() <= 0) {
                    throw new RuntimeException("Sản phẩm này hiện đã hết hàng!");
                }

                CartItem existingItem = anonymousCart.stream()
                        .filter(item -> item.getProduct().getId().equals(id)
                                && item.getProductSize().getId().equals(sizeId))
                        .findFirst()
                        .orElse(null);

                int currentInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
                int totalRequested = currentInCart + quantity;

                if (productSize.getStock() < totalRequested) {
                    if (currentInCart > 0) {
                        throw new RuntimeException("Kho không đủ hàng! Bạn đã có " + currentInCart
                                + " sản phẩm trong giỏ, kho chỉ còn " + productSize.getStock() + " sản phẩm.");
                    } else {
                        throw new RuntimeException("Kho không đủ hàng! Bạn chỉ có thể mua tối đa " + productSize.getStock() + " sản phẩm.");
                    }
                }

                if (existingItem != null) {
                    existingItem.setQuantity(totalRequested);
                } else {
                    anonymousCart.add(CartItem.builder()
                            .id(java.util.UUID.randomUUID().toString())
                            .product(product)
                            .productSize(productSize)
                            .quantity(quantity)
                            .build());
                }

                session.setAttribute("anonymousCart", anonymousCart);
                return "redirect:/cart";
            }

            // Gọi service để kiểm tra logic tồn kho và thêm vào giỏ
            cartService.addToCart(id, sizeId, auth.getName(), quantity);
            return "redirect:/cart";
        } catch (Exception e) {
            return "redirect:/store/products/" + id + "?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/add")
    public String addToCartNoId() {
        return "redirect:/store/products";
    }
    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") String id, Authentication auth, HttpSession session) {
        if (auth == null) {
            List<CartItem> anonymousCart = (List<CartItem>) session.getAttribute("anonymousCart");
            if (anonymousCart != null) {
                anonymousCart.removeIf(item -> id.equals(item.getId()));
                session.setAttribute("anonymousCart", anonymousCart);
            }
        } else {
            cartService.removeFromCart(id);
        }
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
    public String showOrderForm(Authentication auth, Model model, HttpSession session) {
        List<CartItem> cartItems;
        if (auth != null) {
            cartItems = cartService.getCartItems(auth.getName());
        } else {
            cartItems = (List<CartItem>) session.getAttribute("anonymousCart");
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }
        }

        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("items", cartItems);
        model.addAttribute("totalPrice", total);

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
            Authentication auth,
            HttpSession session) {

        try {
            String orderId;
            if (auth != null) {
                orderId = cartService.checkout(auth.getName());
            } else {
                List<CartItem> anonymousCart = (List<CartItem>) session.getAttribute("anonymousCart");
                if (anonymousCart == null || anonymousCart.isEmpty()) {
                    return "redirect:/cart?error=" + java.net.URLEncoder.encode("Giỏ hàng đang trống", java.nio.charset.StandardCharsets.UTF_8);
                }
                String fullAddress = address + ", " + city;
                orderId = cartService.checkoutAnonymous(anonymousCart, fullAddress);
                session.removeAttribute("anonymousCart");
            }

            if (voucherCode != null && !voucherCode.isEmpty()) {
                voucherService.consumeVoucher(voucherCode);
            }

            String redirectUrl = "redirect:/payment?orderId=" + orderId;
            if (finalAmount != null) {
                redirectUrl += "&finalAmount=" + finalAmount.longValue();
            }
            return redirectUrl;
        } catch (Exception e) {
            return "redirect:/cart?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/thank-you")
    public String thankYou() {
        return "thank-you";
    }

}
package com.example.demo.Controller;

import com.example.demo.entity.CartItem;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping({"","/", "/view"})
    public String viewCart(Model model, Authentication auth) {

        if (auth == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCartItems(auth.getName());

        // Tính tổng tiền của cả giỏ hàng
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("items", cartItems); // Đặt tên là "items"
        model.addAttribute("totalPrice", total); // Gửi tổng tiền sang HTML
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
    public String checkout(org.springframework.security.core.Authentication auth) {
        if (auth == null) return "redirect:/login";

        try {
            cartService.checkout(auth.getName());
            // Thành công thì về trang shop, thêm báo hiệu ?success để hiện thông báo
            return "redirect:/store/products?success=true";
        } catch (Exception e) {
            // Lỗi thì về giỏ hàng kèm tin nhắn lỗi
            return "redirect:/cart?error=" + e.getMessage();
        }
    }
}
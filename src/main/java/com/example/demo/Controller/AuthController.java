package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller                                         //Nó giúp Spring Security biết phải hiển thị file HTML
public class    AuthController {
    @GetMapping("/login")                           //Hiển thị giao diện trang Đăng nhập.
    public String loginPage(){
        return "login";
    }
    @GetMapping("/403")
    public String accessDenied() {
        return "403"; // Trang báo lỗi khi không đủ quyền
    }
}

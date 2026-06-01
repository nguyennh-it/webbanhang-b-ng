package com.example.demo.controller;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        UserResponse user = userService.getUserByUsername(auth.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@RequestParam(required = false) String fullName,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String dob,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }
        try {
            LocalDate parsedDob = null;
            if (dob != null && !dob.isBlank()) {
                parsedDob = LocalDate.parse(dob);
            }
            userService.updateUserProfile(auth.getName(), fullName, phone, address, parsedDob);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công.");
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày sinh không hợp lệ.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        if (auth == null) {
            return "redirect:/login";
        }

        if (newPassword == null || newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 8 ký tự.");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xác nhận mật khẩu không khớp.");
            return "redirect:/profile";
        }

        try {
            userService.changePassword(auth.getName(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công.");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }
}

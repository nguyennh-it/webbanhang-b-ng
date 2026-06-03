package com.example.demo.controller;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            log.warn("🚨 Cảnh báo: Có yêu cầu cập nhật hồ sơ nhưng chưa đăng nhập!");
            return "redirect:/login";
        }

        // 📍 TRẠM TRY (Bắt đầu chạy): Xuất log nhận dữ liệu thô ban đầu
        log.info("👤 User [{}] yêu cầu cập nhật hồ sơ. Data nhận được: fullName={}, phone={}, dob={}",
                auth.getName(), fullName, phone, dob);

        try {
            LocalDate parsedDob = null;
            if (dob != null && !dob.isBlank()) {
                parsedDob = LocalDate.parse(dob); // Nếu lỗi "abc", code đứt ở đây và văng xuống catch
            }
            userService.updateUserProfile(auth.getName(), fullName, phone, address, parsedDob);

            // Nếu chạy đến đây mượt mà, xuất tiếp log thành công
            log.info("✅ User [{}] cập nhật hồ sơ thành công.", auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công.");
        } catch (DateTimeParseException e) {
            // 📍 TRẠM CATCH 1: Xuất log lỗi định dạng ngày sinh
            log.error("❌ User [{}] nhập sai định dạng ngày sinh: {}", auth.getName(), dob);
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày sinh không hợp lệ.");
        } catch (Exception e) {
            // 📍 TRẠM CATCH 2: Xuất log lỗi hệ thống bất ngờ (mất mạng, sập DB...)
            log.error("💥 Lỗi nghiêm trọng khi user [{}] cập nhật hồ sơ: ", auth.getName(), e);
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
            log.warn("🚨 Cảnh báo: Có yêu cầu đổi mật khẩu nhưng chưa đăng nhập!");
            return "redirect:/login";
        }

        // 📍 TRẠM TRY (Bắt đầu chạy): Xuất log nhận yêu cầu (Bảo mật: Không in password ra log)
        log.info("🔐 User [{}] yêu cầu thay đổi mật khẩu mới.", auth.getName());

        if (newPassword == null || newPassword.length() < 8) {
            log.warn("⚠️ Đổi mật khẩu thất bại: Mật khẩu mới của user [{}] ngắn hơn 8 ký tự.", auth.getName());
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 8 ký tự.");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            log.warn("⚠️ Đổi mật khẩu thất bại: User [{}] nhập xác nhận mật khẩu không khớp.", auth.getName());
            redirectAttributes.addFlashAttribute("errorMessage", "Xác nhận mật khẩu không khớp.");
            return "redirect:/profile";
        }

        try {
            userService.changePassword(auth.getName(), currentPassword, newPassword);

            // Nếu đổi thành công trọn vẹn
            log.info("✅ User [{}] đã đổi mật khẩu thành công.", auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công.");
        } catch (AppException e) {
            // 📍 TRẠM CATCH: Xuất log lỗi nghiệp vụ từ Service ném ra (Ví dụ: sai mật khẩu cũ)
            log.error("❌ Lỗi đổi mật khẩu của user [{}]: {}", auth.getName(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }
}
package com.example.demo.controller;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j; // 1. Import Slf4j
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j // 2. Thêm log lên đầu class
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        // 📍 VÙNG HÀNH ĐỘNG: Ghi nhận có yêu cầu tạo user mới (Chỉ log username, KHÔNG log mật khẩu)
        log.info("➕ Đang khởi tạo yêu cầu tạo User mới với username: [{}]", request.getUsername());

        UserResponse response = userService.createUser(request);

        // 📍 VÙNG THÀNH CÔNG: Tạo xong xuôi mượt mà
        log.info("✅ Tạo thành công User có ID: [{}]", response.getId());
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        // ❌ HÀM GET THUẦN TÚY: Không cần đặt log ở đây để tránh rác Terminal khi gọi API lấy danh sách liên tục
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        // ❌ HÀM GET THUẦN TÚY: Không cần đặt log xem chi tiết 1 user
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        // 📍 VÙNG HÀNH ĐỘNG: Ghi nhận yêu cầu thay đổi thông tin
        log.info("🔄 Tiếp nhận yêu cầu cập nhật thông tin cho User ID: [{}]", userId);

        UserResponse response = userService.updateUser(userId, request);

        // 📍 VÙNG THÀNH CÔNG: Sửa thành công
        log.info("✅ Cập nhật thành công thông tin cho User ID: [{}]", userId);
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        // 📍 VÙNG HÀNH ĐỘNG NGUY HIỂM: Log trước khi xóa vĩnh viễn dữ liệu
        log.warn("🚨 CẢNH BÁO: Đang tiến hành xóa User có ID: [{}]", userId);

        userService.deleteUser(userId);

        // 📍 VÙNG THÀNH CÔNG: Xóa xong
        log.info("🗑️ Đã xóa thành công User ID: [{}] khỏi hệ thống.", userId);
        return ApiResponse.<String>builder()
                .result("User has been delete")
                .build();
    }
}
package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        log.info("➕ Bắt đầu tạo user mới với username=[{}], email=[{}]", request.getUsername(), request.getEmail());
        if (userRepository.existsByUsername(request.getUsername())){
            log.warn("⚠️ User đã tồn tại theo username [{}]", request.getUsername());
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())){
            log.warn("⚠️ User đã tồn tại theo email [{}]", request.getEmail());
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        UserResponse saved = userMapper.toUserResponse(userRepository.save(user));
        log.info("✅ Tạo user thành công với ID=[{}]", saved.getId());
        return saved;
    }

    public List<UserResponse> getUsers() {
        log.debug("🔍 Lấy danh sách tất cả user");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> {
                    // 🛠️ SỬA SÓT 1: Bổ sung log.error khi không tìm thấy ID người dùng
                    log.error("❌ Không tìm thấy user với ID: [{}]", id);
                    return new RuntimeException("User not found");
                }));
    }

    public UserResponse getUserByUsername(String username) {
        return userMapper.toUserResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // 🛠️ SỬA SÓT 2: Bổ sung log.error khi tìm kiếm theo username thất bại
                    log.error("❌ Không tìm thấy user với Username: [{}]", username);
                    return new RuntimeException("User not found");
                }));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        log.info("🔄 Bắt đầu cập nhật user ID=[{}]", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("❌ Cập nhật thất bại: User ID [{}] không tồn tại", userId);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        userMapper.updateUser(user, request);

        UserResponse updated = userMapper.toUserResponse(userRepository.save(user));
        log.info("✅ Cập nhật user ID=[{}] thành công", userId);
        return updated;
    }

    public UserResponse updateUserProfile(String username, String fullName, String phone, String address, LocalDate dob) {
        log.info("💾 [Service] Đang tiến hành cập nhật hồ sơ trong DB cho username: [{}]", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ [Service] Cập nhật thất bại: Không tìm thấy username [{}] trong hệ thống", username);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        if (fullName != null) user.setFullName(fullName);
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        if (dob != null) user.setDob(dob);

        User savedUser = userRepository.save(user);

        log.info("✅ [Service] Đã lưu thông tin hồ sơ mới của user [{}] xuống Database thành công.", username);
        // 🛠️ SỬA SÓT 3: Trả về trực tiếp biến savedUser đã map, không gọi hàm userRepository.save(user) lần thứ 2 nữa
        return userMapper.toUserResponse(savedUser);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        log.info("🔐 [Service] Đang xử lý kiểm tra logic đổi mật khẩu cho user: [{}]", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ [Service] Đổi mật khẩu thất bại: Username [{}] không tồn tại", username);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("⚠️ [Service] User [{}] đổi mật khẩu THẤT BẠI: Nhập sai mật khẩu hiện tại!", username);
            throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // 🛠️ SỬA SÓT 4: Bổ sung log báo thành công ở cuối hàm
        log.info("✅ [Service] User [{}] đã đổi mật khẩu thành công trong DB.", username);
    }

    public void deleteUser(String userId){
        log.warn("🗑️ Yêu cầu xóa user ID=[{}]", userId);
        // 🛠️ SỬA SÓT 5: Logic check bị nhầm! Xóa theo ID thì phải check userRepository.existsById(userId) thay vì existsByUsername
        if (!userRepository.existsById(userId)){
            log.error("❌ Xóa thất bại: user ID=[{}] không tồn tại", userId);
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userRepository.deleteById(userId);
        log.info("✅ Đã xóa user ID=[{}] khỏi repository", userId);
    }
}
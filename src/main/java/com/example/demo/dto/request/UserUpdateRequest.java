package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;   // Để trống nếu không muốn đổi mật khẩu
    String fullName;   // Dùng fullName để đồng bộ với Entity
    String phone;      // Quan trọng cho việc giao hàng
    String address;    // Quan trọng cho việc giao hàng
    LocalDate dob;
}

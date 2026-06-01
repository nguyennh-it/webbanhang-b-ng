package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(unique = true, nullable = false)
    String email;
 String username;
    String fullName;
 String firstname;
 String lastname;
 String password;
 LocalDate dob;
    String phone;    // Cần để ship hàng
    String address;
 String role; // "ADMIN" hoặc "USER"
    @Builder.Default
    boolean enabled = true;
    // Thêm thời gian tạo để quản lý User cũ/mới
    java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}

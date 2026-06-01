package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    String id;
    String username;       // khớp với User entity
    String email;
    String fullName;
    String firstname;
    String lastname;
    LocalDate dob;
    String phone;
    String address;
    String role;
}

package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderResponse {
    private String id;
    private String status;
    private Double totalPrice;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private String createdAt;
}

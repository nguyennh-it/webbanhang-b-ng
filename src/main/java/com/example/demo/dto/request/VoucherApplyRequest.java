package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherApplyRequest {
    @NotBlank(message = "Voucher code must not be blank")
    String code;
    @NotNull(message = "số tiền đơn hàng không được trống")
    @Positive(message = "số tiền đơn hàng phải là số lớn hơn 0")
    BigDecimal orderAmount;

}

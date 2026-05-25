package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class VoucherApplyResponse {
    String code;
    String name;
    String discountType;
    BigDecimal discountValue;
    BigDecimal originalAmount;      //tỏng tiền trước giảm giá
    BigDecimal discountAmount;      // số tiền thực tế trừ đi
    BigDecimal finalAmount;             // số tiền cuối cùng
}

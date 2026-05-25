    package com.example.demo.dto.response;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.Builder;
    import lombok.Data;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Data
    @Builder
    public class VoucherSummaryResponse {
        String code;
        String name;
        String discountType;      // PERCENT | FIXED
        BigDecimal discountValue; // Giá trị giảm
        BigDecimal minOrderAmount;  //Điều kiện cần (Ví dụ: Đơn phải từ 200k mới dùng được mã này).
        BigDecimal maxDiscount;     //Mức giảm tối đa (Giúp người dùng biết giới hạn của mã).
        LocalDateTime endDate;    // Hiển thị hạn dùng
    }
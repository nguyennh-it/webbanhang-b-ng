package com.example.demo.service;

import com.example.demo.dto.request.VoucherApplyRequest;
import com.example.demo.dto.response.VoucherApplyResponse;
import com.example.demo.dto.response.VoucherSummaryResponse;
import com.example.demo.entity.Voucher;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    @Transactional                                                          //hiển thị và tính thử tiền
    public VoucherApplyResponse applyVoucher(VoucherApplyRequest request) {
        Voucher voucher = voucherRepository
                .findByCode(request.getCode().toUpperCase().trim())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        validate(voucher, request.getOrderAmount());                //kiểm tra tư cách sử dụng

        BigDecimal discount = calcDiscount(voucher, request.getOrderAmount());              //tính số tiền dcd giảm

        return VoucherApplyResponse.builder()                                   //trả hóa đơn đã tính
                .code(voucher.getCode())
                .name(voucher.getName())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .originalAmount(request.getOrderAmount())
                .discountAmount(discount)
                .finalAmount(request.getOrderAmount().subtract(discount))
                .build();
    }
        
    // Trừ quantity — gọi khi đơn hàng đặt THÀNH CÔNG
    @Transactional
    public void consumeVoucher(String code) {
        Voucher voucher = voucherRepository
                .findByCode(code.toUpperCase().trim())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (voucher.getQuantity() <= 0)                                                     //kiểm tra số lượng trong kho
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_STOCK);

        int updated = voucherRepository.decrementQuantity(voucher.getCode());               //tránh án cùng một lúc
        if (updated == 0)                                                                   //hết thì văng ra
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_STOCK);

        // Tự động INACTIVE nếu hết
        voucherRepository.deactivateIfEmpty(voucher.getCode());                     //đóng của kho ẩn mã nếu hết sạch
    }

    // Lấy danh sách voucher hợp lệ để hiển thị UI
    public List<VoucherSummaryResponse> getActiveVouchers() {
        return voucherRepository
                .findUsableVouchers(LocalDateTime.now())
                .stream()
                .map(this::toSummary)
                .toList();
    }

    // ── Private helpers ──────────────────────────────
    //kiểm tra vorcher
    private void validate(Voucher voucher, BigDecimal orderAmount) {
        if (!"ACTIVE".equals(voucher.getStatus()))                  //kiểm tra trạng thái có đang mở hay không
            throw new AppException(ErrorCode.VOUCHER_INACTIVE);

        LocalDateTime now = LocalDateTime.now();                        //kiểm tra trước thời hạn đc dùng k
        if (now.isBefore(voucher.getStartDate()))
            throw new AppException(ErrorCode.VOUCHER_NOT_STARTED);
        if (now.isAfter(voucher.getEndDate()))                          //kierm tra het haạn chưa
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);

        if (voucher.getQuantity() <= 0)                                 // còn lượt k
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_STOCK);

        if (orderAmount.compareTo(voucher.getMinOrderAmount()) < 0)     //số tiền tối thiểu
            throw new AppException(ErrorCode.VOUCHER_MIN_ORDER);
    }

    private BigDecimal calcDiscount(Voucher voucher, BigDecimal orderAmount) {  //tính tiền giảm giá %
        if ("PERCENT".equals(voucher.getDiscountType())) {
            BigDecimal discount = orderAmount
                    .multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
            return discount.min(voucher.getMaxDiscount());
        }
        // FIXED  tiền
        return voucher.getDiscountValue().min(orderAmount);
    }

    private VoucherSummaryResponse toSummary(Voucher v) {   //đóng gói trả ra
        return VoucherSummaryResponse.builder()
                .code(v.getCode())
                .name(v.getName())
                .discountType(v.getDiscountType())
                .discountValue(v.getDiscountValue())
                .minOrderAmount(v.getMinOrderAmount())
                .maxDiscount(v.getMaxDiscount())
                .endDate(v.getEndDate())
                .build();
    }
}
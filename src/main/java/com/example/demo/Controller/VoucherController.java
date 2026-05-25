package com.example.demo.controller;

import com.example.demo.dto.request.VoucherApplyRequest;
import com.example.demo.dto.response.VoucherApplyResponse;
import com.example.demo.dto.response.VoucherSummaryResponse;
import com.example.demo.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // Validate mã + trả về số tiền giảm — CHƯA trừ quantity
    @PostMapping("/apply")
    public ResponseEntity<VoucherApplyResponse> apply(
            @Valid @RequestBody VoucherApplyRequest request) {
        return ResponseEntity.ok(voucherService.applyVoucher(request));
    }

    // Lấy danh sách voucher đang hiệu lực để hiển thị ngoài UI
    @GetMapping("/active")
    public ResponseEntity<List<VoucherSummaryResponse>> getActive() {
        return ResponseEntity.ok(voucherService.getActiveVouchers());
    }

    // Trừ quantity — gọi sau khi đơn hàng đặt thành công
    @PostMapping("/consume/{code}")
    public ResponseEntity<Void> consume(@PathVariable String code) {
        voucherService.consumeVoucher(code);
        return ResponseEntity.ok().build();
    }
}
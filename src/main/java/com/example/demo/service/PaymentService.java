package com.example.demo.service;

import com.example.demo.Enum.PaymentMethod;
import com.example.demo.Enum.PaymentStatus;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    public Payment createInitialPayment(Order order, PaymentMethod method) {
        Optional<Payment> existingPayment = paymentRepository.findByOrder_Id(order.getId());
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }
        Payment payment = Payment.builder()
                .order(order)
                .amount(BigDecimal.valueOf(order.getTotalPrice())) // Giả định Order của bạn có trường totalPrice
                .paymentMethod(method)
                .status(PaymentStatus.PENDING) // Mặc định ban đầu là chờ thanh toán
                .build();

        return paymentRepository.save(payment);
    }
    public void updatePaymentSuccess(Payment payment,String transactionCode){
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment đã xử lý, trạng thái: " + payment.getStatus());
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionCode(transactionCode);                    // mã gg
        payment.setPaidAt(java.time.LocalDateTime.now()); //lưu thời gian thanh toán

    }

    public void updatePaymentFailed(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment đã xử lý rồi");
        }
        payment.setStatus(PaymentStatus.FAILED);

    }
}

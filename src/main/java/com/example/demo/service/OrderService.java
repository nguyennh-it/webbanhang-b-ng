package com.example.demo.service;

import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderStatusHistoryRepository;
import com.example.demo.repository.ProductSizeRepository; // ← Thêm import này
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.Enum.OrderStatus;
import com.example.demo.entity.OrderStatusHistory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ← Thêm import này

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderStatusHistoryRepository orderStatusHistoryRepository;
    OrderDetailRepository orderDetailRepository;
    ProductSizeRepository productSizeRepository; // ← Inject thêm Repository này vào phục vụ trừ kho

    @Transactional // ← BẮT BUỘC phải có để nếu một sản phẩm hết hàng, toàn bộ quá trình duyệt đơn sẽ roll-back (hủy bỏ), không bị lỗi dữ liệu nửa vời
    public void updateStatus(String orderId, OrderStatus newStatus, String changedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // 🚨 LOGIC TRỪ KHO: Chỉ chạy khi trạng thái CŨ khác COMPLETED và trạng thái MỚI là COMPLETED
        if (order.getStatus() != OrderStatus.COMPLETED && newStatus == OrderStatus.COMPLETED) {

            // Lấy danh sách sản phẩm trong đơn hàng này ra để trừ kho
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail detail : details) {
                String productSizeId = detail.getProductSize().getId();
                int buyQuantity = detail.getQuantity();

                // Gọi hàm decreaseStock tối ưu mà bạn vừa viết ở Repository
                int rowsUpdated = productSizeRepository.decreaseStock(productSizeId, buyQuantity);

                // Nếu kết quả trả về bằng 0 -> Nghĩa là không đủ hàng trong kho để trừ
                if (rowsUpdated == 0) {
                    throw new IllegalStateException("Sản phẩm " + detail.getProductSize().getProduct().getName()
                            + " (Size " + detail.getProductSize().getSize() + ") không đủ số lượng tồn kho!");
                }
            }
        }

        // Cập nhật trạng thái và thời gian của đơn hàng
        order.setStatus(newStatus);
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);

        // Lưu lịch sử thay đổi trạng thái đơn hàng
        OrderStatusHistory history = OrderStatusHistory.builder()
                .id(UUID.randomUUID().toString())
                .order(order)
                .status(newStatus)
                .changedAt(new Timestamp(System.currentTimeMillis()))
                .changedBy(changedBy)
                .build();
        orderStatusHistoryRepository.save(history);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
    }

    public List<OrderDetail> getOrderDetailsByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
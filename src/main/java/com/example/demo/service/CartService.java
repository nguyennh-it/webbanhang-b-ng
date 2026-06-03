package com.example.demo.service;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.repository.*;
import com.example.demo.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. Import Slf4j
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // 2. Kích hoạt Log trên đầu Class
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductSizeRepository productSizeRepository;

    public void addToCart(String productId, String sizeId, String username, int quantity) {
        // 📍 VÙNG HÀNH ĐỘNG: Ghi nhận yêu cầu thêm giỏ hàng ngầm ở Service
        log.info("[CartService] Đang xử lý thêm SP vào giỏ cho user [{}]: ProductID={}, SizeID={}, Qty={}",
                username, productId, sizeId, quantity);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ [CartService] Thêm giỏ hàng thất bại: Không tìm thấy username [{}]", username);
                    return new RuntimeException("Không tìm thấy người dùng");
                });

        Product product = getProductById(productId);

        ProductSize productSize = productSizeRepository.findById(sizeId)
                .orElseThrow(() -> {
                    log.error("❌ [CartService] Thêm giỏ hàng thất bại: SizeID [{}] không tồn tại", sizeId);
                    return new RuntimeException("Size không tồn tại");
                });

        // 📍 VÙNG SAI: Chặn lỗi hết hàng hoàn toàn trong DB
        if (productSize.getStock() <= 0) {
            log.warn("⚠️ [CartService] Chặn thêm vào giỏ: Sản phẩm [{}] - Size [{}] đã HẾT HÀNG.", product.getName(), productSize.getSize());
            throw new RuntimeException("Sản phẩm này hiện đã hết hàng!");
        }

        CartItem existingItem = cartItemRepository.findByUserAndProductAndProductSize(user, product, productSize);

        int currentInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        int totalRequested = currentInCart + quantity;

        // 📍 VÙNG SAI: Chặn lỗi vượt quá tồn kho thực tế
        if (productSize.getStock() < totalRequested) {
            log.warn("⚠️ [CartService] Chặn dồn giỏ: Kho còn {}, Khách đòi dồn thành {} item", productSize.getStock(), totalRequested);
            if (currentInCart > 0) {
                throw new RuntimeException("Kho không đủ hàng! Bạn đã có " + currentInCart
                        + " sản phẩm trong giỏ, kho chỉ còn " + productSize.getStock() + " sản phẩm.");
            } else {
                throw new RuntimeException("Kho không đủ hàng! Bạn chỉ có thể mua tối đa " + productSize.getStock() + " sản phẩm.");
            }
        }

        // VÙNG ĐÚNG: Tiến hành ghi nhận ghi dữ liệu
        if (existingItem != null) {
            existingItem.setQuantity(totalRequested);
            cartItemRepository.save(existingItem);
            log.info("✅ [CartService] Đã cập nhật tăng số lượng dồn lên [{}] cho sản phẩm [{}]", totalRequested, product.getName());
        } else {
            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .productSize(productSize)
                    .quantity(quantity)
                    .cart(cart)
                    .build();

            cartItemRepository.save(newItem);
            log.info("✅ [CartService] Đã thêm mới hoàn toàn item vào giỏ cho user [{}]", username);
        }
    }

    public void removeFromCart(String id) {
        log.info("[CartService] Tiến hành xóa CartItem ID: [{}] khỏi Database", id);
        cartItemRepository.deleteById(id);
    }

    public List<CartItem> getCartItems(String username) {
        // ❌ HÀM ĐỌC DỮ LIỆU: Không đặt log lặt vặt để tránh rác console
        return cartItemRepository.findByUserUsername(username);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ [CartService] Lỗi tìm kiếm: Sản phẩm ID [{}] không tồn tại", id);
                    return new RuntimeException("Sản phẩm không tồn tại");
                });
    }

    public ProductSize getProductSizeById(String id) {
        return productSizeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ [CartService] Lỗi tìm kiếm: Size ID [{}] không tồn tại", id);
                    return new RuntimeException("Size không tồn tại");
                });
    }

    @org.springframework.transaction.annotation.Transactional
    public String checkoutAnonymous(List<CartItem> cartItems, String address) {
        log.info("🚀 [Service] Bắt đầu xử lý chốt đơn (Checkout) cho KHÁCH ẨN DANH");

        if (cartItems == null || cartItems.isEmpty()) {
            log.warn("⚠️ [Service] Checkout thất bại: Giỏ hàng ẩn danh truyền xuống trống rỗng!");
            throw new RuntimeException("Giỏ hàng đang trống, không thể thanh toán!");
        }

        com.example.demo.entity.Order order = new com.example.demo.entity.Order();
        order.setId(java.util.UUID.randomUUID().toString());
        order.setUser(null);
        order.setAddress(address);
        order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        order.setStatus(OrderStatus.PENDING);

        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(total);
        orderRepository.save(order);
        log.info("📝 [Service] Đã tạo đơn hàng chờ [ID: {}] cho Khách ẩn danh. Tổng tiền: {} VND", order.getId(), total);

        for (CartItem item : cartItems) {
            ProductSize size = item.getProductSize();
            // 📍 VÙNG SAI: Check tồn kho một lần nữa trước khi lưu hóa đơn (Đề phòng mua trễ kho hết)
            if (size.getStock() < item.getQuantity()) {
                log.error("❌ [Service] Lỗi chốt hóa đơn: Size [{}] của SP [{}] chỉ còn {} nhưng khách đòi chốt {}",
                        size.getSize(), item.getProduct().getName(), size.getStock(), item.getQuantity());
                throw new RuntimeException("Size " + size.getSize() + " không đủ hàng");
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setProductSize(size);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }

        log.info("✅ [Service] Khách ẩn danh checkout THÀNH CÔNG đơn hàng [{}]", order.getId());
        return order.getId();
    }

    @org.springframework.transaction.annotation.Transactional
    public String checkout(String username) {
        log.info("🚀 [Service] Bắt đầu xử lý chốt đơn (Checkout) cho thành viên: [{}]", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ [Service] Checkout thất bại: Người dùng [{}] không tồn tại", username);
                    return new RuntimeException("Người dùng không tồn tại");
                });

        List<CartItem> cartItems = cartItemRepository.findByUserUsername(username);

        if (cartItems.isEmpty()) {
            log.warn("⚠️ [Service] User [{}] gọi checkout nhưng giỏ hàng thực tế trống rỗng!", username);
            throw new RuntimeException("Giỏ hàng đang trống, không thể thanh toán!");
        }

        com.example.demo.entity.Order order = new com.example.demo.entity.Order();
        order.setId(java.util.UUID.randomUUID().toString());
        order.setUser(user);
        order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        order.setStatus(OrderStatus.PENDING);

        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(total);
        orderRepository.save(order);
        log.info("📝 [Service] Đã lập đơn hàng chờ [ID: {}] cho user [{}]. Tổng tiền: {} VND", order.getId(), username, total);

        for (CartItem item : cartItems) {
            ProductSize size = item.getProductSize();
            if (size.getStock() < item.getQuantity()) {
                log.error("❌ [Service] Lỗi lập hóa đơn cho [{}]: Size [{}] của SP [{}] không đủ hàng để giao.",
                        username, size.getSize(), item.getProduct().getName());
                throw new RuntimeException("Size " + size.getSize() + " không đủ hàng");
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setProductSize(size);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }

        // 🔥 QUAN TRỌNG NHẤT: xóa giỏ hàng
        cartItemRepository.deleteByUserUsername(username);
        log.info("✅ [Service] Đã giải phóng/xóa sạch giỏ hàng rác cũ của user [{}]. Hoàn tất tạo đơn [{}]", username, order.getId());

        return order.getId();
    }
}
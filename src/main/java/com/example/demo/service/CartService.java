package com.example.demo.service;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.repository.*;
import com.example.demo.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//Dùng @Transactional ,Xử lý DB nâng cao (Spring Data JPA), (Java Stream API),Logic Nghiệp vụ (Business Validation)
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductSizeRepository productSizeRepository;

    public void addToCart(
            String productId,
            String sizeId,
            String username,
            int quantity
    ) {

        User user = userRepository.findByUsername(username) //tìm trong giỏ hàng
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng"));

        Product product = getProductById(productId);

        ProductSize productSize = productSizeRepository.findById(sizeId)
                .orElseThrow(() ->
                        new RuntimeException("Size không tồn tại"));

        // 1. CHẶN NGAY: Nếu kho đã hết sạch hàng (bằng 0 hoặc âm)
        if (productSize.getStock() <= 0) {
            throw new RuntimeException("Sản phẩm này hiện đã hết hàng!");
        }

        // 2. Tìm xem sản phẩm cùng size này đã có trong giỏ hàng của khách chưa
        CartItem existingItem =
                cartItemRepository.findByUserAndProductAndProductSize(
                        user,
                        product,
                        productSize
                );

        // Tính toán tổng số lượng mà khách hàng muốn mua dồn
        int currentInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        int totalRequested = currentInCart + quantity;

        // 3. CHẶN TIẾP: Nếu tổng số lượng định mua vượt quá số lượng còn lại trong kho
        if (productSize.getStock() < totalRequested) {
            if (currentInCart > 0) {
                throw new RuntimeException("Kho không đủ hàng! Bạn đã có " + currentInCart
                        + " sản phẩm trong giỏ, kho chỉ còn " + productSize.getStock() + " sản phẩm.");
            } else {
                throw new RuntimeException("Kho không đủ hàng! Bạn chỉ có thể mua tối đa " + productSize.getStock() + " sản phẩm.");
            }
        }

        // 4. Nếu vượt qua các vòng check trên thì tiến hành lưu vào Database
        if (existingItem != null) {

            existingItem.setQuantity(totalRequested);
            cartItemRepository.save(existingItem);

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
        }
    }

    public void removeFromCart(String id) {
        cartItemRepository.deleteById(id);
    }

    public List<CartItem> getCartItems(String username) {
        return cartItemRepository.findByUserUsername(username);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
    }

    public ProductSize getProductSizeById(String id) {
        return productSizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size không tồn tại"));
    }

    @org.springframework.transaction.annotation.Transactional
    public String checkoutAnonymous(List<CartItem> cartItems, String address) {
        if (cartItems == null || cartItems.isEmpty()) {
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

        for (CartItem item : cartItems) {
            ProductSize size = item.getProductSize();
            if (size.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                        "Size " + size.getSize() + " không đủ hàng"
                );
            }
            size.setStock(size.getStock() - item.getQuantity());
            productSizeRepository.save(size);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setProductSize(size);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }

        return order.getId();
    }

    @org.springframework.transaction.annotation.Transactional
    public String checkout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        List<CartItem> cartItems = cartItemRepository.findByUserUsername(username);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống, không thể thanh toán!");
        }
        com.example.demo.entity.Order order = new com.example.demo.entity.Order();
        order.setId(java.util.UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên vì ID là String dam bảo k trung mã
        order.setUser(user);
        order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        order.setStatus(OrderStatus.PENDING);
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(total);
        orderRepository.save(order);
        for (CartItem item : cartItems) {
            ProductSize size = item.getProductSize();
            if (size.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                        "Size " + size.getSize() + " không đủ hàng"
                );
            }
            size.setStock(size.getStock() - item.getQuantity());
            productSizeRepository.save(size);

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
        return order.getId();
    }
}
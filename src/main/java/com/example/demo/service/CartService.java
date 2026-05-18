package com.example.demo.service;

import com.example.demo.UserRepository.*;
import com.example.demo.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
            String username
    ) {

        User user = userRepository.findByUsername(username)             //tìm trong giỏ hàng
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Sản phẩm không tồn tại"));

        ProductSize productSize = productSizeRepository.findById(sizeId)
                .orElseThrow(() ->
                        new RuntimeException("Size không tồn tại"));

        CartItem existingItem =
                cartItemRepository.findByUserAndProductAndProductSize(
                        user,
                        product,
                        productSize
                );

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity() + 1
            );

            cartItemRepository.save(existingItem);

        } else {

            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .productSize(productSize)
                    .quantity(1)
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

    @org.springframework.transaction.annotation.Transactional
    public void checkout(String username) {
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
    }
}
package com.example.demo.service;
import com.example.demo.UserRepository.OrderDetailRepository;
import com.example.demo.UserRepository.ProductSizeRepository;
import com.example.demo.UserRepository.ReviewRepository;
import com.example.demo.entity.ProductSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.demo.UserRepository.ProductRepository;
import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.ProductMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
     ProductSizeRepository productSizeRepository;
     ReviewRepository reviewRepository;
    OrderDetailRepository orderDetailRepository;

    private ProductResponse toResponseWithRating(Product product) {
        ProductResponse response = productMapper.toProductResponse(product);
        response.setAvgRating(reviewRepository.findAverageRatingByProductId(product.getId()));
        response.setReviewCount(reviewRepository.countByProductId(product.getId()));
        response.setSoldCount(orderDetailRepository.countSoldByProductId(product.getId())); // ✅ thêm
        return response;
    }
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        // 1. map product
        Product product = productMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);

        // 2. TẠO SIZE
        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            for (String s : request.getSizes()) {

                ProductSize size = new ProductSize();
                size.setSize(s.trim());
                size.setStock(request.getStock() > 0 ? request.getStock() : 10);
                size.setProduct(savedProduct);

                productSizeRepository.save(size);
            }
        }

        // 3. trả response
        return toResponseWithRating(savedProduct);
    }

    public void deleteProduct(String id) {
        // Xóa ProductSize trước để tránh fail do foreign key
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if (product.getSizes() != null) {
            product.getSizes().forEach(size -> productSizeRepository.delete(size));
        }

        productRepository.deleteById(id);
    }

    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        productMapper.updateProduct(product, request);

        return toResponseWithRating(productRepository.save(product));
    }
    public ProductResponse getProduct(String id) {
        return productRepository.findById(id)
                .map(this::toResponseWithRating)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
    }
    // Sửa lại để hỗ trợ phân trang và tìm kiếm gộp làm một
    public Page<ProductResponse> getProducts(int page, int size, String keyword, Long categoryId) {
        // 1. Tạo đối tượng Pageable (trang bắt đầu từ 0)
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage;

        // 2. Logic: Nếu có từ khóa thì tìm theo tên + phân trang, không thì lấy hết + phân trang
        if (categoryId != null) {
            if (keyword != null && !keyword.isEmpty()) {
                productPage = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, keyword, pageable);
            } else {
                productPage = productRepository.findByCategoryId(categoryId, pageable);
            }
        } else {
            if (keyword != null && !keyword.isEmpty()) {
                productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }
        }

        // 3. Chuyển đổi từ Page<Entity> sang Page<ResponseDTO> bằng MapStruct
        return productPage.map(this::toResponseWithRating);
    }
}
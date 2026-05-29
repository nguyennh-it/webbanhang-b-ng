package com.example.demo.service;

import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.ProductSizeRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.entity.ProductSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.demo.repository.ProductRepository;
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
        response.setSoldCount(orderDetailRepository.countSoldByProductId(product.getId()));
        return response;
    }

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        Product product = productMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);

        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            for (String s : request.getSizes()) {
                ProductSize size = new ProductSize();
                size.setSize(s.trim());
                size.setStock(request.getStock() > 0 ? request.getStock() : 10);
                size.setProduct(savedProduct);
                productSizeRepository.save(size);
            }
        }
        return toResponseWithRating(savedProduct);
    }

    public void deleteProduct(String id) {
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

    // 🚀 HÀM TỔNG HỢP DUY NHẤT: Hỗ trợ phân trang, tìm kiếm, lọc Category và lọc Brand gộp làm một!
    public Page<ProductResponse> getProducts(int page, int size, String keyword, Long categoryId, Long brandId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        // 2. Kiểm tra điều kiện ưu tiên lọc
        if (brandId != null) {
            // ƯU TIÊN 1: Lọc theo Brand trước nếu người dùng click chọn thương hiệu
            productPage = productRepository.findByBrandId(brandId, pageable);
        } else if (categoryId != null) {
            // ƯU TIÊN 2: Lọc theo Danh mục
            if (keyword != null && !keyword.isEmpty()) {
                productPage = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, keyword, pageable);
            } else {
                productPage = productRepository.findByCategoryId(categoryId, pageable);
            }
        } else {
            // MẶC ĐỊNH: Tìm kiếm theo từ khóa hoặc lấy tất cả
            if (keyword != null && !keyword.isEmpty()) {
                productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }
        }

        return productPage.map(this::toResponseWithRating);
    }
}
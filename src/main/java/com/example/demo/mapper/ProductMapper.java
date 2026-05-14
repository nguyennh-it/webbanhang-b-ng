package com.example.demo.mapper;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductSizeResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sizes", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "productVariantId", ignore = true)
    Product toProduct(ProductRequest request);

    ProductResponse toProductResponse(Product product);

    List<ProductSizeResponse> toSizeResponseList(List<ProductSize> sizes);

    ProductSizeResponse toSizeResponse(ProductSize size);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sizes", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "productVariantId", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductRequest request);
}
package com.example.demo.service;

import com.example.demo.entity.Brand;
import com.example.demo.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    final BrandRepository brandRepository;

    // Lấy tất cả brand
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    // Lấy brand theo id
    public Brand getById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy brand"));
    }

    // Thêm brand mới
    public Brand create(Brand brand) {
        return brandRepository.save(brand);
    }

    // Sửa brand
    public Brand update(Long id, Brand newData) {
        Brand brand = getById(id);
        brand.setName(newData.getName());
        brand.setLogo(newData.getLogo());
        brand.setDescription(newData.getDescription());
        brand.setCountry(newData.getCountry());
        brand.setStatus(newData.getStatus());
        return brandRepository.save(brand);
    }

    // Xóa brand
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }
}
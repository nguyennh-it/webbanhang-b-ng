package com.example.demo.repository;

import com.example.demo.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    // Lấy danh sách banner đang hoạt động và sắp xếp theo thứ tự hiển thị tăng dần
    List<Banner> findByStatusOrderBySortOrderAsc(String status);
}
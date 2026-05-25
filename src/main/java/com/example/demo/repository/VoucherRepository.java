package com.example.demo.repository;

import com.example.demo.entity.Voucher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

     Optional<Voucher> findByCode(String code);             // trả về 1 đối tượng có the ton tai hoac k

     @Lock(LockModeType.PESSIMISTIC_WRITE)                            // giải quyết xong r mới cho chạy
     @Query("SELECT v FROM Voucher v WHERE v.code = :code")           //Xác nhận mã code
     Optional<Voucher> findByCodeForUpdate(@Param("code") String code);    //xuống data tìm va đóng gói

     @Query("SELECT v FROM Voucher v " +
             "WHERE v.status = 'ACTIVE' " +                                // chỉ lấy mã đang hoạt động
             "AND v.startDate <= :now " +                                   // kiểm tra xem còn hiệu lực k
             "AND v.endDate >= :now " +
             "AND v.quantity > 0 " +                                  //chỉ lấy những mã còn lượt dùng
             "ORDER BY v.endDate ASC")                                        // ưu tiên những mã sắp hết hạn
     List<Voucher> findUsableVouchers(@Param("now") LocalDateTime now);    // tìm mã sửu ddungjconf hạn

     @Modifying                                   //: Thông báo cho Spring đây là lệnh thay đổi dữ liệu (UPDATE/DELETE
     @Query("UPDATE Voucher v " +
             "SET v.quantity = v.quantity - 1 " +           //tru truc tiep trong data
             "WHERE v.code = :code AND v.quantity > 0")     // lấy những mã vẫn còn
     int decrementQuantity(@Param("code") String code);     //kích hoạt

     @Modifying
     @Query("UPDATE Voucher v " +                           // trạng thái đã dùng hết
             "SET v.status = 'INACTIVE' " +
             "WHERE v.code = :code AND v.quantity = 0")
     void deactivateIfEmpty(@Param("code") String code);
}
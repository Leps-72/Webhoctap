package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.DangKyKhoaHoc;
import com.huynh.Webhoctap.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// [TẠO MỚI] Repository này hoàn toàn thiếu trong bộ repository gốc
public interface DangKyKhoaHocRepository extends JpaRepository<DangKyKhoaHoc, Integer> {

    // DangKyKhoaHocService.huyDangKy() và daDangKy() dùng method này
    Optional<DangKyKhoaHoc> findByKhoaHoc_MaKhoaHocAndHocSinh(Integer maKhoaHoc, NguoiDung hocSinh);

    // DangKyKhoaHocService.daDangKy() kiểm tra tồn tại
    boolean existsByKhoaHoc_MaKhoaHocAndHocSinh(Integer maKhoaHoc, NguoiDung hocSinh);

    // DangKyKhoaHocService.layKhoaHocCuaHocSinh() — Student xem khóa đã đăng ký
    List<DangKyKhoaHoc> findByHocSinh(NguoiDung hocSinh);

    // DangKyKhoaHocService.layHocSinhCuaKhoaHoc() — Teacher/Admin xem danh sách học sinh
    List<DangKyKhoaHoc> findByKhoaHoc_MaKhoaHoc(Integer maKhoaHoc);

    // Lọc theo trạng thái (DangHoc / HoanThanh)
    List<DangKyKhoaHoc> findByHocSinhAndTrangThai(NguoiDung hocSinh, String trangThai);
}
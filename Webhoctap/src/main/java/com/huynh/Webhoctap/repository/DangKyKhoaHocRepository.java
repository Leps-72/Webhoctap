package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.DangKyKhoaHoc;
import com.huynh.Webhoctap.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DangKyKhoaHocRepository extends JpaRepository<DangKyKhoaHoc, Integer> {

    Optional<DangKyKhoaHoc> findByKhoaHoc_MaKhoaHocAndHocSinh(Integer maKhoaHoc, NguoiDung hocSinh);
    boolean existsByKhoaHoc_MaKhoaHocAndHocSinh(Integer maKhoaHoc, NguoiDung hocSinh);
    List<DangKyKhoaHoc> findByHocSinh(NguoiDung hocSinh);
    List<DangKyKhoaHoc> findByKhoaHoc_MaKhoaHoc(Integer maKhoaHoc);
    List<DangKyKhoaHoc> findByHocSinhAndTrangThai(NguoiDung hocSinh, String trangThai);
    List<DangKyKhoaHoc> findTop5ByOrderByNgayDangKyDesc();
}
package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.KhoaHoc;
import com.huynh.Webhoctap.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Integer> {

    List<KhoaHoc> findByTrangThaiDuyet(String trangThaiDuyet);
    List<KhoaHoc> findByTrangThaiDuyetAndIsActive(String trangThaiDuyet, Boolean isActive);
    boolean existsBySlug(String slug);

    List<KhoaHoc> findByGiaoVien_MaNguoiDung(Integer maGiaoVien);
    List<KhoaHoc> findByGiaoVien(NguoiDung giaoVien);
    List<KhoaHoc> findTop5ByOrderByNgayTaoDesc();
}
package com.huynh.Webhoctap.repository;

import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    List<Quiz> findByGiaoVien_MaNguoiDung(Integer maGiaoVien);
    List<Quiz> findByKhoaHoc_MaKhoaHoc(Integer maKhoaHoc);
    List<Quiz> findByTrangThai(String trangThai);
    List<Quiz> findByKhoaHoc_MaKhoaHocAndTrangThai(Integer maKhoaHoc, String trangThai);
    List<Quiz> findByGiaoVien(NguoiDung giaoVien);
}
package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.model.DangKyKhoaHoc;
import com.huynh.Webhoctap.model.KhoaHoc;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.repository.DangKyKhoaHocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DangKyKhoaHocService {

    private final DangKyKhoaHocRepository dangKyRepository;
    private final KhoaHocService khoaHocService;

    // ── Học sinh đăng ký khóa học ─────────────────────────────────────────
    @Transactional
    public DangKyKhoaHoc dangKy(Integer maKhoaHoc, NguoiDung hocSinh) {
        KhoaHoc khoaHoc = khoaHocService.layTheoId(maKhoaHoc);

        if (!"Approved".equals(khoaHoc.getTrangThaiDuyet()) || !khoaHoc.getIsActive()) {
            throw new RuntimeException("Khóa học chưa được mở đăng ký.");
        }
        if (daDangKy(maKhoaHoc, hocSinh)) {
            throw new RuntimeException("Bạn đã đăng ký khóa học này rồi.");
        }

        DangKyKhoaHoc dangKy = new DangKyKhoaHoc();
        dangKy.setKhoaHoc(khoaHoc);
        dangKy.setHocSinh(hocSinh);
        dangKy.setNgayDangKy(LocalDateTime.now());
        dangKy.setTrangThai("DangHoc");
        return dangKyRepository.save(dangKy);
    }

    // ── Học sinh huỷ đăng ký ──────────────────────────────────────────────
    @Transactional
    public void huyDangKy(Integer maKhoaHoc, NguoiDung hocSinh) {
        DangKyKhoaHoc dangKy = dangKyRepository
                .findByKhoaHoc_MaKhoaHocAndHocSinh(maKhoaHoc, hocSinh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký này."));
        dangKyRepository.delete(dangKy);
    }

    // ── Đánh dấu hoàn thành ───────────────────────────────────────────────
    @Transactional
    public void danhDauHoanThanh(Integer maKhoaHoc, NguoiDung hocSinh) {
        DangKyKhoaHoc dangKy = dangKyRepository
                .findByKhoaHoc_MaKhoaHocAndHocSinh(maKhoaHoc, hocSinh)
                .orElseThrow(() -> new RuntimeException("Bạn chưa đăng ký khóa học này."));
        dangKy.setTrangThai("HoanThanh");
        dangKyRepository.save(dangKy);
    }

    // ── Kiểm tra đã đăng ký chưa ──────────────────────────────────────────
    public boolean daDangKy(Integer maKhoaHoc, NguoiDung hocSinh) {
        return dangKyRepository.existsByKhoaHoc_MaKhoaHocAndHocSinh(maKhoaHoc, hocSinh);
    }

    // ── Danh sách khóa học học sinh đã đăng ký ────────────────────────────
    public List<DangKyKhoaHoc> layKhoaHocCuaHocSinh(NguoiDung hocSinh) {
        return dangKyRepository.findByHocSinh(hocSinh);
    }

    // ── Danh sách học sinh trong khóa học (Teacher/Admin) ─────────────────
    public List<DangKyKhoaHoc> layHocSinhCuaKhoaHoc(Integer maKhoaHoc) {
        return dangKyRepository.findByKhoaHoc_MaKhoaHoc(maKhoaHoc);
    }
}
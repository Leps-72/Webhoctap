package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.model.KhoaHoc;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.repository.KhoaHocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KhoaHocService {

    private final KhoaHocRepository khoaHocRepository;

    // ── Teacher tạo khóa học mới (trạng thái Draft) ───────────────────────
    @Transactional
    public KhoaHoc taoKhoaHoc(KhoaHoc khoaHoc, NguoiDung giaoVien) {
        khoaHoc.setGiaoVien(giaoVien);
        khoaHoc.setTrangThaiDuyet("Draft");
        khoaHoc.setNgayTao(LocalDateTime.now());
        khoaHoc.setIsActive(true);
        return khoaHocRepository.save(khoaHoc);
    }

    // ── Teacher gửi yêu cầu duyệt ─────────────────────────────────────────
    @Transactional
    public KhoaHoc guiYeuCauDuyet(Integer maKhoaHoc, NguoiDung giaoVien) {
        KhoaHoc khoaHoc = layTheoId(maKhoaHoc);
        kiemTraChuSoHuu(khoaHoc, giaoVien);
        khoaHoc.setTrangThaiDuyet("PendingApproval");
        khoaHoc.setNgayGuiDuyet(LocalDateTime.now());
        return khoaHocRepository.save(khoaHoc);
    }

    // ── Admin duyệt khóa học ──────────────────────────────────────────────
    @Transactional
    public KhoaHoc duyetKhoaHoc(Integer maKhoaHoc, NguoiDung admin) {
        KhoaHoc khoaHoc = layTheoId(maKhoaHoc);
        khoaHoc.setTrangThaiDuyet("Approved");
        khoaHoc.setNgayDuyet(LocalDateTime.now());
        khoaHoc.setNguoiDuyet(admin);
        khoaHoc.setLyDoTuChoi(null);
        return khoaHocRepository.save(khoaHoc);
    }

    // ── Admin từ chối khóa học ────────────────────────────────────────────
    @Transactional
    public KhoaHoc tuChoiKhoaHoc(Integer maKhoaHoc, NguoiDung admin, String lyDo) {
        KhoaHoc khoaHoc = layTheoId(maKhoaHoc);
        khoaHoc.setTrangThaiDuyet("Rejected");
        khoaHoc.setNgayDuyet(LocalDateTime.now());
        khoaHoc.setNguoiDuyet(admin);
        khoaHoc.setLyDoTuChoi(lyDo);
        return khoaHocRepository.save(khoaHoc);
    }

    // ── Teacher cập nhật khóa học (chỉ được khi còn Draft/Rejected) ───────
    @Transactional
    public KhoaHoc capNhat(Integer maKhoaHoc, KhoaHoc thongTinMoi, NguoiDung giaoVien) {
        KhoaHoc khoaHoc = layTheoId(maKhoaHoc);
        kiemTraChuSoHuu(khoaHoc, giaoVien);
        if (!List.of("Draft", "Rejected").contains(khoaHoc.getTrangThaiDuyet())) {
            throw new RuntimeException("Chỉ có thể chỉnh sửa khóa học ở trạng thái Draft hoặc Rejected.");
        }
        khoaHoc.setTenKhoaHoc(thongTinMoi.getTenKhoaHoc());
        khoaHoc.setMoTa(thongTinMoi.getMoTa());
        khoaHoc.setAnhBia(thongTinMoi.getAnhBia());
        khoaHoc.setSlug(thongTinMoi.getSlug());
        return khoaHocRepository.save(khoaHoc);
    }

    // ── Ẩn / hiện khóa học ────────────────────────────────────────────────
    @Transactional
    public void doiTrangThaiHienThi(Integer maKhoaHoc, NguoiDung giaoVien, boolean isActive) {
        KhoaHoc khoaHoc = layTheoId(maKhoaHoc);
        kiemTraChuSoHuu(khoaHoc, giaoVien);
        khoaHoc.setIsActive(isActive);
        khoaHocRepository.save(khoaHoc);
    }

    // ── Truy vấn ──────────────────────────────────────────────────────────
    public KhoaHoc layTheoId(Integer id) {
        return khoaHocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học ID: " + id));
    }

    public List<KhoaHoc> layTatCaChoAdmin() {
        return khoaHocRepository.findAll();
    }

    public List<KhoaHoc> layChooDuyet() {
        return khoaHocRepository.findByTrangThaiDuyet("PendingApproval");
    }

    public List<KhoaHoc> layCuaGiaoVien(NguoiDung giaoVien) {
        return khoaHocRepository.findByGiaoVien(giaoVien);
    }

    public List<KhoaHoc> layDaDuyetVaHienThi() {
        return khoaHocRepository.findByTrangThaiDuyetAndIsActive("Approved", true);
    }

    // ── Kiểm tra quyền sở hữu ─────────────────────────────────────────────
    private void kiemTraChuSoHuu(KhoaHoc khoaHoc, NguoiDung giaoVien) {
        if (!khoaHoc.getGiaoVien().getMaNguoiDung().equals(giaoVien.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền thao tác với khóa học này.");
        }
    }
}
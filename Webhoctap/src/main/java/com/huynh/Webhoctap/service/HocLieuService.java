package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.model.HocLieu;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.repository.HocLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HocLieuService {

    private final HocLieuRepository hocLieuRepository;

    @Value("${upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    // ── Upload file và lưu học liệu ───────────────────────────────────────
    @Transactional
    public HocLieu uploadHocLieu(HocLieu hocLieu, MultipartFile file, NguoiDung nguoiDung) throws IOException {
        if (file != null && !file.isEmpty()) {
            String tenFile = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path duongDan = Paths.get(uploadDir).resolve(tenFile);
            Files.createDirectories(duongDan.getParent());
            Files.copy(file.getInputStream(), duongDan, StandardCopyOption.REPLACE_EXISTING);

            hocLieu.setDuongDanTep("/uploads/" + tenFile);
            hocLieu.setLoaiTep(file.getContentType());
            hocLieu.setKichThuocTep(file.getSize());
        }
        hocLieu.setNguoiDung(nguoiDung);
        hocLieu.setNgayDang(LocalDateTime.now());
        hocLieu.setDaDuyet(false);
        hocLieu.setTrangThaiDuyet("Chờ duyệt");
        hocLieu.setLuotXem(0);
        hocLieu.setSoLuotTai(0);
        return hocLieuRepository.save(hocLieu);
    }

    // ── Admin duyệt học liệu ──────────────────────────────────────────────
    @Transactional
    public HocLieu duyetHocLieu(Integer maHocLieu) {
        HocLieu hocLieu = layTheoId(maHocLieu);
        hocLieu.setDaDuyet(true);
        hocLieu.setTrangThaiDuyet("Đã duyệt");
        return hocLieuRepository.save(hocLieu);
    }

    // ── Admin từ chối học liệu ────────────────────────────────────────────
    @Transactional
    public HocLieu tuChoiHocLieu(Integer maHocLieu) {
        HocLieu hocLieu = layTheoId(maHocLieu);
        hocLieu.setDaDuyet(false);
        hocLieu.setTrangThaiDuyet("Từ chối");
        return hocLieuRepository.save(hocLieu);
    }

    // ── Tăng lượt xem — dùng @Query UPDATE trực tiếp, tránh race condition ─
    @Transactional
    public void tangLuotXem(Integer maHocLieu) {
        hocLieuRepository.tangLuotXem(maHocLieu);
    }

    // ── Tăng lượt tải — dùng @Query UPDATE trực tiếp, tránh race condition ─
    @Transactional
    public void tangLuotTai(Integer maHocLieu) {
        hocLieuRepository.tangSoLuotTai(maHocLieu);
    }

    // ── Xóa học liệu (và file vật lý) ────────────────────────────────────
    @Transactional
    public void xoaHocLieu(Integer maHocLieu, NguoiDung nguoiDung) throws IOException {
        HocLieu hocLieu = layTheoId(maHocLieu);
        if (!hocLieu.getNguoiDung().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền xóa học liệu này.");
        }
        if (hocLieu.getDuongDanTep() != null) {
            Path filePath = Paths.get("src/main/resources/static" + hocLieu.getDuongDanTep());
            Files.deleteIfExists(filePath);
        }
        hocLieuRepository.delete(hocLieu);
    }

    // ── Truy vấn ──────────────────────────────────────────────────────────
    public HocLieu layTheoId(Integer id) {
        return hocLieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học liệu ID: " + id));
    }

    public List<HocLieu> layDaDuyet() {
        return hocLieuRepository.findByDaDuyet(true);
    }

    public List<HocLieu> layChoDuyet() {
        return hocLieuRepository.findByTrangThaiDuyet("Chờ duyệt");
    }

    public List<HocLieu> layCuaNguoiDung(NguoiDung nguoiDung) {
        return hocLieuRepository.findByNguoiDung(nguoiDung);
    }

    public List<HocLieu> layTatCaChoAdmin() {
        return hocLieuRepository.findAll();
    }
}
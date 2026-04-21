package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.VaiTro;
import com.huynh.Webhoctap.repository.NguoiDungRepository;
import com.huynh.Webhoctap.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Đăng ký tài khoản mới (mặc định vai trò Student) ──────────────────
    @Transactional
    public NguoiDung dangKy(NguoiDung nguoiDung, String tenVaiTro) {
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng: " + nguoiDung.getEmail());
        }
        VaiTro vaiTro = vaiTroRepository.findByTenVaiTro(tenVaiTro)
                .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại: " + tenVaiTro));

        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        nguoiDung.setVaiTro(vaiTro);
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDung.setTrangThai(true);
        return nguoiDungRepository.save(nguoiDung);
    }

    // ── Lấy danh sách tất cả người dùng (Admin) ───────────────────────────
    public List<NguoiDung> layTatCa() {
        return nguoiDungRepository.findAll();
    }

    // ── Lấy người dùng theo ID ────────────────────────────────────────────
    public NguoiDung layTheoId(Integer id) {
        return nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));
    }

    // ── Lấy người dùng theo email ─────────────────────────────────────────
    public Optional<NguoiDung> layTheoEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }

    // ── Cập nhật thông tin cá nhân ────────────────────────────────────────
    @Transactional
    public NguoiDung capNhatThongTin(Integer id, NguoiDung thongTinMoi) {
        NguoiDung nguoiDung = layTheoId(id);
        nguoiDung.setHoTen(thongTinMoi.getHoTen());
        nguoiDung.setSoDienThoai(thongTinMoi.getSoDienThoai());
        nguoiDung.setDiaChi(thongTinMoi.getDiaChi());
        nguoiDung.setNgaySinh(thongTinMoi.getNgaySinh());
        nguoiDung.setGioiTinh(thongTinMoi.getGioiTinh());
        nguoiDung.setAnhDaiDien(thongTinMoi.getAnhDaiDien());
        return nguoiDungRepository.save(nguoiDung);
    }

    // ── Khoá / mở khoá tài khoản (Admin) ─────────────────────────────────
    @Transactional
    public void doiTrangThai(Integer id, boolean trangThai) {
        NguoiDung nguoiDung = layTheoId(id);
        nguoiDung.setTrangThai(trangThai);
        nguoiDungRepository.save(nguoiDung);
    }

    // ── Đổi vai trò (Admin) ───────────────────────────────────────────────
    @Transactional
    public void doiVaiTro(Integer id, String tenVaiTro) {
        NguoiDung nguoiDung = layTheoId(id);
        VaiTro vaiTro = vaiTroRepository.findByTenVaiTro(tenVaiTro)
                .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại: " + tenVaiTro));
        nguoiDung.setVaiTro(vaiTro);
        nguoiDungRepository.save(nguoiDung);
    }

    // ── Ghi nhận lần đăng nhập cuối ───────────────────────────────────────
    @Transactional
    public void capNhatLanDangNhapCuoi(String email) {
        nguoiDungRepository.findByEmail(email).ifPresent(nd -> {
            nd.setLanDangNhapCuoi(LocalDateTime.now());
            nguoiDungRepository.save(nd);
        });
    }

    // ── Lọc theo vai trò ─────────────────────────────────────────────────
    public List<NguoiDung> layTheoVaiTro(String tenVaiTro) {
        return nguoiDungRepository.findByVaiTro_TenVaiTro(tenVaiTro);
    }

    // ── Tìm kiếm / lọc linh hoạt (Admin) ────────────────────────────────
    public List<NguoiDung> timKiem(String keyword, String tenVaiTro, Boolean trangThai) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String role = (tenVaiTro != null && !tenVaiTro.isBlank()) ? tenVaiTro : null;
        return nguoiDungRepository.timKiem(kw, role, trangThai);
    }

    // ── Thêm người dùng mới (Admin) ───────────────────────────────────────
    @Transactional
    public NguoiDung themNguoiDung(NguoiDung nguoiDung, String tenVaiTro) {
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng: " + nguoiDung.getEmail());
        }
        VaiTro vaiTro = vaiTroRepository.findByTenVaiTro(tenVaiTro)
                .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại: " + tenVaiTro));
        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        nguoiDung.setVaiTro(vaiTro);
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDung.setTrangThai(true);
        return nguoiDungRepository.save(nguoiDung);
    }

    // ── Xóa người dùng (Admin) ────────────────────────────────────────────
    @Transactional
    public void xoaNguoiDung(Integer id) {
        NguoiDung nguoiDung = layTheoId(id);
        nguoiDungRepository.delete(nguoiDung);
    }
}
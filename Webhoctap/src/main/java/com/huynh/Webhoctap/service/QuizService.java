package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.model.CauHoi;
import com.huynh.Webhoctap.model.LuaChon;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.Quiz;
import com.huynh.Webhoctap.repository.CauHoiRepository;
import com.huynh.Webhoctap.repository.LuaChonRepository;
import com.huynh.Webhoctap.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CauHoiRepository cauHoiRepository;
    private final LuaChonRepository luaChonRepository;

    // ── Teacher tạo quiz mới (Draft) ──────────────────────────────────────
    @Transactional
    public Quiz taoQuiz(Quiz quiz, NguoiDung giaoVien) {
        quiz.setGiaoVien(giaoVien);
        quiz.setTrangThai("Draft");
        return quizRepository.save(quiz);
    }

    // ── Thêm câu hỏi vào quiz ─────────────────────────────────────────────
    @Transactional
    public CauHoi themCauHoi(Integer maQuiz, CauHoi cauHoi, NguoiDung giaoVien) {
        Quiz quiz = layTheoId(maQuiz);
        kiemTraChuSoHuuQuiz(quiz, giaoVien);
        cauHoi.setQuiz(quiz);
        cauHoi.setNguonGoc("Quiz");
        return cauHoiRepository.save(cauHoi);
    }

    // ── Sửa câu hỏi trong quiz ────────────────────────────────────────────
    @Transactional
    public CauHoi capNhatCauHoi(Integer maCauHoi, CauHoi thongTinMoi, NguoiDung giaoVien) {
        CauHoi cauHoi = cauHoiRepository.findById(maCauHoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + maCauHoi));
        if (cauHoi.getQuiz() != null) {
            kiemTraChuSoHuuQuiz(cauHoi.getQuiz(), giaoVien);
        } else if (cauHoi.getGiaoVienNganHang() != null) {
            kiemTraChuSoHuuNganHang(cauHoi, giaoVien);
        }
        cauHoi.setNoiDung(thongTinMoi.getNoiDung());
        cauHoi.setLoaiCauHoi(thongTinMoi.getLoaiCauHoi());
        cauHoi.setDiem(thongTinMoi.getDiem());
        cauHoi.setChuDe(thongTinMoi.getChuDe());
        cauHoi.setDoKho(thongTinMoi.getDoKho());
        return cauHoiRepository.save(cauHoi);
    }

    // ── Thêm lựa chọn vào câu hỏi ────────────────────────────────────────
    @Transactional
    public LuaChon themLuaChon(Integer maCauHoi, LuaChon luaChon) {
        CauHoi cauHoi = cauHoiRepository.findById(maCauHoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + maCauHoi));
        luaChon.setCauHoi(cauHoi);
        return luaChonRepository.save(luaChon);
    }

    @Transactional
    public void capNhatLuaChons(Integer maCauHoi, List<LuaChon> danhSachMoi) {
        CauHoi cauHoi = cauHoiRepository.findById(maCauHoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + maCauHoi));
        
        // Cập nhật thông qua JPA collection management
        cauHoi.getLuaChons().clear();
        for (LuaChon lc : danhSachMoi) {
            lc.setCauHoi(cauHoi);
            cauHoi.getLuaChons().add(lc);
        }
        cauHoiRepository.save(cauHoi);
    }

    // ── Teacher publish quiz ───────────────────────────────────────────────
    @Transactional
    public Quiz publishQuiz(Integer maQuiz, NguoiDung giaoVien) {
        Quiz quiz = layTheoId(maQuiz);
        kiemTraChuSoHuuQuiz(quiz, giaoVien);
        List<CauHoi> danhSachCauHoi = cauHoiRepository.findByQuiz(quiz);
        if (danhSachCauHoi.isEmpty()) {
            throw new RuntimeException("Quiz phải có ít nhất 1 câu hỏi trước khi publish.");
        }
        quiz.setTrangThai("Published");
        return quizRepository.save(quiz);
    }

    // ── Chuyển quiz về Draft ──────────────────────────────────────────────
    @Transactional
    public Quiz unPublishQuiz(Integer maQuiz, NguoiDung giaoVien) {
        Quiz quiz = layTheoId(maQuiz);
        kiemTraChuSoHuuQuiz(quiz, giaoVien);
        quiz.setTrangThai("Draft");
        return quizRepository.save(quiz);
    }

    @Transactional
    public void xoaQuiz(Integer maQuiz, NguoiDung giaoVien) {
        Quiz quiz = layTheoId(maQuiz);
        kiemTraChuSoHuuQuiz(quiz, giaoVien);
        quizRepository.delete(quiz);
    }

    // ── Xóa câu hỏi (trong quiz hoặc ngân hàng) ──────────────────────────
    @Transactional
    public void xoaCauHoi(Integer maCauHoi, NguoiDung giaoVien) {
        CauHoi cauHoi = cauHoiRepository.findById(maCauHoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + maCauHoi));
        if (cauHoi.getQuiz() != null) {
            kiemTraChuSoHuuQuiz(cauHoi.getQuiz(), giaoVien);
        } else {
            kiemTraChuSoHuuNganHang(cauHoi, giaoVien);
        }
        cauHoiRepository.delete(cauHoi);
    }

    // ══════════════════════════════════════════════════════════════════════
    // NGÂN HÀNG CÂU HỎI
    // ══════════════════════════════════════════════════════════════════════

    // ── Tạo câu hỏi trong ngân hàng ──────────────────────────────────────
    @Transactional
    public CauHoi taoCauHoiNganHang(CauHoi cauHoi, NguoiDung giaoVien) {
        cauHoi.setQuiz(null);
        cauHoi.setNguonGoc("NganHang");
        cauHoi.setGiaoVienNganHang(giaoVien);
        return cauHoiRepository.save(cauHoi);
    }

    // ── Lấy toàn bộ ngân hàng câu hỏi của teacher ────────────────────────
    public List<CauHoi> layNganHangCuaGiaoVien(NguoiDung giaoVien) {
        return cauHoiRepository.findTatCaCauHoiCuaGiaoVien(giaoVien);
    }

    public List<CauHoi> layNganHangVoiFilter(NguoiDung giaoVien, String chuDe, String doKho) {
        boolean hasChuDe = chuDe != null && !chuDe.isBlank();
        boolean hasDoKho = doKho != null && !doKho.isBlank();

        if (hasChuDe && hasDoKho) {
            return cauHoiRepository.findCauHoiCuaGiaoVienTheoChuDeAndDoKho(
                    giaoVien, chuDe, doKho);
        } else if (hasChuDe) {
            return cauHoiRepository.findCauHoiCuaGiaoVienTheoChuDe(
                    giaoVien, chuDe);
        } else if (hasDoKho) {
            return cauHoiRepository.findCauHoiCuaGiaoVienTheoDoKho(
                    giaoVien, doKho);
        }
        return layNganHangCuaGiaoVien(giaoVien);
    }

    // ── Import câu hỏi từ ngân hàng vào quiz ─────────────────────────────
    @Transactional
    public int importCauHoiVaoQuiz(Integer maQuiz, List<Integer> maCauHoiIds, NguoiDung giaoVien) {
        Quiz quiz = layTheoId(maQuiz);
        kiemTraChuSoHuuQuiz(quiz, giaoVien);
        int count = 0;
        for (Integer maCauHoi : maCauHoiIds) {
            CauHoi nguon = cauHoiRepository.findById(maCauHoi)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + maCauHoi));
            // Tạo bản sao câu hỏi vào quiz (không sửa câu hỏi gốc ngân hàng)
            CauHoi banSao = new CauHoi();
            banSao.setNoiDung(nguon.getNoiDung());
            banSao.setLoaiCauHoi(nguon.getLoaiCauHoi());
            banSao.setDiem(nguon.getDiem());
            banSao.setChuDe(nguon.getChuDe());
            banSao.setDoKho(nguon.getDoKho());
            banSao.setQuiz(quiz);
            banSao.setNguonGoc("Quiz");
            CauHoi saved = cauHoiRepository.save(banSao);
            // Copy lựa chọn
            if (nguon.getLuaChons() != null) {
                for (var lc : nguon.getLuaChons()) {
                    LuaChon luaChon = new LuaChon();
                    luaChon.setNoiDung(lc.getNoiDung());
                    luaChon.setLaDapAnDung(lc.getLaDapAnDung());
                    luaChon.setCauHoi(saved);
                    luaChonRepository.save(luaChon);
                }
            }
            count++;
        }
        return count;
    }

    // ── Truy vấn ──────────────────────────────────────────────────────────
    public Quiz layTheoId(Integer id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quiz ID: " + id));
    }

    public List<Quiz> layCuaGiaoVien(NguoiDung giaoVien) {
        return quizRepository.findByGiaoVien(giaoVien);
    }

    public List<Quiz> layPublishedCuaKhoaHoc(Integer maKhoaHoc) {
        return quizRepository.findByKhoaHoc_MaKhoaHocAndTrangThai(maKhoaHoc, "Published");
    }

    public List<Quiz> layTatCaCuaKhoaHoc(Integer maKhoaHoc) {
        return quizRepository.findByKhoaHoc_MaKhoaHoc(maKhoaHoc);
    }

    @Transactional
    public Quiz capNhatQuiz(Integer maQuiz, Quiz thongTinMoi, NguoiDung giaoVien) {
        Quiz quiz = layTheoId(maQuiz);
        kiemTraChuSoHuuQuiz(quiz, giaoVien);
        quiz.setTenQuiz(thongTinMoi.getTenQuiz());
        quiz.setThoiGianLamBai(thongTinMoi.getThoiGianLamBai());
        quiz.setKhoaHoc(thongTinMoi.getKhoaHoc());
        return quizRepository.save(quiz);
    }

    public List<CauHoi> layCauHoiCuaQuiz(Integer maQuiz) {
        Quiz quiz = layTheoId(maQuiz);
        return cauHoiRepository.findByQuiz(quiz);
    }

    public CauHoi layCauHoiTheoId(Integer id) {
        return cauHoiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
    }

    public List<LuaChon> layLuaChonCuaCauHoi(Integer maCauHoi) {
        return luaChonRepository.findByCauHoi_MaCauHoi(maCauHoi);
    }

    // Admin: toàn bộ ngân hàng hệ thống
    public List<CauHoi> layToanBoNganHang() {
        return cauHoiRepository.findByNguonGoc("NganHang");
    }

    // ── Kiểm tra quyền sở hữu ─────────────────────────────────────────────
    private void kiemTraChuSoHuuQuiz(Quiz quiz, NguoiDung giaoVien) {
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(giaoVien.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền thao tác với quiz này.");
        }
    }

    private void kiemTraChuSoHuuNganHang(CauHoi cauHoi, NguoiDung giaoVien) {
        if (cauHoi.getGiaoVienNganHang() == null ||
                !cauHoi.getGiaoVienNganHang().getMaNguoiDung().equals(giaoVien.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền thao tác với câu hỏi này.");
        }
    }
}
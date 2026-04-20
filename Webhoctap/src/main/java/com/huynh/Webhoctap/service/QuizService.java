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

    // ── Xóa câu hỏi ──────────────────────────────────────────────────────
    @Transactional
    public void xoaCauHoi(Integer maCauHoi, NguoiDung giaoVien) {
        CauHoi cauHoi = cauHoiRepository.findById(maCauHoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + maCauHoi));
        kiemTraChuSoHuuQuiz(cauHoi.getQuiz(), giaoVien);
        cauHoiRepository.delete(cauHoi);
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

    public List<CauHoi> layCauHoiCuaQuiz(Integer maQuiz) {
        Quiz quiz = layTheoId(maQuiz);
        return cauHoiRepository.findByQuiz(quiz);
    }

    public List<LuaChon> layLuaChonCuaCauHoi(Integer maCauHoi) {
        return luaChonRepository.findByCauHoi_MaCauHoi(maCauHoi);
    }

    // ── Kiểm tra quyền sở hữu ─────────────────────────────────────────────
    private void kiemTraChuSoHuuQuiz(Quiz quiz, NguoiDung giaoVien) {
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(giaoVien.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền thao tác với quiz này.");
        }
    }
}
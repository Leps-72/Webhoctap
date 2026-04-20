package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.model.CauHoi;
import com.huynh.Webhoctap.model.KetQuaQuiz;
import com.huynh.Webhoctap.model.LuaChon;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.Quiz;
import com.huynh.Webhoctap.repository.CauHoiRepository;
import com.huynh.Webhoctap.repository.KetQuaQuizRepository;
import com.huynh.Webhoctap.repository.LuaChonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KetQuaQuizService {

    private final KetQuaQuizRepository ketQuaQuizRepository;
    private final CauHoiRepository cauHoiRepository;
    private final LuaChonRepository luaChonRepository;
    private final QuizService quizService;

    /**
     * Nộp bài làm và tính điểm tự động.
     *
     * @param maQuiz      ID của quiz
     * @param hocSinh     Người nộp bài
     * @param dapAn       Map<maCauHoi, maLuaChonChon> — đáp án học sinh chọn
     * @param batDau      Thời điểm bắt đầu làm bài
     */
    @Transactional
    public KetQuaQuiz nopBai(Integer maQuiz, NguoiDung hocSinh,
                             Map<Integer, Integer> dapAn, LocalDateTime batDau) {
        Quiz quiz = quizService.layTheoId(maQuiz);
        List<CauHoi> danhSachCauHoi = cauHoiRepository.findByQuiz(quiz);

        double tongDiem = 0.0;
        for (CauHoi cauHoi : danhSachCauHoi) {
            Integer maLuaChonChon = dapAn.get(cauHoi.getMaCauHoi());
            if (maLuaChonChon != null) {
                boolean dungDapAn = luaChonRepository
                        .findById(maLuaChonChon)
                        .map(LuaChon::getLaDapAnDung)
                        .orElse(false);
                if (dungDapAn) {
                    tongDiem += cauHoi.getDiem();
                }
            }
        }

        KetQuaQuiz ketQua = new KetQuaQuiz();
        ketQua.setQuiz(quiz);
        ketQua.setHocSinh(hocSinh);
        ketQua.setDiem(tongDiem);
        ketQua.setThoiGianBatDau(batDau);
        ketQua.setThoiGianKetThuc(LocalDateTime.now());
        return ketQuaQuizRepository.save(ketQua);
    }

    // ── Lịch sử làm bài của học sinh ─────────────────────────────────────
    public List<KetQuaQuiz> layLichSuCuaHocSinh(NguoiDung hocSinh) {
        return ketQuaQuizRepository.findByHocSinh(hocSinh);
    }

    // ── Tất cả kết quả của một quiz (Teacher/Admin xem) ───────────────────
    public List<KetQuaQuiz> layKetQuaCuaQuiz(Integer maQuiz) {
        Quiz quiz = quizService.layTheoId(maQuiz);
        return ketQuaQuizRepository.findByQuiz(quiz);
    }

    // ── Kết quả cao nhất của học sinh trong một quiz ───────────────────────
    public double diemCaoNhatCuaHocSinh(Integer maQuiz, NguoiDung hocSinh) {
        Quiz quiz = quizService.layTheoId(maQuiz);
        return ketQuaQuizRepository
                .findByQuizAndHocSinh(quiz, hocSinh)
                .stream()
                .mapToDouble(KetQuaQuiz::getDiem)
                .max()
                .orElse(0.0);
    }

    // ── Xem chi tiết một lần làm bài ─────────────────────────────────────
    public KetQuaQuiz layTheoId(Integer id) {
        return ketQuaQuizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả ID: " + id));
    }
}
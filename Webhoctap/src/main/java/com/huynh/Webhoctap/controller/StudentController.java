package com.huynh.Webhoctap.controller;

import com.huynh.Webhoctap.dto.QuizSubmitDto;
import com.huynh.Webhoctap.model.ChiTietKetQua;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final NguoiDungService nguoiDungService;
    private final KhoaHocService khoaHocService;
    private final HocLieuService hocLieuService;
    private final QuizService quizService;
    private final KetQuaQuizService ketQuaQuizService;
    private final DangKyKhoaHocService dangKyService;

    // ── Helper ────────────────────────────────────────────────────────────
    private NguoiDung getCurrentUser(UserDetails ud) {
        return nguoiDungService.layTheoEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng."));
    }

    // ══════════════════════════════════════════════════════════════════════
    // DASHBOARD - khóa học đã đăng ký
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung student = getCurrentUser(ud);
        model.addAttribute("danhSachDangKy", dangKyService.layKhoaHocCuaHocSinh(student));
        return "student/dashboard";
    }

    // ══════════════════════════════════════════════════════════════════════
    // KHÓA HỌC
    // ══════════════════════════════════════════════════════════════════════

    // Danh sách khóa học công khai để đăng ký thêm
    @GetMapping("/courses/explore")
    public String khamPhaKhoaHoc(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung student = getCurrentUser(ud);
        model.addAttribute("tatCa", khoaHocService.layDaDuyetVaHienThi());
        model.addAttribute("daDangKyIds",
                dangKyService.layKhoaHocCuaHocSinh(student)
                        .stream()
                        .map(dk -> dk.getKhoaHoc().getMaKhoaHoc())
                        .toList());
        return "student/explore-courses";
    }

    // Chi tiết khóa học + danh sách học liệu + quiz
    @GetMapping("/courses/{id}")
    public String chiTietKhoaHoc(@PathVariable Integer id,
                                 @AuthenticationPrincipal UserDetails ud,
                                 Model model) {
        NguoiDung student = getCurrentUser(ud);
        boolean daDangKy = dangKyService.daDangKy(id, student);

        model.addAttribute("khoaHoc",   khoaHocService.layTheoId(id));
        model.addAttribute("daDangKy",  daDangKy);

        if (daDangKy) {
            model.addAttribute("quizs", quizService.layPublishedCuaKhoaHoc(id));
            model.addAttribute("hocLieus", hocLieuService.layCuaKhoaHoc(id));
        }
        return "student/course-detail";
    }

    // Đăng ký khóa học
    @PostMapping("/courses/{id}/register")
    public String dangKyKhoaHoc(@PathVariable Integer id,
                                @AuthenticationPrincipal UserDetails ud,
                                RedirectAttributes ra) {
        try {
            NguoiDung student = getCurrentUser(ud);
            dangKyService.dangKy(id, student);
            ra.addFlashAttribute("successMsg", "Đăng ký thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/student/courses/" + id;
    }

    // Huỷ đăng ký
    @PostMapping("/courses/{id}/unregister")
    public String huyDangKy(@PathVariable Integer id,
                            @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra) {
        try {
            NguoiDung student = getCurrentUser(ud);
            dangKyService.huyDangKy(id, student);
            ra.addFlashAttribute("successMsg", "Đã huỷ đăng ký.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    // ══════════════════════════════════════════════════════════════════════
    // HỌC LIỆU
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/materials/{id}")
    public String xemHocLieu(@PathVariable Integer id,
                             @AuthenticationPrincipal UserDetails ud,
                             Model model) {
        // Tăng lượt xem mỗi khi student truy cập
        hocLieuService.tangLuotXem(id);
        model.addAttribute("hocLieu", hocLieuService.layTheoId(id));
        return "student/material-view";
    }

    @GetMapping("/materials/{id}/download")
    public String taiHocLieu(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            hocLieuService.tangLuotTai(id);
            String duongDan = hocLieuService.layTheoId(id).getDuongDanTep();
            // Redirect tới file tĩnh để trình duyệt tải xuống
            return "redirect:" + duongDan;
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/student/dashboard";
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // QUIZ - LÀM BÀI
    // ══════════════════════════════════════════════════════════════════════

    // Trang bắt đầu làm bài — lưu thời gian bắt đầu vào session
    @GetMapping("/quizzes/{id}/start")
    public String batDauQuiz(@PathVariable Integer id,
                             @AuthenticationPrincipal UserDetails ud,
                             Model model,
                             jakarta.servlet.http.HttpSession session) {
        NguoiDung student = getCurrentUser(ud);

        // Chỉ cho làm bài nếu đã đăng ký khóa học chứa quiz này
        var quiz = quizService.layTheoId(id);
        if (quiz.getKhoaHoc() != null &&
                !dangKyService.daDangKy(quiz.getKhoaHoc().getMaKhoaHoc(), student)) {
            return "redirect:/student/dashboard";
        }

        // Lưu thời gian bắt đầu vào session để tính thời gian làm bài
        session.setAttribute("quizStartTime_" + id, LocalDateTime.now());

        model.addAttribute("quiz",    quiz);
        model.addAttribute("cauHois", quizService.layCauHoiCuaQuiz(id));
        return "student/quiz-play";
    }

    // Nộp bài — dùng QuizSubmitDto thay vì parse Map thủ công
    @PostMapping("/quizzes/{id}/submit")
    public String nopBai(@PathVariable Integer id,
                         @ModelAttribute QuizSubmitDto submitDto,
                         @AuthenticationPrincipal UserDetails ud,
                         jakarta.servlet.http.HttpSession session,
                         RedirectAttributes ra) {
        try {
            NguoiDung student = getCurrentUser(ud);
            LocalDateTime batDau = (LocalDateTime) session.getAttribute("quizStartTime_" + id);
            if (batDau == null) batDau = LocalDateTime.now();
            session.removeAttribute("quizStartTime_" + id);

            var ketQua = ketQuaQuizService.nopBai(id, student, submitDto.getDapAn(), batDau);
            return "redirect:/student/quizzes/" + id + "/result/" + ketQua.getMaKetQua();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/student/quizzes/" + id + "/start";
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // XEM KẾT QUẢ
    // ══════════════════════════════════════════════════════════════════════

    // Chi tiết một lần làm bài
    @GetMapping("/quizzes/{quizId}/result/{ketQuaId}")
    public String xemKetQua(@PathVariable Integer quizId,
                            @PathVariable Integer ketQuaId,
                            @AuthenticationPrincipal UserDetails ud,
                            Model model) {
        NguoiDung student = getCurrentUser(ud);
        var ketQua = ketQuaQuizService.layTheoId(ketQuaId);

        // Chỉ cho xem kết quả của chính mình
        if (!ketQua.getHocSinh().getMaNguoiDung().equals(student.getMaNguoiDung())) {
            return "redirect:/student/dashboard";
        }

        model.addAttribute("ketQua",  ketQua);
        model.addAttribute("quiz",    quizService.layTheoId(quizId));
        model.addAttribute("cauHois", quizService.layCauHoiCuaQuiz(quizId));

        // Lấy chi tiết kết quả (nếu có)
        List<ChiTietKetQua> chiTietList = ketQuaQuizService.layChiTietKetQua(ketQua);
        Map<Integer, Integer> luaChonDaChon = new HashMap<>();
        for (com.huynh.Webhoctap.model.ChiTietKetQua ct : chiTietList) {
            if (ct.getLuaChonChon() != null) {
                luaChonDaChon.put(ct.getCauHoi().getMaCauHoi(), ct.getLuaChonChon().getMaLuaChon());
            }
        }
        model.addAttribute("luaChonDaChon", luaChonDaChon);

        return "student/quiz-result";
    }

    // Lịch sử tất cả lần làm bài
    @GetMapping("/results")
    public String lichSuLamBai(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung student = getCurrentUser(ud);
        model.addAttribute("lichSu", ketQuaQuizService.layLichSuCuaHocSinh(student));
        return "student/results";
    }
}
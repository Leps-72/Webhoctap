package com.huynh.Webhoctap.controller;

import com.huynh.Webhoctap.model.*;
import com.huynh.Webhoctap.dto.*;
import com.huynh.Webhoctap.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final NguoiDungService nguoiDungService;
    private final KhoaHocService khoaHocService;
    private final HocLieuService hocLieuService;
    private final QuizService quizService;
    private final DangKyKhoaHocService dangKyService;

    // ── Helper ────────────────────────────────────────────────────────────
    private NguoiDung getCurrentUser(UserDetails ud) {
        return nguoiDungService.layTheoEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng."));
    }

    // ══════════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(teacher));
        model.addAttribute("quizs",    quizService.layCuaGiaoVien(teacher));
        return "teacher/dashboard";
    }

    // ══════════════════════════════════════════════════════════════════════
    // KHÓA HỌC
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/courses")
    public String danhSachKhoaHoc(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(teacher));
        return "teacher/courses";
    }

    @GetMapping("/courses/new")
    public String formTaoKhoaHoc(Model model) {
        model.addAttribute("khoaHocDto", new KhoaHocDto());
        return "teacher/course-form";
    }

    @PostMapping("/courses/new")
    public String taoKhoaHoc(@Valid @ModelAttribute("khoaHocDto") KhoaHocDto dto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra,
                             Model model) {
        if (bindingResult.hasErrors()) return "teacher/course-form";
        try {
            NguoiDung teacher = getCurrentUser(ud);
            KhoaHoc khoaHoc = new KhoaHoc();
            khoaHoc.setTenKhoaHoc(dto.getTenKhoaHoc());
            khoaHoc.setSlug(dto.getSlug());
            khoaHoc.setMoTa(dto.getMoTa());
            khoaHoc.setAnhBia(dto.getAnhBia());
            KhoaHoc saved = khoaHocService.taoKhoaHoc(khoaHoc, teacher);
            ra.addFlashAttribute("successMsg", "Tạo khóa học thành công!");
            return "redirect:/teacher/courses/" + saved.getMaKhoaHoc() + "/edit";
        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "teacher/course-form";
        }
    }

    @GetMapping("/courses/{id}/edit")
    public String formSuaKhoaHoc(@PathVariable Integer id,
                                 @AuthenticationPrincipal UserDetails ud,
                                 Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        KhoaHoc khoaHoc = khoaHocService.layTheoId(id);
        if (!khoaHoc.getGiaoVien().getMaNguoiDung().equals(teacher.getMaNguoiDung())) {
            return "redirect:/teacher/courses";
        }
        // Điền sẵn DTO từ entity hiện tại
        KhoaHocDto dto = new KhoaHocDto();
        dto.setTenKhoaHoc(khoaHoc.getTenKhoaHoc());
        dto.setSlug(khoaHoc.getSlug());
        dto.setMoTa(khoaHoc.getMoTa());
        dto.setAnhBia(khoaHoc.getAnhBia());

        model.addAttribute("khoaHocDto", dto);
        model.addAttribute("khoaHoc",    khoaHoc);
        model.addAttribute("quizs",      quizService.layPublishedCuaKhoaHoc(id));
        model.addAttribute("hocSinhs",   dangKyService.layHocSinhCuaKhoaHoc(id));
        return "teacher/course-form";
    }

    @PostMapping("/courses/{id}/edit")
    public String suaKhoaHoc(@PathVariable Integer id,
                             @Valid @ModelAttribute("khoaHocDto") KhoaHocDto dto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra,
                             Model model) {
        if (bindingResult.hasErrors()) return "teacher/course-form";
        try {
            NguoiDung teacher = getCurrentUser(ud);
            KhoaHoc thongTinMoi = new KhoaHoc();
            thongTinMoi.setTenKhoaHoc(dto.getTenKhoaHoc());
            thongTinMoi.setSlug(dto.getSlug());
            thongTinMoi.setMoTa(dto.getMoTa());
            thongTinMoi.setAnhBia(dto.getAnhBia());
            khoaHocService.capNhat(id, thongTinMoi, teacher);
            ra.addFlashAttribute("successMsg", "Cập nhật thành công.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/courses/" + id + "/edit";
    }

    // Gửi duyệt
    @PostMapping("/courses/{id}/submit")
    public String guiDuyet(@PathVariable Integer id,
                           @AuthenticationPrincipal UserDetails ud,
                           RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            khoaHocService.guiYeuCauDuyet(id, teacher);
            ra.addFlashAttribute("successMsg", "Đã gửi yêu cầu duyệt.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/courses";
    }

    // Ẩn / hiện khóa học
    @PostMapping("/courses/{id}/toggle")
    public String toggleHienThi(@PathVariable Integer id,
                                @RequestParam boolean isActive,
                                @AuthenticationPrincipal UserDetails ud,
                                RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            khoaHocService.doiTrangThaiHienThi(id, teacher, isActive);
            ra.addFlashAttribute("successMsg", isActive ? "Đã hiện khóa học." : "Đã ẩn khóa học.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/courses";
    }

    // ══════════════════════════════════════════════════════════════════════
    // HỌC LIỆU
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/materials")
    public String danhSachHocLieu(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("hocLieus", hocLieuService.layCuaNguoiDung(teacher));
        return "teacher/materials";
    }

    @GetMapping("/materials/upload")
    public String formUpload(Model model) {
        model.addAttribute("hocLieuDto", new HocLieuDto());
        return "teacher/upload-material";
    }

    @PostMapping("/materials/upload")
    public String uploadHocLieu(@Valid @ModelAttribute("hocLieuDto") HocLieuDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails ud,
                                RedirectAttributes ra,
                                Model model) {
        if (bindingResult.hasErrors()) return "teacher/upload-material";
        try {
            NguoiDung teacher = getCurrentUser(ud);
            HocLieu hocLieu = new HocLieu();
            hocLieu.setTieuDe(dto.getTieuDe());
            hocLieu.setMoTa(dto.getMoTa());
            hocLieu.setDoKho(dto.getDoKho());
            hocLieuService.uploadHocLieu(hocLieu, dto.getFile(), teacher);
            ra.addFlashAttribute("successMsg", "Upload thành công! Đang chờ duyệt.");
        } catch (java.io.IOException e) {
            model.addAttribute("errorMsg", "Lỗi khi upload file: " + e.getMessage());
            return "teacher/upload-material";
        }
        return "redirect:/teacher/materials";
    }

    @PostMapping("/materials/{id}/delete")
    public String xoaHocLieu(@PathVariable Integer id,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            hocLieuService.xoaHocLieu(id, teacher);
            ra.addFlashAttribute("successMsg", "Đã xóa học liệu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/materials";
    }

    // ══════════════════════════════════════════════════════════════════════
    // QUIZ
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/quizzes")
    public String danhSachQuiz(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("quizs", quizService.layCuaGiaoVien(teacher));
        return "teacher/quizzes";
    }

    @GetMapping("/quizzes/new")
    public String formTaoQuiz(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("quizDto",    new QuizDto());
        model.addAttribute("khoaHocs",   khoaHocService.layCuaGiaoVien(teacher));
        return "teacher/quiz-form";
    }

    @PostMapping("/quizzes/new")
    public String taoQuiz(@Valid @ModelAttribute("quizDto") QuizDto dto,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserDetails ud,
                          RedirectAttributes ra,
                          Model model) {
        if (bindingResult.hasErrors()) {
            NguoiDung teacher = getCurrentUser(ud);
            model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(teacher));
            return "teacher/quiz-form";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            Quiz quiz = new Quiz();
            quiz.setTenQuiz(dto.getTenQuiz());
            quiz.setThoiGianLamBai(dto.getThoiGianLamBai());
            if (dto.getMaKhoaHoc() != null) {
                quiz.setKhoaHoc(khoaHocService.layTheoId(dto.getMaKhoaHoc()));
            }
            Quiz saved = quizService.taoQuiz(quiz, teacher);
            ra.addFlashAttribute("successMsg", "Tạo quiz thành công! Thêm câu hỏi bên dưới.");
            return "redirect:/teacher/quizzes/" + saved.getMaQuiz() + "/edit";
        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "teacher/quiz-form";
        }
    }

    @GetMapping("/quizzes/{id}/edit")
    public String formSuaQuiz(@PathVariable Integer id,
                              @AuthenticationPrincipal UserDetails ud,
                              Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        Quiz quiz = quizService.layTheoId(id);
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(teacher.getMaNguoiDung())) {
            return "redirect:/teacher/quizzes";
        }
        model.addAttribute("quiz",      quiz);
        model.addAttribute("cauHois",   quizService.layCauHoiCuaQuiz(id));
        model.addAttribute("cauHoiDto", new CauHoiDto()); // form thêm câu hỏi mới
        return "teacher/quiz-builder";
    }

    // Thêm câu hỏi + lựa chọn cùng lúc qua CauHoiDto
    @PostMapping("/quizzes/{id}/questions/add")
    public String themCauHoi(@PathVariable Integer id,
                             @Valid @ModelAttribute("cauHoiDto") CauHoiDto dto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("errorMsg", "Dữ liệu câu hỏi không hợp lệ.");
            return "redirect:/teacher/quizzes/" + id + "/edit";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            CauHoi cauHoi = new CauHoi();
            cauHoi.setNoiDung(dto.getNoiDung());
            cauHoi.setLoaiCauHoi(dto.getLoaiCauHoi());
            cauHoi.setDiem(dto.getDiem());
            CauHoi saved = quizService.themCauHoi(id, cauHoi, teacher);

            // Thêm các lựa chọn kèm theo
            for (CauHoiDto.LuaChonDto lcDto : dto.getLuaChons()) {
                LuaChon luaChon = new LuaChon();
                luaChon.setNoiDung(lcDto.getNoiDung());
                luaChon.setLaDapAnDung(lcDto.getLaDapAnDung());
                quizService.themLuaChon(saved.getMaCauHoi(), luaChon);
            }
            ra.addFlashAttribute("successMsg", "Đã thêm câu hỏi.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + id + "/edit";
    }

    // Xóa câu hỏi
    @PostMapping("/quizzes/{quizId}/questions/{cauHoiId}/delete")
    public String xoaCauHoi(@PathVariable Integer quizId,
                            @PathVariable Integer cauHoiId,
                            @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            quizService.xoaCauHoi(cauHoiId, teacher);
            ra.addFlashAttribute("successMsg", "Đã xóa câu hỏi.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + quizId + "/edit";
    }

    // Publish / Unpublish quiz
    @PostMapping("/quizzes/{id}/publish")
    public String publishQuiz(@PathVariable Integer id,
                              @AuthenticationPrincipal UserDetails ud,
                              RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            quizService.publishQuiz(id, teacher);
            ra.addFlashAttribute("successMsg", "Quiz đã được publish.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes";
    }

    @PostMapping("/quizzes/{id}/unpublish")
    public String unpublishQuiz(@PathVariable Integer id,
                                @AuthenticationPrincipal UserDetails ud,
                                RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            quizService.unPublishQuiz(id, teacher);
            ra.addFlashAttribute("successMsg", "Quiz đã chuyển về Draft.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes";
    }

    // Xem kết quả học viên của quiz
    @GetMapping("/quizzes/{id}/results")
    public String xemKetQua(@PathVariable Integer id, Model model) {
        model.addAttribute("quiz", quizService.layTheoId(id));
        model.addAttribute("cauHois", quizService.layCauHoiCuaQuiz(id));
        return "teacher/quiz-results";
    }
}
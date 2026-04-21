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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final NguoiDungService nguoiDungService;
    private final KhoaHocService khoaHocService;
    private final HocLieuService hocLieuService;
    private final QuizService quizService;
    private final DangKyKhoaHocService dangKyService;
    private final KetQuaQuizService ketQuaQuizService;

    private NguoiDung getCurrentUser(UserDetails ud) {
        return nguoiDungService.layTheoEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng."));
    }

    // ══ DASHBOARD ══════════════════════════════════════════════════════════
    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        List<KhoaHoc> khoaHocs = khoaHocService.layCuaGiaoVien(teacher);
        List<Quiz> quizs = quizService.layCuaGiaoVien(teacher);
        model.addAttribute("khoaHocs", khoaHocs);
        model.addAttribute("quizs", quizs);
        model.addAttribute("tongKhoaHoc", khoaHocs.size());
        model.addAttribute("tongQuiz", quizs.size());
        model.addAttribute("tongHocLieu", hocLieuService.layCuaNguoiDung(teacher).size());
        model.addAttribute("tongNganHang", quizService.layNganHangCuaGiaoVien(teacher).size());
        return "teacher/dashboard";
    }

    // ══ KHÓA HỌC ══════════════════════════════════════════════════════════
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
                             RedirectAttributes ra, Model model) {
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
                                 @AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        KhoaHoc khoaHoc = khoaHocService.layTheoId(id);
        if (!khoaHoc.getGiaoVien().getMaNguoiDung().equals(teacher.getMaNguoiDung()))
            return "redirect:/teacher/courses";
        KhoaHocDto dto = new KhoaHocDto();
        dto.setTenKhoaHoc(khoaHoc.getTenKhoaHoc());
        dto.setSlug(khoaHoc.getSlug());
        dto.setMoTa(khoaHoc.getMoTa());
        dto.setAnhBia(khoaHoc.getAnhBia());
        model.addAttribute("khoaHocDto", dto);
        model.addAttribute("khoaHoc", khoaHoc);
        model.addAttribute("quizs", quizService.layTatCaCuaKhoaHoc(id));
        model.addAttribute("hocSinhs", dangKyService.layHocSinhCuaKhoaHoc(id));
        return "teacher/course-form";
    }

    @PostMapping("/courses/{id}/edit")
    public String suaKhoaHoc(@PathVariable Integer id,
                             @Valid @ModelAttribute("khoaHocDto") KhoaHocDto dto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra, Model model) {
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

    @PostMapping("/courses/{id}/submit")
    public String guiDuyet(@PathVariable Integer id,
                           @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            khoaHocService.guiYeuCauDuyet(id, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Đã gửi yêu cầu duyệt.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/courses";
    }

    @PostMapping("/courses/{id}/toggle")
    public String toggleHienThi(@PathVariable Integer id, @RequestParam boolean isActive,
                                @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            khoaHocService.doiTrangThaiHienThi(id, getCurrentUser(ud), isActive);
            ra.addFlashAttribute("successMsg", isActive ? "Đã hiện khóa học." : "Đã ẩn khóa học.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/courses";
    }

    // ══ HỌC LIỆU ══════════════════════════════════════════════════════════
    @GetMapping("/materials")
    public String danhSachHocLieu(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("hocLieus", hocLieuService.layCuaNguoiDung(teacher));
        return "teacher/materials";
    }

    @GetMapping("/materials/upload")
    public String formUpload(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("hocLieuDto", new HocLieuDto());
        model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(getCurrentUser(ud)));
        return "teacher/upload-material";
    }

    @PostMapping("/materials/upload")
    public String uploadHocLieu(@Valid @ModelAttribute("hocLieuDto") HocLieuDto dto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails ud,
                                RedirectAttributes ra, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(getCurrentUser(ud)));
            return "teacher/upload-material";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            HocLieu hocLieu = new HocLieu();
            hocLieu.setTieuDe(dto.getTieuDe());
            hocLieu.setMoTa(dto.getMoTa());
            hocLieu.setDoKho(dto.getDoKho());
            if (dto.getMaKhoaHoc() != null) {
                hocLieu.setKhoaHoc(khoaHocService.layTheoId(dto.getMaKhoaHoc()));
            }
            hocLieuService.uploadHocLieu(hocLieu, dto.getFile(), teacher);
            ra.addFlashAttribute("successMsg", "Upload thành công! Đang chờ duyệt.");
        } catch (java.io.IOException e) {
            model.addAttribute("errorMsg", "Lỗi khi upload file: " + e.getMessage());
            model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(getCurrentUser(ud)));
            return "teacher/upload-material";
        }
        return "redirect:/teacher/materials";
    }

    @PostMapping("/materials/{id}/delete")
    public String xoaHocLieu(@PathVariable Integer id,
                             @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            hocLieuService.xoaHocLieu(id, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Đã xóa học liệu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/materials";
    }

    // ══ QUIZ ══════════════════════════════════════════════════════════════
    @GetMapping("/quizzes")
    public String danhSachQuiz(@AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("quizs", quizService.layCuaGiaoVien(teacher));
        return "teacher/quizzes";
    }

    @GetMapping("/quizzes/new")
    public String formTaoQuiz(@RequestParam(required = false) Integer courseId,
                              @AuthenticationPrincipal UserDetails ud, Model model) {
        if (ud == null) return "redirect:/login";
        NguoiDung teacher = getCurrentUser(ud);
        QuizDto dto = new QuizDto();
        if (courseId != null) dto.setMaKhoaHoc(courseId);
        model.addAttribute("quizDto", dto);
        model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(teacher));
        return "teacher/quiz-form";
    }

    @PostMapping("/quizzes/new")
    public String taoQuiz(@Valid @ModelAttribute("quizDto") QuizDto dto,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserDetails ud,
                          RedirectAttributes ra, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(getCurrentUser(ud)));
            return "teacher/quiz-form";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            Quiz quiz = new Quiz();
            quiz.setTenQuiz(dto.getTenQuiz());
            quiz.setThoiGianLamBai(dto.getThoiGianLamBai());
            if (dto.getMaKhoaHoc() != null)
                quiz.setKhoaHoc(khoaHocService.layTheoId(dto.getMaKhoaHoc()));
            Quiz saved = quizService.taoQuiz(quiz, teacher);
            ra.addFlashAttribute("successMsg", "Tạo quiz thành công! Thêm câu hỏi bên dưới.");
            return "redirect:/teacher/quizzes/" + saved.getMaQuiz() + "/edit";
        } catch (Exception e) {
            model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(getCurrentUser(ud)));
            model.addAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "teacher/quiz-form";
        }
    }

    // ── KẾT QUẢ CỦA QUIZ (Teacher xem) ──────────────────────────────────
    @GetMapping("/quizzes/{id}/results")
    public String danhSachKetQua(@PathVariable Integer id, @AuthenticationPrincipal UserDetails ud, Model model) {
        Quiz quiz = quizService.layTheoId(id);
        // Kiểm tra quyền sở hữu
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(getCurrentUser(ud).getMaNguoiDung())) {
            return "redirect:/teacher/quizzes";
        }
        model.addAttribute("quiz", quiz);
        List<KetQuaQuiz> ketQuas = ketQuaQuizService.layKetQuaCuaQuiz(id);
        model.addAttribute("danhSachKetQua", ketQuas);
        
        double trungBinh = ketQuas.stream().mapToDouble(KetQuaQuiz::getDiem).average().orElse(0);
        double caoNhat  = ketQuas.stream().mapToDouble(KetQuaQuiz::getDiem).max().orElse(0);
        double thapNhat = ketQuas.stream().mapToDouble(KetQuaQuiz::getDiem).min().orElse(0);
        model.addAttribute("trungBinh", String.format("%.1f", trungBinh));
        model.addAttribute("caoNhat",  String.format("%.1f", caoNhat));
        model.addAttribute("thapNhat", String.format("%.1f", thapNhat));

        return "teacher/quiz-results";
    }

    @GetMapping("/quizzes/{quizId}/results/{ketQuaId}")
    public String xemChiTietKetQua(@PathVariable Integer quizId, @PathVariable Integer ketQuaId,
                                   @AuthenticationPrincipal UserDetails ud, Model model) {
        Quiz quiz = quizService.layTheoId(quizId);
        // Kiểm tra quyền
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(getCurrentUser(ud).getMaNguoiDung())) {
            return "redirect:/teacher/quizzes";
        }

        KetQuaQuiz ketQua = ketQuaQuizService.layTheoId(ketQuaId);
        model.addAttribute("ketQua", ketQua);
        model.addAttribute("quiz", quiz);
        model.addAttribute("cauHois", quizService.layCauHoiCuaQuiz(quizId));

        // Lấy chi tiết chọn lựa
        java.util.List<ChiTietKetQua> chiTietList = ketQuaQuizService.layChiTietKetQua(ketQua);
        java.util.Map<Integer, Integer> luaChonDaChon = new java.util.HashMap<>();
        for (ChiTietKetQua ct : chiTietList) {
            if (ct.getLuaChonChon() != null) {
                luaChonDaChon.put(ct.getCauHoi().getMaCauHoi(), ct.getLuaChonChon().getMaLuaChon());
            }
        }
        model.addAttribute("luaChonDaChon", luaChonDaChon);

        return "teacher/quiz-result-detail";
    }

    @GetMapping("/quizzes/{id}/edit")
    public String formSuaQuiz(@PathVariable Integer id,
                              @AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        Quiz quiz = quizService.layTheoId(id);
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(teacher.getMaNguoiDung()))
            return "redirect:/teacher/quizzes";
        model.addAttribute("quiz", quiz);
        List<CauHoi> cauHois = quizService.layCauHoiCuaQuiz(id);
        model.addAttribute("cauHois", cauHois);
        
        double tongDiem = 0;
        for (CauHoi ch : cauHois) {
            if (ch.getDiem() != null) tongDiem += ch.getDiem();
        }
        model.addAttribute("tongDiem", tongDiem);
        CauHoiDto chDto = new CauHoiDto();
        chDto.setLoaiCauHoi("TracNghiem");
        model.addAttribute("cauHoiDto", chDto);
        model.addAttribute("nganHang", quizService.layNganHangCuaGiaoVien(teacher));
        
        QuizDto quizDto = new QuizDto();
        quizDto.setTenQuiz(quiz.getTenQuiz());
        quizDto.setThoiGianLamBai(quiz.getThoiGianLamBai());
        quizDto.setMaKhoaHoc(quiz.getKhoaHoc() != null ? quiz.getKhoaHoc().getMaKhoaHoc() : null);
        model.addAttribute("quizDto", quizDto);
        model.addAttribute("khoaHocs", khoaHocService.layCuaGiaoVien(teacher));
        
        return "teacher/quiz-builder";
    }

    @PostMapping("/quizzes/{id}/delete")
    public String xoaQuiz(@PathVariable Integer id,
                           @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra,
                           jakarta.servlet.http.HttpServletRequest request) {
        Integer maKhoaHoc = null;
        try {
            Quiz quiz = quizService.layTheoId(id);
            if (quiz != null && quiz.getKhoaHoc() != null) {
                maKhoaHoc = quiz.getKhoaHoc().getMaKhoaHoc();
            }
            quizService.xoaQuiz(id, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Đã xóa quiz thành công.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/teacher/courses/")) {
            // Stay on course page, specifically the quiz tab
            if (!referer.contains("tab=")) {
                if (referer.contains("?")) return "redirect:" + referer + "&tab=quiz";
                else return "redirect:" + referer + "?tab=quiz";
            }
            return "redirect:" + referer;
        } else if (referer != null && referer.contains("/teacher/quizzes")) {
            return "redirect:/teacher/quizzes";
        }
        
        if (maKhoaHoc != null) {
            return "redirect:/teacher/courses/" + maKhoaHoc + "/edit?tab=quiz";
        }
        return "redirect:/teacher/quizzes";
    }

    @PostMapping("/quizzes/{id}/edit")
    public String suaQuizThongTin(@PathVariable Integer id,
                                   @Valid @ModelAttribute("quizDto") QuizDto dto,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails ud,
                                   RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                .map(org.springframework.validation.ObjectError::getDefaultMessage)
                .collect(java.util.stream.Collectors.joining(", "));
            ra.addFlashAttribute("errorMsg", "Thông tin quiz không hợp lệ: " + errors);
            return "redirect:/teacher/quizzes/" + id + "/edit";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            Quiz thongTinMoi = new Quiz();
            thongTinMoi.setTenQuiz(dto.getTenQuiz());
            thongTinMoi.setThoiGianLamBai(dto.getThoiGianLamBai());
            if (dto.getMaKhoaHoc() != null)
                thongTinMoi.setKhoaHoc(khoaHocService.layTheoId(dto.getMaKhoaHoc()));
            quizService.capNhatQuiz(id, thongTinMoi, teacher);
            ra.addFlashAttribute("successMsg", "Đã cập nhật thông tin quiz.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + id + "/edit";
    }

    @PostMapping("/quizzes/{id}/questions/add")
    public String themCauHoi(@PathVariable Integer id,
                             @Valid @ModelAttribute("cauHoiDto") CauHoiDto dto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails ud,
                             RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                .map(org.springframework.validation.ObjectError::getDefaultMessage)
                .collect(java.util.stream.Collectors.joining(", "));
            ra.addFlashAttribute("errorMsg", "Lỗi dữ liệu câu hỏi: " + errors);
            return "redirect:/teacher/quizzes/" + id + "/edit";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            CauHoi cauHoi = new CauHoi();
            cauHoi.setNoiDung(dto.getNoiDung());
            cauHoi.setLoaiCauHoi(dto.getLoaiCauHoi());
            cauHoi.setDiem(dto.getDiem());
            cauHoi.setChuDe(dto.getChuDe());
            cauHoi.setDoKho(dto.getDoKho());
            CauHoi saved = quizService.themCauHoi(id, cauHoi, teacher);
            if (dto.getLuaChons() != null) {
                for (CauHoiDto.LuaChonDto lcDto : dto.getLuaChons()) {
                    if (lcDto.getNoiDung() != null && !lcDto.getNoiDung().isBlank()) {
                        LuaChon luaChon = new LuaChon();
                        luaChon.setNoiDung(lcDto.getNoiDung());
                        luaChon.setLaDapAnDung(lcDto.getLaDapAnDung() != null && lcDto.getLaDapAnDung());
                        quizService.themLuaChon(saved.getMaCauHoi(), luaChon);
                    }
                }
            }
            ra.addFlashAttribute("successMsg", "Đã thêm câu hỏi thành công.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + id + "/edit";
    }

    @GetMapping("/quizzes/{quizId}/questions/{cauHoiId}/edit")
    public String formSuaCauHoi(@PathVariable Integer quizId,
                                @PathVariable Integer cauHoiId,
                                @AuthenticationPrincipal UserDetails ud, Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        Quiz quiz = quizService.layTheoId(quizId);
        if (!quiz.getGiaoVien().getMaNguoiDung().equals(teacher.getMaNguoiDung()))
            return "redirect:/teacher/quizzes";
        CauHoi cauHoi = quizService.layCauHoiTheoId(cauHoiId);
        CauHoiDto dto = new CauHoiDto();
        dto.setNoiDung(cauHoi.getNoiDung());
        dto.setLoaiCauHoi(cauHoi.getLoaiCauHoi());
        dto.setDiem(cauHoi.getDiem());
        dto.setChuDe(cauHoi.getChuDe());
        dto.setDoKho(cauHoi.getDoKho());
        
        // Load choices into DTO
        if (cauHoi.getLuaChons() != null) {
            for (LuaChon lc : cauHoi.getLuaChons()) {
                dto.getLuaChons().add(new CauHoiDto.LuaChonDto(lc.getNoiDung(), lc.getLaDapAnDung()));
            }
        }
        // Pad to 4 options for the UI
        while (dto.getLuaChons().size() < 4) dto.getLuaChons().add(new CauHoiDto.LuaChonDto());
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("cauHoi", cauHoi);
        model.addAttribute("cauHoiDto", dto);
        return "teacher/question-edit";
    }

    @PostMapping("/quizzes/{quizId}/questions/{cauHoiId}/edit")
    public String suaCauHoi(@PathVariable Integer quizId,
                            @PathVariable Integer cauHoiId,
                            @Valid @ModelAttribute("cauHoiDto") CauHoiDto dto,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                .map(org.springframework.validation.ObjectError::getDefaultMessage)
                .collect(java.util.stream.Collectors.joining(", "));
            ra.addFlashAttribute("errorMsg", "Lỗi dữ liệu: " + errors);
            return "redirect:/teacher/quizzes/" + quizId + "/questions/" + cauHoiId + "/edit";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            CauHoi thongTinMoi = new CauHoi();
            thongTinMoi.setNoiDung(dto.getNoiDung());
            thongTinMoi.setLoaiCauHoi(dto.getLoaiCauHoi());
            thongTinMoi.setDiem(dto.getDiem());
            thongTinMoi.setChuDe(dto.getChuDe());
            thongTinMoi.setDoKho(dto.getDoKho());
            
            quizService.capNhatCauHoi(cauHoiId, thongTinMoi, teacher);
            
            // Update choices
            java.util.List<LuaChon> luaChons = new java.util.ArrayList<>();
            if (dto.getLuaChons() != null) {
                for (CauHoiDto.LuaChonDto lcDto : dto.getLuaChons()) {
                    if (lcDto.getNoiDung() != null && !lcDto.getNoiDung().isBlank()) {
                        LuaChon lc = new LuaChon();
                        lc.setNoiDung(lcDto.getNoiDung());
                        lc.setLaDapAnDung(lcDto.getLaDapAnDung() != null && lcDto.getLaDapAnDung());
                        luaChons.add(lc);
                    }
                }
            }
            quizService.capNhatLuaChons(cauHoiId, luaChons);
            
            ra.addFlashAttribute("successMsg", "Đã cập nhật câu hỏi.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + quizId + "/edit";
    }

    @PostMapping("/quizzes/{quizId}/questions/{cauHoiId}/delete")
    public String xoaCauHoi(@PathVariable Integer quizId, @PathVariable Integer cauHoiId,
                            @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            quizService.xoaCauHoi(cauHoiId, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Đã xóa câu hỏi.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + quizId + "/edit";
    }

    @PostMapping("/quizzes/{quizId}/import-questions")
    public String importCauHoi(@PathVariable Integer quizId,
                               @RequestParam List<Integer> cauHoiIds,
                               @AuthenticationPrincipal UserDetails ud,
                               RedirectAttributes ra) {
        try {
            int count = quizService.importCauHoiVaoQuiz(quizId, cauHoiIds, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Đã import " + count + " câu hỏi từ ngân hàng.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes/" + quizId + "/edit";
    }

    @PostMapping("/quizzes/{id}/publish")
    public String publishQuiz(@PathVariable Integer id,
                              @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            quizService.publishQuiz(id, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Quiz đã được publish.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes";
    }

    @PostMapping("/quizzes/{id}/unpublish")
    public String unpublishQuiz(@PathVariable Integer id,
                                @AuthenticationPrincipal UserDetails ud, RedirectAttributes ra) {
        try {
            quizService.unPublishQuiz(id, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Quiz đã chuyển về Draft.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/quizzes";
    }


    // ══ NGÂN HÀNG CÂU HỎI ═════════════════════════════════════════════════
    @GetMapping("/question-bank")
    public String nganHangCauHoi(@AuthenticationPrincipal UserDetails ud,
                                 @RequestParam(required = false) String chuDe,
                                 @RequestParam(required = false) String doKho,
                                 Model model) {
        NguoiDung teacher = getCurrentUser(ud);
        model.addAttribute("cauHois", quizService.layNganHangVoiFilter(teacher, chuDe, doKho));
        model.addAttribute("filterChuDe", chuDe);
        model.addAttribute("filterDoKho", doKho);
        return "teacher/question-bank";
    }

    @GetMapping("/question-bank/new")
    public String formTaoCauHoiNganHang(Model model) {
        CauHoiDto dto = new CauHoiDto();
        dto.getLuaChons().add(new CauHoiDto.LuaChonDto());
        dto.getLuaChons().add(new CauHoiDto.LuaChonDto());
        dto.getLuaChons().add(new CauHoiDto.LuaChonDto());
        dto.getLuaChons().add(new CauHoiDto.LuaChonDto());
        model.addAttribute("cauHoiDto", dto);
        model.addAttribute("isEdit", false);
        return "teacher/question-form";
    }

    @PostMapping("/question-bank/new")
    public String taoCauHoiNganHang(@Valid @ModelAttribute("cauHoiDto") CauHoiDto dto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails ud,
                                    RedirectAttributes ra, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "teacher/question-form";
        }
        try {
            NguoiDung teacher = getCurrentUser(ud);
            CauHoi cauHoi = new CauHoi();
            cauHoi.setNoiDung(dto.getNoiDung());
            cauHoi.setLoaiCauHoi(dto.getLoaiCauHoi());
            cauHoi.setDiem(dto.getDiem());
            cauHoi.setChuDe(dto.getChuDe());
            cauHoi.setDoKho(dto.getDoKho());
            CauHoi saved = quizService.taoCauHoiNganHang(cauHoi, teacher);
            for (CauHoiDto.LuaChonDto lcDto : dto.getLuaChons()) {
                if (lcDto.getNoiDung() != null && !lcDto.getNoiDung().isBlank()) {
                    LuaChon lc = new LuaChon();
                    lc.setNoiDung(lcDto.getNoiDung());
                    lc.setLaDapAnDung(lcDto.getLaDapAnDung() != null && lcDto.getLaDapAnDung());
                    quizService.themLuaChon(saved.getMaCauHoi(), lc);
                }
            }
            ra.addFlashAttribute("successMsg", "Đã thêm câu hỏi vào ngân hàng.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/question-bank";
    }

    @GetMapping("/question-bank/{id}/edit")
    public String formSuaCauHoiNganHang(@PathVariable Integer id,
                                        @AuthenticationPrincipal UserDetails ud, Model model) {
        CauHoi cauHoi = quizService.layCauHoiTheoId(id);
        CauHoiDto dto = new CauHoiDto();
        dto.setNoiDung(cauHoi.getNoiDung());
        dto.setLoaiCauHoi(cauHoi.getLoaiCauHoi());
        dto.setDiem(cauHoi.getDiem());
        dto.setChuDe(cauHoi.getChuDe());
        dto.setDoKho(cauHoi.getDoKho());
        if (cauHoi.getLuaChons() != null) {
            for (LuaChon lc : cauHoi.getLuaChons()) {
                dto.getLuaChons().add(new CauHoiDto.LuaChonDto(lc.getNoiDung(), lc.getLaDapAnDung()));
            }
        }
        while (dto.getLuaChons().size() < 4) dto.getLuaChons().add(new CauHoiDto.LuaChonDto());
        model.addAttribute("cauHoiDto", dto);
        model.addAttribute("cauHoi", cauHoi);
        model.addAttribute("isEdit", true);
        return "teacher/question-form";
    }

    @PostMapping("/question-bank/{id}/edit")
    public String suaCauHoiNganHang(@PathVariable Integer id,
                                    @ModelAttribute("cauHoiDto") CauHoiDto dto,
                                    @AuthenticationPrincipal UserDetails ud,
                                    RedirectAttributes ra) {
        try {
            NguoiDung teacher = getCurrentUser(ud);
            CauHoi thongTinMoi = new CauHoi();
            thongTinMoi.setNoiDung(dto.getNoiDung());
            thongTinMoi.setLoaiCauHoi(dto.getLoaiCauHoi());
            thongTinMoi.setDiem(dto.getDiem());
            thongTinMoi.setChuDe(dto.getChuDe());
            thongTinMoi.setDoKho(dto.getDoKho());
            quizService.capNhatCauHoi(id, thongTinMoi, teacher);
            ra.addFlashAttribute("successMsg", "Đã cập nhật câu hỏi.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/question-bank";
    }

    @PostMapping("/question-bank/{id}/delete")
    public String xoaCauHoiNganHang(@PathVariable Integer id,
                                    @AuthenticationPrincipal UserDetails ud,
                                    RedirectAttributes ra) {
        try {
            quizService.xoaCauHoi(id, getCurrentUser(ud));
            ra.addFlashAttribute("successMsg", "Đã xóa câu hỏi khỏi ngân hàng.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/teacher/question-bank";
    }
}
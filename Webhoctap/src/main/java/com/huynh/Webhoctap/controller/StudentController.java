package com.huynh.Webhoctap.controller;

import com.huynh.Webhoctap.dto.QuizSubmitDto;
import com.huynh.Webhoctap.model.ChiTietKetQua;
import com.huynh.Webhoctap.model.HocLieu;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    // ── Helper ────────────────────────────────────────────────────────────
    private NguoiDung getCurrentUser(UserDetails ud) {
        return nguoiDungService.layTheoEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng."));
    }

    /** Kiểm tra student có quyền truy cập học liệu (đã đăng ký khóa học chứa tài liệu). */
    private boolean coQuyenXemHocLieu(HocLieu hl, NguoiDung student) {
        if (hl.getKhoaHoc() == null) return true; // tài liệu không gắn khóa học → cho xem
        return dangKyService.daDangKy(hl.getKhoaHoc().getMaKhoaHoc(), student);
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
                             Model model,
                             RedirectAttributes ra) {
        try {
            NguoiDung student = getCurrentUser(ud);
            HocLieu hl = hocLieuService.layTheoId(id);

            // Kiểm tra quyền truy cập
            if (!coQuyenXemHocLieu(hl, student)) {
                ra.addFlashAttribute("errorMsg", "Bạn cần đăng ký khóa học để xem tài liệu này.");
                return "redirect:/student/dashboard";
            }

            // Tăng lượt xem
            hocLieuService.tangLuotXem(id);
            model.addAttribute("hocLieu", hl);
            return "student/material-view";

        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", "Không thể mở học liệu: " + e.getMessage());
            return "redirect:/student/dashboard";
        }
    }

    /**
     * Serve file inline — dùng cho iframe PDF/ảnh preview.
     * Không yêu cầu đăng ký (vì đã kiểm tra khi vào xemHocLieu).
     */
    @GetMapping("/materials/{id}/preview")
    @ResponseBody
    public ResponseEntity<Resource> previewHocLieu(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails ud) {
        try {
            NguoiDung student = getCurrentUser(ud);
            HocLieu hl = hocLieuService.layTheoId(id);

            if (!coQuyenXemHocLieu(hl, student)) {
                return ResponseEntity.status(403).build();
            }

            Resource resource = resolveFile(hl);
            if (resource == null) return ResponseEntity.notFound().build();

            String contentType = hl.getLoaiTep();
            if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "bin";
    }

    /**
     * Tải học liệu xuống — Content-Disposition: attachment.
     */
    @GetMapping("/materials/{id}/download")
    @ResponseBody
    public ResponseEntity<Resource> taiHocLieu(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails ud) {
        try {
            NguoiDung student = getCurrentUser(ud);
            HocLieu hl = hocLieuService.layTheoId(id);

            if (!coQuyenXemHocLieu(hl, student)) {
                return ResponseEntity.status(403).build();
            }

            Resource resource = resolveFile(hl);
            if (resource == null) return ResponseEntity.notFound().build();

            // Tăng lượt tải
            hocLieuService.tangLuotTai(id);

            String contentType = hl.getLoaiTep();
            if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";

            String duongDanTep = hl.getDuongDanTep();
            String tenFile = duongDanTep.replaceFirst("^/uploads/", "");
            String downloadName = hl.getTieuDe() != null
                    ? hl.getTieuDe().replaceAll("[^\\w\\s.-]", "_") + "." + getExtension(tenFile)
                    : tenFile;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + downloadName + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tìm file trên filesystem theo thứ tự ưu tiên:
     * 1. upload.dir (cấu hình trong application.properties)
     * 2. src/main/resources/static/uploads (file đã upload trước khi chỉnh sửa)
     * 3. Các thư mục uploads/ trong project
     */
    private Resource resolveFile(HocLieu hl) throws MalformedURLException {
        String duongDanTep = hl.getDuongDanTep();
        if (duongDanTep == null || duongDanTep.isBlank()) return null;

        String tenFile = duongDanTep.replaceFirst("^/uploads/", "");

        // Danh sách các thư mục để tìm
        String[] searchDirs = {
            uploadDir,                                   // cấu hình hiện tại
            "src/main/resources/static/uploads",         // thư mục source (cũ)
            "../src/main/resources/static/uploads",      // relative từ Webhoctap/
            "uploads",                                   // working dir
        };

        for (String dir : searchDirs) {
            try {
                Path p = Paths.get(dir).toAbsolutePath().normalize().resolve(tenFile);
                Resource r = new UrlResource(p.toUri());
                if (r.exists() && r.isReadable()) return r;
            } catch (Exception ignored) {}
        }
        return null;
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
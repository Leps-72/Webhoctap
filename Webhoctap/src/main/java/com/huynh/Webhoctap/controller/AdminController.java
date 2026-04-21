package com.huynh.Webhoctap.controller;

import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.service.HocLieuService;
import com.huynh.Webhoctap.service.KhoaHocService;
import com.huynh.Webhoctap.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final NguoiDungService nguoiDungService;
    private final KhoaHocService khoaHocService;
    private final HocLieuService hocLieuService;
    private final com.huynh.Webhoctap.service.ActivityService activityService;

    // ── Helper: lấy NguoiDung đang đăng nhập ─────────────────────────────
    private NguoiDung getCurrentUser(UserDetails userDetails) {
        return nguoiDungService.layTheoEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng."));
    }

    // ══════════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        var users = nguoiDungService.layTatCa();
        var coursesAll = khoaHocService.layTatCaChoAdmin();
        var coursesPending = khoaHocService.layChooDuyet();
        var materialsAll = hocLieuService.layTatCaChoAdmin();

        model.addAttribute("tongNguoiDung", users != null ? users.size() : 0);
        model.addAttribute("tongKhoaHoc",   coursesAll != null ? coursesAll.size() : 0);
        model.addAttribute("choDuyet",      coursesPending != null ? coursesPending.size() : 0);
        model.addAttribute("tongHocLieu",   materialsAll != null ? materialsAll.size() : 0);
        model.addAttribute("hoatDongGanDay", activityService.layHoatDongGanDay());
        return "admin/dashboard";
    }

    // ══════════════════════════════════════════════════════════════════════
    // QUẢN LÝ NGƯỜI DÙNG
    // ══════════════════════════════════════════════════════════════════════

    /** Danh sách + tìm kiếm / lọc */
    @GetMapping("/users")
    public String danhSachNguoiDung(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            Model model) {

        Boolean trangThai = null;
        if ("active".equals(status))   trangThai = true;
        if ("locked".equals(status))   trangThai = false;

        var danhSach = nguoiDungService.timKiem(search, role, trangThai);

        model.addAttribute("danhSach", danhSach);
        model.addAttribute("search",   search  != null ? search  : "");
        model.addAttribute("role",     role    != null ? role    : "");
        model.addAttribute("status",   status  != null ? status  : "");
        return "admin/users";
    }

    /** Form thêm người dùng mới */
    @GetMapping("/users/add")
    public String formThemNguoiDung(Model model) {
        model.addAttribute("nguoiDung", new com.huynh.Webhoctap.model.NguoiDung());
        return "admin/user-add";
    }

    /** Xử lý thêm người dùng mới */
    @PostMapping("/users/add")
    public String themNguoiDung(
            @RequestParam String hoTen,
            @RequestParam String email,
            @RequestParam String matKhau,
            @RequestParam String vaiTro,
            @RequestParam(required = false) String soDienThoai,
            RedirectAttributes ra) {
        try {
            com.huynh.Webhoctap.model.NguoiDung nd = new com.huynh.Webhoctap.model.NguoiDung();
            nd.setHoTen(hoTen);
            nd.setEmail(email);
            nd.setMatKhau(matKhau);
            nd.setSoDienThoai(soDienThoai);
            nguoiDungService.themNguoiDung(nd, vaiTro);
            ra.addFlashAttribute("successMsg", "Đã thêm người dùng: " + email);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/users/add";
        }
        return "redirect:/admin/users";
    }

    // Khoá tài khoản
    @PostMapping("/users/{id}/lock")
    public String khoaTaiKhoan(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            nguoiDungService.doiTrangThai(id, false);
            ra.addFlashAttribute("successMsg", "Đã khoá tài khoản.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Mở khoá tài khoản
    @PostMapping("/users/{id}/unlock")
    public String moKhoaTaiKhoan(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            nguoiDungService.doiTrangThai(id, true);
            ra.addFlashAttribute("successMsg", "Đã mở khoá tài khoản.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Đổi vai trò
    @PostMapping("/users/{id}/role")
    public String doiVaiTro(@PathVariable Integer id,
                            @RequestParam String vaiTro,
                            RedirectAttributes ra) {
        try {
            nguoiDungService.doiVaiTro(id, vaiTro);
            ra.addFlashAttribute("successMsg", "Đã cập nhật vai trò.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Xóa người dùng
    @PostMapping("/users/{id}/delete")
    public String xoaNguoiDung(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            nguoiDungService.xoaNguoiDung(id);
            ra.addFlashAttribute("successMsg", "Đã xóa người dùng.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ══════════════════════════════════════════════════════════════════════
    // DUYỆT KHÓA HỌC
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/courses")
    public String danhSachKhoaHoc(Model model) {
        var tatCa = khoaHocService.layTatCaChoAdmin();
        var choDuyet = khoaHocService.layChooDuyet();
        
        model.addAttribute("tatCa",    tatCa != null ? tatCa : java.util.Collections.emptyList());
        model.addAttribute("choDuyet", choDuyet != null ? choDuyet : java.util.Collections.emptyList());
        return "admin/courses";
    }

    @GetMapping("/courses/pending")
    public String danhSachKhoaHocPending(Model model) {
        return danhSachKhoaHoc(model);
    }

    @PostMapping("/courses/{id}/approve")
    public String duyetKhoaHoc(@PathVariable Integer id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes ra) {
        try {
            NguoiDung admin = getCurrentUser(userDetails);
            khoaHocService.duyetKhoaHoc(id, admin);
            ra.addFlashAttribute("successMsg", "Đã duyệt khóa học.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{id}/reject")
    public String tuChoiKhoaHoc(@PathVariable Integer id,
                                @RequestParam String lyDo,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes ra) {
        try {
            NguoiDung admin = getCurrentUser(userDetails);
            khoaHocService.tuChoiKhoaHoc(id, admin, lyDo);
            ra.addFlashAttribute("successMsg", "Đã từ chối khóa học.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // ══════════════════════════════════════════════════════════════════════
    // DUYỆT HỌC LIỆU
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/materials")
    public String danhSachHocLieu(Model model) {
        var choDuyet = hocLieuService.layChoDuyet();
        var tatCa = hocLieuService.layTatCaChoAdmin();

        model.addAttribute("choDuyet", choDuyet != null ? choDuyet : java.util.Collections.emptyList());
        model.addAttribute("tatCa",    tatCa != null ? tatCa : java.util.Collections.emptyList());
        return "admin/materials";
    }

    @PostMapping("/materials/{id}/approve")
    public String duyetHocLieu(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            hocLieuService.duyetHocLieu(id);
            ra.addFlashAttribute("successMsg", "Đã duyệt học liệu.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/materials";
    }

    @PostMapping("/materials/{id}/reject")
    public String tuChoiHocLieu(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            hocLieuService.tuChoiHocLieu(id);
            ra.addFlashAttribute("successMsg", "Đã từ chối học liệu.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/materials";
    }
}
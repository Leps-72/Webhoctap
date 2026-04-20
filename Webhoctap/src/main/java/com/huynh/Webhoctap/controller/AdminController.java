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
        model.addAttribute("tongNguoiDung", nguoiDungService.layTatCa().size());
        model.addAttribute("tongKhoaHoc",   khoaHocService.layTatCaChoAdmin().size());
        model.addAttribute("choDuyet",      khoaHocService.layChooDuyet().size());
        model.addAttribute("tongHocLieu",   hocLieuService.layTatCaChoAdmin().size());
        return "admin/dashboard";
    }

    // ══════════════════════════════════════════════════════════════════════
    // QUẢN LÝ NGƯỜI DÙNG
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/users")
    public String danhSachNguoiDung(Model model) {
        model.addAttribute("danhSach", nguoiDungService.layTatCa());
        return "admin/users";
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

    // ══════════════════════════════════════════════════════════════════════
    // DUYỆT KHÓA HỌC
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/courses")
    public String danhSachKhoaHoc(Model model) {
        model.addAttribute("tatCa",    khoaHocService.layTatCaChoAdmin());
        model.addAttribute("choDuyet", khoaHocService.layChooDuyet());
        return "admin/courses";
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
        model.addAttribute("choDuyet", hocLieuService.layChoDuyet());
        model.addAttribute("tatCa",    hocLieuService.layTatCaChoAdmin());
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
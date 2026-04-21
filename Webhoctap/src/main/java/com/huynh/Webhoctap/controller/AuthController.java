package com.huynh.Webhoctap.controller;

import com.huynh.Webhoctap.dto.RegisterDto;
import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final NguoiDungService nguoiDungService;

    // ── GET /login ────────────────────────────────────────────────────────
    @GetMapping("/login")
    public String trangDangNhap(@RequestParam(value = "error",  required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails ud,
                                Model model) {
        if (ud != null) return "redirect:/dashboard";
        if (error  != null) model.addAttribute("errorMsg",  "Email hoặc mật khẩu không đúng.");
        if (logout != null) model.addAttribute("logoutMsg", "Bạn đã đăng xuất thành công.");
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboardRouter(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails ud) {
        if (ud == null) return "redirect:/login";
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));
        boolean isTeacher = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Teacher"));
        if (isAdmin) return "redirect:/admin/dashboard";
        if (isTeacher) return "redirect:/teacher/dashboard";
        return "redirect:/student/dashboard";
    }

    // ── GET /register ─────────────────────────────────────────────────────
    @GetMapping("/register")
    public String trangDangKy(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    // ── POST /register ────────────────────────────────────────────────────
    @PostMapping("/register")
    public String xuLyDangKy(@Valid @ModelAttribute("registerDto") RegisterDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        if (!dto.isMatKhauKhop()) {
            model.addAttribute("errorMsg", "Mật khẩu xác nhận không khớp.");
            return "auth/register";
        }
        try {
            NguoiDung nguoiDung = new NguoiDung();
            nguoiDung.setHoTen(dto.getHoTen());
            nguoiDung.setEmail(dto.getEmail());
            nguoiDung.setMatKhau(dto.getMatKhau());

            String tenVaiTro = "Teacher".equals(dto.getVaiTro()) ? "Teacher" : "Student";
            nguoiDungService.dangKy(nguoiDung, tenVaiTro);

            redirectAttributes.addFlashAttribute("successMsg", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/register";
        }
    }
}
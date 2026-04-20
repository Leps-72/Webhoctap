package com.huynh.Webhoctap.controller;

import com.huynh.Webhoctap.service.HocLieuService;
import com.huynh.Webhoctap.service.KhoaHocService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final KhoaHocService khoaHocService;
    private final HocLieuService hocLieuService;

    // ── GET / ─────────────────────────────────────────────────────────────
    @GetMapping("/")
    public String trangChu(Model model) {
        // Chỉ hiển thị khóa học đã duyệt và đang active
        model.addAttribute("khoaHocs", khoaHocService.layDaDuyetVaHienThi());
        // Chỉ hiển thị học liệu đã duyệt
        model.addAttribute("hocLieus", hocLieuService.layDaDuyet());
        return "home";
    }
}
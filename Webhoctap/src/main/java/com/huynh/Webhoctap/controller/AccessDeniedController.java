package com.huynh.Webhoctap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    // Spring Security redirect đến đây khi user đăng nhập nhưng không đủ quyền (403)
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
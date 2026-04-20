package com.huynh.Webhoctap.security;

import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    /**
     * Spring Security gọi method này mỗi khi cần xác thực.
     * Username ở đây là Email của người dùng.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email));

        // Tài khoản bị khoá → Spring Security tự từ chối đăng nhập
        if (!nguoiDung.getTrangThai()) {
            throw new UsernameNotFoundException("Tài khoản đã bị khoá: " + email);
        }

        // TenVaiTro trong DB: "Admin" | "Teacher" | "Student"
        // Spring Security yêu cầu prefix ROLE_  →  "ROLE_Admin", "ROLE_Teacher", "ROLE_Student"
        String role = "ROLE_" + nguoiDung.getVaiTro().getTenVaiTro();

        // Cập nhật thời điểm đăng nhập cuối (không block luồng auth nếu lỗi)
        try {
            nguoiDung.setLanDangNhapCuoi(LocalDateTime.now());
            nguoiDungRepository.save(nguoiDung);
        } catch (Exception ignored) {}

        return User.builder()
                .username(nguoiDung.getEmail())
                .password(nguoiDung.getMatKhau())   // đã BCrypt từ lúc đăng ký
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .accountLocked(!nguoiDung.getTrangThai())
                .build();
    }
}
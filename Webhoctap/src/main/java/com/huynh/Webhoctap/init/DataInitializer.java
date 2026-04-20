package com.huynh.Webhoctap.init;

import com.huynh.Webhoctap.model.NguoiDung;
import com.huynh.Webhoctap.model.VaiTro;
import com.huynh.Webhoctap.repository.NguoiDungRepository;
import com.huynh.Webhoctap.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initializeData() {
        return args -> {
            // Khởi tạo vai trò nếu chưa tồn tại
            if (vaiTroRepository.findByTenVaiTro("Admin").isEmpty()) {
                VaiTro admin = new VaiTro();
                admin.setTenVaiTro("Admin");
                vaiTroRepository.save(admin);
            }
            if (vaiTroRepository.findByTenVaiTro("Teacher").isEmpty()) {
                VaiTro teacher = new VaiTro();
                teacher.setTenVaiTro("Teacher");
                vaiTroRepository.save(teacher);
            }
            if (vaiTroRepository.findByTenVaiTro("Student").isEmpty()) {
                VaiTro student = new VaiTro();
                student.setTenVaiTro("Student");
                vaiTroRepository.save(student);
            }

            // Khởi tạo tài khoản demo nếu chưa tồn tại
            createDemoUserIfNotExists("admin@example.com", "Quản Trị Viên", "Admin", "0912345678");
            createDemoUserIfNotExists("teacher@example.com", "Giáo Viên Demo", "Teacher", "0912345679");
            createDemoUserIfNotExists("student@example.com", "Học Viên Demo", "Student", "0912345680");
        };
    }

    private void createDemoUserIfNotExists(String email, String hoTen, String roleCode, String phone) {
        if (!nguoiDungRepository.existsByEmail(email)) {
            VaiTro role = vaiTroRepository.findByTenVaiTro(roleCode)
                    .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại: " + roleCode));

            NguoiDung user = new NguoiDung();
            user.setEmail(email);
            user.setHoTen(hoTen);
            user.setMatKhau(passwordEncoder.encode("password123"));
            user.setSoDienThoai(phone);
            user.setGioiTinh("Nam");
            user.setNgaySinh(LocalDate.of(2000, 1, 1));
            user.setVaiTro(role);
            user.setTrangThai(true);
            user.setNgayTao(LocalDateTime.now());

            nguoiDungRepository.save(user);
            System.out.println("✅ Tạo tài khoản: " + email);
        }
    }
}

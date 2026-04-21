package com.huynh.Webhoctap.service;

import com.huynh.Webhoctap.dto.AdminActivityDto;
import com.huynh.Webhoctap.repository.DangKyKhoaHocRepository;
import com.huynh.Webhoctap.repository.KhoaHocRepository;
import com.huynh.Webhoctap.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final NguoiDungRepository nguoiDungRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final DangKyKhoaHocRepository dangKyRepository;

    public List<AdminActivityDto> layHoatDongGanDay() {
        List<AdminActivityDto> activities = new ArrayList<>();

        // 1. New users
        nguoiDungRepository.findTop5ByOrderByNgayTaoDesc().forEach(u -> {
            activities.add(new AdminActivityDto(
                u.getHoTen(),
                "Tham gia hệ thống",
                u.getNgayTao(),
                "bg-info"
            ));
        });

        // 2. New courses
        khoaHocRepository.findTop5ByOrderByNgayTaoDesc().forEach(k -> {
            activities.add(new AdminActivityDto(
                k.getGiaoVien().getHoTen(),
                "Tạo khóa học mới: " + k.getTenKhoaHoc(),
                k.getNgayTao(),
                "bg-success"
            ));
        });

        // 3. New registrations
        dangKyRepository.findTop5ByOrderByNgayDangKyDesc().forEach(d -> {
            activities.add(new AdminActivityDto(
                d.getHocSinh().getHoTen(),
                "Đăng ký khóa học: " + d.getKhoaHoc().getTenKhoaHoc(),
                d.getNgayDangKy(),
                "bg-primary"
            ));
        });

        // Sort by time desc and take top 10
        return activities.stream()
                .filter(a -> a.getThoiGian() != null)
                .sorted(Comparator.comparing(AdminActivityDto::getThoiGian).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}

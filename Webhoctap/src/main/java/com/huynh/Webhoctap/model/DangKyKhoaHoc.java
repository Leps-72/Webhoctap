package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DangKyKhoaHoc")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DangKyKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDangKy")
    private Integer maDangKy;

    // FK -> KhoaHoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhoaHoc", referencedColumnName = "MaKhoaHoc", nullable = false)
    private KhoaHoc khoaHoc;

    // FK -> NguoiDung (học sinh đăng ký)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocSinh", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung hocSinh;

    @Column(name = "NgayDangKy", nullable = false)
    private LocalDateTime ngayDangKy;

    // "DangHoc" | "HoanThanh" | "HuyDangKy"
    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai = "DangHoc";

    @PrePersist
    public void prePersist() {
        if (ngayDangKy == null) ngayDangKy = LocalDateTime.now();
    }
}
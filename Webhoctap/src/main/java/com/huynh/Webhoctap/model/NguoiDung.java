package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "NguoiDung")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNguoiDung")
    private Integer maNguoiDung;

    @Column(name = "HoTen", length = 100)
    private String hoTen;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "MatKhau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "AnhDaiDien", length = 255)
    private String anhDaiDien;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    // FK -> VaiTro
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaVaiTro", referencedColumnName = "MaVaiTro")
    private VaiTro vaiTro;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "LanDangNhapCuoi")
    private LocalDateTime lanDangNhapCuoi;

    @Column(name = "TrangThai")
    private Boolean trangThai = true;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
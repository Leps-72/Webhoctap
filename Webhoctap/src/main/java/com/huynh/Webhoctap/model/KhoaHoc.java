package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhoaHoc")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKhoaHoc")
    private Integer maKhoaHoc;

    @Column(name = "TenKhoaHoc", nullable = false, length = 200)
    private String tenKhoaHoc;

    @Column(name = "Slug", length = 200)
    private String slug;

    @Column(name = "MoTa", length = 1000)
    private String moTa;

    @Column(name = "AnhBia", length = 500)
    private String anhBia;

    // FK -> NguoiDung (giáo viên tạo khóa học)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiaoVien", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung giaoVien;

    // "Draft" | "PendingReview" | "Approved" | "Rejected"
    @Column(name = "TrangThaiDuyet", nullable = false, length = 20)
    private String trangThaiDuyet = "Draft";

    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NgayGuiDuyet")
    private LocalDateTime ngayGuiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    // FK -> NguoiDung (admin/nhân viên duyệt)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyetId", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiDuyet;

    @Column(name = "LyDoTuChoi", length = 500)
    private String lyDoTuChoi;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
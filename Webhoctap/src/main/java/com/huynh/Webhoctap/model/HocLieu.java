package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HocLieu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class HocLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHocLieu")
    private Integer maHocLieu;

    @Column(name = "TieuDe", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "MoTa", length = 500)
    private String moTa;

    @Column(name = "DuongDanTep", length = 255)
    private String duongDanTep;

    @Column(name = "LoaiTep", length = 50)
    private String loaiTep;

    @Column(name = "KichThuocTep")
    private Long kichThuocTep;

    // "De" | "TrungBinh" | "Kho"
    @Column(name = "DoKho", length = 20)
    private String doKho;

    @Column(name = "NgayDang")
    private LocalDateTime ngayDang;

    // FK -> ChuDe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChuDe", referencedColumnName = "MaChuDe")
    private ChuDe chuDe;

    // FK -> NguoiDung (người đăng tải)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiDung;

    // FK -> MonHoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaMonHoc", referencedColumnName = "MaMonHoc")
    private MonHoc monHoc;

    // FK -> LopHoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHoc", referencedColumnName = "MaLopHoc")
    private LopHoc lopHoc;

    // FK -> KhoaHoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhoaHoc", referencedColumnName = "MaKhoaHoc")
    private KhoaHoc khoaHoc;

    @Column(name = "DaDuyet")
    private Boolean daDuyet = false;

    // "ChoДуyet" | "DaDuyet" | "TuChoi"
    @Column(name = "TrangThaiDuyet", length = 20)
    private String trangThaiDuyet = "ChoDuyet";

    @Column(name = "LuotXem")
    private Integer luotXem = 0;

    @Column(name = "SoLuotTai")
    private Integer soLuotTai = 0;

    @PrePersist
    public void prePersist() {
        if (ngayDang == null) ngayDang = LocalDateTime.now();
    }
}
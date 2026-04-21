package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "CauHoi")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CauHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCauHoi")
    private Integer maCauHoi;

    // FK -> Quiz (nullable: câu hỏi ngân hàng không gắn quiz)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaQuiz", referencedColumnName = "MaQuiz")
    private Quiz quiz;

    @Column(name = "NoiDung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    // "TracNghiem" | "DungSai"
    @Column(name = "LoaiCauHoi", nullable = false, length = 50)
    private String loaiCauHoi;

    @Column(name = "Diem")
    private Double diem = 1.0;

    // Chủ đề / môn học (dùng cho ngân hàng)
    @Column(name = "ChuDe", length = 100)
    private String chuDe;

    // "De" | "TrungBinh" | "Kho"
    @Column(name = "DoKho", length = 20)
    private String doKho = "TrungBinh";

    // "Quiz" | "NganHang"
    @Column(name = "NguonGoc", length = 20)
    private String nguonGoc = "Quiz";

    // FK -> NguoiDung (giáo viên sở hữu câu hỏi ngân hàng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiaoVienNH", referencedColumnName = "MaNguoiDung")
    private NguoiDung giaoVienNganHang;

    // Eager load lựa chọn cùng với câu hỏi (cần khi hiển thị quiz)
    @OneToMany(mappedBy = "cauHoi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LuaChon> luaChons;
}
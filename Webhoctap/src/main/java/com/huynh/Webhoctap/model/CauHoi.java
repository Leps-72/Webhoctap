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

    // FK -> Quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaQuiz", referencedColumnName = "MaQuiz")
    private Quiz quiz;

    @Column(name = "NoiDung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    // "TracNghiem" | "DungSai" | ...
    @Column(name = "LoaiCauHoi", nullable = false, length = 50)
    private String loaiCauHoi;

    @Column(name = "Diem")
    private Double diem = 1.0;

    // Eager load lựa chọn cùng với câu hỏi (cần khi hiển thị quiz)
    @OneToMany(mappedBy = "cauHoi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LuaChon> luaChons;
}
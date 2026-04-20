package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KetQuaQuiz")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KetQuaQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKetQua")
    private Integer maKetQua;

    // FK -> Quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaQuiz", referencedColumnName = "MaQuiz")
    private Quiz quiz;

    // FK -> NguoiDung (học sinh làm bài)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocSinh", referencedColumnName = "MaNguoiDung")
    private NguoiDung hocSinh;

    @Column(name = "Diem")
    private Double diem;

    @Column(name = "ThoiGianBatDau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "ThoiGianKetThuc")
    private LocalDateTime thoiGianKetThuc;
}
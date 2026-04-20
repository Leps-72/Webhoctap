package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Quiz")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaQuiz")
    private Integer maQuiz;

    @Column(name = "TenQuiz", nullable = false, length = 200)
    private String tenQuiz;

    // FK -> KhoaHoc (nullable - quiz có thể không gắn với khóa học cụ thể)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhoaHoc", referencedColumnName = "MaKhoaHoc")
    private KhoaHoc khoaHoc;

    // FK -> NguoiDung (giáo viên tạo quiz)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiaoVien", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung giaoVien;

    // Thời gian làm bài tính theo phút
    @Column(name = "ThoiGianLamBai")
    private Integer thoiGianLamBai;

    // "Draft" | "Published"
    @Column(name = "TrangThai", length = 20)
    private String trangThai = "Draft";
}
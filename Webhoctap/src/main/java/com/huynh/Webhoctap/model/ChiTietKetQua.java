package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ChiTietKetQua")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChiTietKetQua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChiTiet")
    private Integer maChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKetQua", referencedColumnName = "MaKetQua", nullable = false)
    private KetQuaQuiz ketQuaQuiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCauHoi", referencedColumnName = "MaCauHoi", nullable = false)
    private CauHoi cauHoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLuaChonChon", referencedColumnName = "MaLuaChon")
    private LuaChon luaChonChon; // Có thể null nếu học sinh không chọn đáp án nào
}

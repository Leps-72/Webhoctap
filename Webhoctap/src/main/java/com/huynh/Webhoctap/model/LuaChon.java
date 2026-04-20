package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LuaChon")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LuaChon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLuaChon")
    private Integer maLuaChon;

    // FK -> CauHoi (ON DELETE CASCADE đã set ở DB)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCauHoi", referencedColumnName = "MaCauHoi")
    private CauHoi cauHoi;

    @Column(name = "NoiDung", nullable = false, length = 500)
    private String noiDung;

    @Column(name = "LaDapAnDung")
    private Boolean laDapAnDung = false;
}
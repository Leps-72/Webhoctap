package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ChuDe")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChuDe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChuDe")
    private Integer maChuDe;

    @Column(name = "TenChuDe", nullable = false, length = 100)
    private String tenChuDe;

    @Column(name = "MoTa", length = 255)
    private String moTa;
}
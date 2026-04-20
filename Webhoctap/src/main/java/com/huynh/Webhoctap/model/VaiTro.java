package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VaiTro")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaVaiTro")
    private Integer maVaiTro;

    @Column(name = "TenVaiTro", nullable = false, length = 50)
    private String tenVaiTro;
}
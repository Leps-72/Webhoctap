package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LopHoc")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LopHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLopHoc")
    private Integer maLopHoc;

    @Column(name = "TenLopHoc", nullable = false, length = 50)
    private String tenLopHoc;
}
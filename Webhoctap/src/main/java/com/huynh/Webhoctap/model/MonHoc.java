package com.huynh.Webhoctap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MonHoc")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MonHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaMonHoc")
    private Integer maMonHoc;

    @Column(name = "TenMonHoc", nullable = false, length = 100)
    private String tenMonHoc;
}
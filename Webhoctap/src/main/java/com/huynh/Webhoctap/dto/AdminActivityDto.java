package com.huynh.Webhoctap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminActivityDto {
    private String nguoiDung;
    private String hanhDong;
    private LocalDateTime thoiGian;
    private String badgeClass;
}

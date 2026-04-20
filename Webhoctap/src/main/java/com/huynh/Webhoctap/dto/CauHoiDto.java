package com.huynh.Webhoctap.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CauHoiDto {

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String noiDung;

    // "TracNghiem" | "DungSai"
    @NotBlank(message = "Loại câu hỏi không được để trống")
    private String loaiCauHoi;

    @DecimalMin(value = "0.0", inclusive = false, message = "Điểm phải lớn hơn 0")
    private Double diem = 1.0;

    // Danh sách lựa chọn gửi kèm — tối thiểu 2 lựa chọn
    @Size(min = 2, message = "Câu hỏi phải có ít nhất 2 lựa chọn")
    private List<LuaChonDto> luaChons = new ArrayList<>();

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class LuaChonDto {

        @NotBlank(message = "Nội dung lựa chọn không được để trống")
        @Size(max = 500, message = "Nội dung lựa chọn tối đa 500 ký tự")
        private String noiDung;

        private Boolean laDapAnDung = false;
    }
}

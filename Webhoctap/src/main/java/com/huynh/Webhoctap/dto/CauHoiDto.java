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

    @Positive(message = "Điểm phải lớn hơn 0")
    private Double diem = 1.0;

    // Phân loại (dùng cho ngân hàng câu hỏi)
    private String chuDe;

    // "De" | "TrungBinh" | "Kho"
    private String doKho = "TrungBinh";

    // Danh sách lựa chọn gửi kèm
    private List<LuaChonDto> luaChons = new ArrayList<>();

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class LuaChonDto {
        @Size(max = 500, message = "Nội dung lựa chọn tối đa 500 ký tự")
        private String noiDung;

        private Boolean laDapAnDung = false;
    }
}

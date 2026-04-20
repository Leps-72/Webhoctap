package com.huynh.Webhoctap.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KhoaHocDto {

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 200, message = "Tên khóa học tối đa 200 ký tự")
    private String tenKhoaHoc;

    @Size(max = 200, message = "Slug tối đa 200 ký tự")
    private String slug;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String moTa;

    @Size(max = 500, message = "Đường dẫn ảnh bìa tối đa 500 ký tự")
    private String anhBia;
}

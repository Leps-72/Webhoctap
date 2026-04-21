package com.huynh.Webhoctap.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class HocLieuDto {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
    private String tieuDe;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;

    // "Dễ" | "Trung bình" | "Khó"
    private String doKho;

    // FK — ID chủ đề, môn học, lớp học (chọn từ dropdown)
    private Integer maChuDe;
    private Integer maMonHoc;
    private Integer maLopHoc;
    private Integer maKhoaHoc;

    // File thực tế — không validate @NotNull vì có thể cập nhật không kèm file mới
    private MultipartFile file;

    public boolean hasFile() {
        return file != null && !file.isEmpty();
    }
}

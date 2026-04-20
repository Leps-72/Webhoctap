package com.huynh.Webhoctap.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class QuizDto {

    @NotBlank(message = "Tên quiz không được để trống")
    @Size(max = 200, message = "Tên quiz tối đa 200 ký tự")
    private String tenQuiz;

    // FK — có thể null nếu quiz không gắn với khóa học cụ thể
    private Integer maKhoaHoc;

    @Min(value = 1, message = "Thời gian làm bài phải ít nhất 1 phút")
    @Max(value = 300, message = "Thời gian làm bài tối đa 300 phút")
    private Integer thoiGianLamBai;
}

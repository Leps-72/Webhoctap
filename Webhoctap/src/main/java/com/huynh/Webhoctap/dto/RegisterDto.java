package com.huynh.Webhoctap.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String matKhau;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String xacNhanMatKhau;

    // "Student" | "Teacher" — không cho tự chọn Admin từ form
    private String vaiTro = "Student";

    public boolean isMatKhauKhop() {
        return matKhau != null && matKhau.equals(xacNhanMatKhau);
    }
}

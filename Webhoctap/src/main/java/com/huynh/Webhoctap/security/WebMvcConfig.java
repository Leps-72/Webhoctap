package com.huynh.Webhoctap.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Đăng ký resource handler để phục vụ file upload từ thư mục filesystem
 * thay vì chỉ từ classpath static.
 * <p>
 * Điều này giải quyết: NoResourceFoundException cho /uploads/**
 * vì file được lưu trên đĩa nhưng Spring Boot static handler
 * chỉ tìm trong classpath.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve đường dẫn tuyệt đối từ uploadDir (relative hoặc absolute)
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString().replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                // Fallback: tìm trong classpath static (cho file cũ đã upload vào static/)
                .addResourceLocations("classpath:/static/uploads/");
    }
}

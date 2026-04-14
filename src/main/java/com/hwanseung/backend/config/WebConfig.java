package com.hwanseung.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
//    @Value("${custom.upload-path}")
//    private String filePath = "file:///C:/bImg/";
    @Value("${custom.upload-path}")
    private String filePath;

    @Override  // 리소스 외부 경로 맵핑 설정
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String os = System.getProperty("os.name").toLowerCase();
//        if (!os.contains("win")) {
//            filePath = "/Users/ikarosala/Documents/bImg/";
//        }

//        registry.addResourceHandler("/api/imgs/**") // 웹에서 접근할 경로
//                .addResourceLocations("file:///" +filePath); // 실제 파일이 있는 위치
//        registry.addResourceHandler("/api/download/**")
//                .addResourceLocations("file:///" +filePath);
        // 🌟 수정 포인트 1: "file:///" 대신 "file:" 사용
        registry.addResourceHandler("/api/imgs/**")
                .addResourceLocations("file:///" + filePath);

        registry.addResourceHandler("/api/download/**")
                .addResourceLocations("file:///" + filePath);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost",
                        "http://127.0.0.1",
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://localhost:5173",
                        "http://127.0.0.1:5173"
                )
                .allowCredentials(true) // 중요!
                .allowedMethods("*");
//                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
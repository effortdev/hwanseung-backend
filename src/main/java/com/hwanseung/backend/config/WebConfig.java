package com.hwanseung.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private String filePath = "file:///C:/bImg/";

    @Override  // 리소스 외부 경로 맵핑 설정
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name").toLowerCase();
        if(!os.contains("win")){
            filePath ="/Users/ikarosala/Documents/bImg/";
        }

        registry.addResourceHandler("/api/imgs/**") // 웹에서 접근할 경로
                .addResourceLocations(filePath); // 실제 파일이 있는 위치
        registry.addResourceHandler("/api/download/**")
                .addResourceLocations(filePath);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
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

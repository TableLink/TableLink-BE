package com.est.tablelink.global.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트에 대해 CORS를 허용합니다.
                .allowedOrigins("http://localhost:8081") // 허용할 origin을 설정합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드를 설정합니다.
                .allowedHeaders("*") // 허용할 헤더를 설정합니다.
                .allowCredentials(true); // 자격 증명을 허용합니다.
    }
}

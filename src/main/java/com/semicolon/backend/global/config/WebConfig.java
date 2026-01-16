package com.semicolon.backend.global.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${com.semicolon.backend.upload}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://www.jeocenter.shop", "https://jeocenter.shop",
                        "https://public-sports-facility.vercel.app",
                        "https://public-sports-facility-git-main-lee-kunhos-projects.vercel.app",
                        "https://public-sports-facility-pna1v7t17-lee-kunhos-projects.vercel.app"
                )
                .allowedMethods("HEAD","GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*") // 모든 헤더 허용
                .maxAge(300)
                .allowCredentials(true); // 쿠키 및 인증 정보(JWT 등) 전송 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // file: 접두어를 붙여서 파일 시스템 경로임을 명시
        String rootPath = "file:" + uploadDir + "/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(rootPath);

        // 하드코딩 된 경로를 변수로 변경
        registry.addResourceHandler("/download/**")
                .addResourceLocations(
                        rootPath + "notice/",
                        rootPath + "bank/",
                        rootPath + "cert/",
                        rootPath + "resume/"
                )
                .setCachePeriod(3600);
    }

    @Bean
    public Filter downloadHeaderFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;

                String uri = req.getRequestURI();
                if (uri.startsWith("/download/")) {
                    String filename = Paths.get(uri).getFileName().toString();
                    res.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                }

                chain.doFilter(request, response);
            }
        };
    }
}
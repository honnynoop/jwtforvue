package com.ssafy.edu.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload.directory}")
    private String uploadDirectory;
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }
  

	/*
	 * @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 * registry.addResourceHandler("/uploads/**") .addResourceLocations("file:" +
	 * uploadDirectory + "/"); }
	 */
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("multipart", MediaType.MULTIPART_FORM_DATA);
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  
                        .allowedOrigins("http://localhost:5500","http://127.0.0.1:5500")
                        .maxAge(1800)
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
    /*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(20);
    }
    */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry//.addResourceHandler("**")  // URL 패턴
        		.addResourceHandler("/image/**")  // URL 패턴
                .addResourceLocations("file:" + uploadDirectory + "/")  // 실제 파일 경로
                .setCachePeriod(3600)  // 캐시 주기 설정 (선택사항)
                .resourceChain(true);  // 리소스 체인 활성화 (선택사항)
     // 정적 리소스를 위한 설정
        registry.addResourceHandler("/**")
                .addResourceLocations(
                    "classpath:/static/",
                    "classpath:/public/",
                    "classpath:/resources/",
                    "classpath:/META-INF/resources/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}

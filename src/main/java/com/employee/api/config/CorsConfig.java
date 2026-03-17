package com.employee.api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
// CORS(Cross-Origin Resource Sharing): SOP(Same-Origin Policy) 정책을 우회하기 위한 설정, 보안상 주의 필요
// 사용 이유 : 프론트엔드와 백엔드가 다른 도메인에서 운영될 때, 브라우저가 CORS 정책에 의해 API 요청을 차단하는 것을 방지하기 위해 사용
@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<?> corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 명시적인 도메인만 허용
        //configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:80","http://localhost"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        // Credentials은 필요한 경우에만
        configuration.setAllowCredentials(true);
        // 필요한 헤더만 허용
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        // 필요한 HTTP 메소드만 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 특정 경로에만 적용 (예: "/api/**")
        source.registerCorsConfiguration("/api/**", configuration);

        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

}
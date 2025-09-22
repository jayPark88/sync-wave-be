package com.parker.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * com.parker.admin.config
 * ㄴ CorsConfig
 *
 * <pre>
 * description : cors설정 /api로 시작되는 모든 부분에 대해서 허용하는 설정 안그러면 도메인 다르면 안됨(필수는 아님)
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 12/24/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */
@Configuration
@Slf4j
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 모든 Origin 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
        return new CorsFilter(source);
    }
}
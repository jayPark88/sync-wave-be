package com.parker.service.config;

import com.parker.common.jwt.JwtAccessDeniedHandler;
import com.parker.common.jwt.JwtAuthenticationEntryPoint;
import com.parker.common.jwt.JwtSecurityConfig;
import com.parker.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * com.parker.admin.config
 * ㄴ SecurityConfig
 *
 * <pre>
 * description : spring security 설정
 * 참조: https://devlog-wjdrbs96.tistory.com/434
 * WebSecurityConfigurerAdapter Deprecated 되서  SecurityFilterChain를 bean으로 등록해서 사용하라고 공식 가이드
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 11/5/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;

    //암호화 기능
    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)// UsernamePasswordAuthenticationFilter 앞에 corsFilter가 먼저
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                                .requestMatchers(new MvcRequestMatcher(introspector, "/v1/auth/**")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/v1/user/signUp")).permitAll()
                                .requestMatchers(new MvcRequestMatcher(introspector, "/v1/notices")).permitAll() // 공지사항 조회는 모든 사용자 허용
                                .requestMatchers(new MvcRequestMatcher(introspector, "/v1/notices/**")).permitAll() // 공지사항 상세 조회도 허용
                                .anyRequest().authenticated()
                )
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .with(new JwtSecurityConfig(tokenProvider), JwtSecurityConfig::getClass);// JwtSecurityConfig::getClass는 이 설정이 어떤 클래스인지 알려주는 것입니다. 쉽게 말해서, "이 설정은 JwtSecurityConfig 클래스에서 온 거야"라고 알려주는 역할을 합니다.

        return http.build();
    }
}
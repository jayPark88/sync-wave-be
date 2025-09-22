package com.parker.common.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * com.parker.admin.jwt.config
 * ㄴ JwtSecurityConfig
 *
 * <pre>
 * JwtSecurityConfig를 사용하는 방식은 구성(config) 클래스를 통해 세부적인 설정들을 모듈화하고 분리할 수 있어 관리하기 쉽다는 장점이 있습니다.
 * (https://blog.naver.com/jihoons88/223314673697)
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
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;

    @Override
    public void configure(HttpSecurity httpSecurity) {
        httpSecurity.addFilterBefore(
                new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class
        );
    }

}
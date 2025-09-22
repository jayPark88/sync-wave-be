package com.parker.common.service;


import com.parker.common.enums.UserStatus;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_403;

/**
 * com.parker.admin.service
 * ㄴ UserDetailService
 *
 * <pre>
 * description :사용자 인증을 위한 사용자 상세 정보를 제공하는 서비스 클래스. Spring Security의 UserDetailsService 인터페이스를 구현함.
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 12/25/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */

@RequiredArgsConstructor
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    /**
     * 사용자명을 기반으로 사용자 정보를 로드하는 메서드.
     *
     * @param userId 로그인 시 입력한 사용자명
     * @return UserDetails 객체 (Spring Security에서 사용자 정보를 나타내는 인터페이스)
     * @throws UsernameNotFoundException 지정된 사용자명을 찾을 수 없을 때 발생하는 예외
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByEmail(userId)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(userId + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    /**
     * 주어진 사용자 정보를 기반으로 UserDetails 객체를 생성하는 메서드.
     *
     * @param userEntity 사용자 엔터티 객체
     * @return UserDetails 객체
     */
    private User createUser(UserEntity userEntity) {
        if (!userEntity.getStatus().equals(UserStatus.ACTIVATED.code())) {
            throw new CustomException(FAIL_403.code(), messageSource.getMessage("http.status.forbidden", null, Locale.getDefault()), HttpStatus.FORBIDDEN);
        }

        // 사용자의 권한 정보를 GrantedAuthority로 변환하여 리스트로 저장
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(userEntity.getRole()));

        // UserDetails 객체를 생성하여 반환
        return new User(
                userEntity.getEmail()
                , userEntity.getPassword()
                , grantedAuthorities
        );
    }
}
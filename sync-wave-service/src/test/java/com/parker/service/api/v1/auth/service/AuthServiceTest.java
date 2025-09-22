package com.parker.service.api.v1.auth.service;

import com.parker.common.dto.TokenDto;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.PasswordResetTokenEntity;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.PasswordResetTokenRepository;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.jwt.TokenProvider;
import com.parker.common.resonse.CommonResponse;
import com.parker.common.service.PasswordResetService;
import com.parker.service.api.v1.auth.dto.LoginDto;
import com.parker.service.api.v1.auth.dto.PasswordResetRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageSource messageSource;
    @Mock
    private PasswordResetService passwordResetService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    // ===========================================
    // 🔐 로그인 테스트
    // ===========================================
    
    @Test
    void 로그인_성공_토큰반환() {
        // given - 테스트 데이터 준비
        LoginDto loginDto = LoginDto.builder()
                .userId("parker@test.com")
                .password("password123")
                .build();
        
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        
        // Mock 객체 생성
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        Authentication mockAuth = mock(Authentication.class);
        
        // Mock 설정
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthManager);
        when(mockAuthManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(tokenProvider.createToken(mockAuth)).thenReturn(expectedToken);

        // when - 테스트 실행
        CommonResponse<TokenDto> result = authService.authorize(loginDto);

        // then - 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getToken()).isEqualTo(expectedToken);
        
        // Mock 호출 검증
        verify(authenticationManagerBuilder).getObject();
        verify(mockAuthManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).createToken(mockAuth);
    }

    // ===========================================
    // 📧 비밀번호 리셋 이메일 요청 테스트
    // ===========================================
    
    @Test
    void 비밀번호리셋이메일요청_성공() {
        // given - 테스트 데이터 준비
        String email = "parker@test.com";
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .userName("Parker")
                .build();
        
        PasswordResetTokenEntity existingToken = PasswordResetTokenEntity.builder()
                .email(email)
                .used(false)
                .build();
        
        // Mock 설정
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordResetTokenRepository.findByEmail(email)).thenReturn(Arrays.asList(existingToken));
        when(passwordResetTokenRepository.save(any(PasswordResetTokenEntity.class))).thenReturn(existingToken);

        // when - 테스트 실행
        authService.passwordResetEmailRequest(email);

        // then - 결과 검증
        verify(userRepository).findByEmail(email);
        verify(passwordResetTokenRepository).findByEmail(email);
        verify(passwordResetTokenRepository).save(existingToken);
        verify(passwordResetService).sendResetEmail(email);
    }
    
    @Test
    void 비밀번호리셋이메일요청_사용자없음_예외발생() {
        // given - 테스트 데이터 준비
        String email = "nonexistent@test.com";
        
        // Mock 설정
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("사용자를 찾을 수 없습니다");

        // when & then - 테스트 실행 및 예외 검증
        assertThatThrownBy(() -> authService.passwordResetEmailRequest(email))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
        
        verify(userRepository).findByEmail(email);
        verify(passwordResetService, never()).sendResetEmail(anyString());
    }

    // ===========================================
    // 🔑 비밀번호 리셋 테스트
    // ===========================================
    
    @Test
    void 비밀번호리셋_성공() {
        // given - 테스트 데이터 준비
        String token = "valid-reset-token";
        String email = "parker@test.com";
        String newPassword = "newPassword123";
        
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
        requestDto.setToken(token);
        requestDto.setPassword(newPassword);
        
        PasswordResetTokenEntity resetToken = PasswordResetTokenEntity.builder()
                .email(email)
                .used(false)
                .build();
        
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .userName("Parker")
                .build();
        
        String encodedPassword = "encodedNewPassword";
        
        // Mock 설정
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(passwordResetTokenRepository.findByEmail(email)).thenReturn(Arrays.asList(resetToken));
        when(passwordResetTokenRepository.save(any(PasswordResetTokenEntity.class))).thenReturn(resetToken);

        // when - 테스트 실행
        authService.passwordReset(requestDto);

        // then - 결과 검증
        verify(passwordResetTokenRepository).findByToken(token);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(userEntity);
        verify(passwordResetTokenRepository).findByEmail(email);
        verify(passwordResetTokenRepository).save(resetToken);
        
        // 비밀번호가 변경되었는지 확인
        assertThat(userEntity.getPassword()).isEqualTo(encodedPassword);
    }
    
    @Test
    void 비밀번호리셋_토큰없음_예외발생() {
        // given - 테스트 데이터 준비
        String invalidToken = "invalid-token";
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
        requestDto.setToken(invalidToken);
        requestDto.setPassword("newPassword123");
        
        // Mock 설정
        when(passwordResetTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("토큰이 만료되었습니다");

        // when & then - 테스트 실행 및 예외 검증
        assertThatThrownBy(() -> authService.passwordReset(requestDto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("토큰이 만료되었습니다");
        
        verify(passwordResetTokenRepository).findByToken(invalidToken);
        verify(userRepository, never()).findByEmail(anyString());
    }
    
    @Test
    void 비밀번호리셋_사용자없음_예외발생() {
        // given - 테스트 데이터 준비
        String token = "valid-token";
        String email = "nonexistent@test.com";
        
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
        requestDto.setToken(token);
        requestDto.setPassword("newPassword123");
        
        PasswordResetTokenEntity resetToken = PasswordResetTokenEntity.builder()
                .email(email)
                .used(false)
                .build();
        
        // Mock 설정
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("사용자를 찾을 수 없습니다");

        // when & then - 테스트 실행 및 예외 검증
        assertThatThrownBy(() -> authService.passwordReset(requestDto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
        
        verify(passwordResetTokenRepository).findByToken(token);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
    }
}

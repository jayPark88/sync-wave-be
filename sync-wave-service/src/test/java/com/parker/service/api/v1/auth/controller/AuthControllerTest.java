package com.parker.service.api.v1.auth.controller;

import com.parker.common.dto.TokenDto;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.auth.dto.LoginDto;
import com.parker.service.api.v1.auth.dto.PasswordResetRequestDto;
import com.parker.service.api.v1.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthController 단위 테스트 - 간단한 버전
 *
 * @ExtendWith(MockitoExtension.class): Mockito를 사용한 테스트를 위한 어노테이션
 * - @Mock: Mock 객체를 생성
 * - @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상 객체
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService; // AuthService를 Mock 객체로 생성

    @Mock
    private MessageSource messageSource; // MessageSource를 Mock 객체로 생성

    @InjectMocks
    private AuthController authController; // 테스트 대상 AuthController (Mock 객체들이 주입됨)

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 유효한 로그인 정보로 로그인 요청 시 성공 응답을 받는다
     */
    @Test
    @DisplayName("로그인 성공 테스트")
    void login_유효한사용자_성공응답반환() {
        // Given: 테스트 데이터 준비
        LoginDto loginDto = LoginDto.builder()
                .userId("test@email.com")
                .password("password123")
                .build();

        TokenDto tokenDto = new TokenDto("jwt-token-here");
        CommonResponse<TokenDto> mockResponse = new CommonResponse<>(tokenDto);

        // Mock 설정: AuthService의 authorize 메서드가 호출되면 mockResponse를 반환
        when(authService.authorize(any(LoginDto.class)))
                .thenReturn(mockResponse);

        // When: 테스트할 메서드 실행
        CommonResponse<?> result = authController.authorize(loginDto, null);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(((TokenDto) result.getData()).getToken()).isEqualTo("jwt-token-here");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(authService).authorize(loginDto);
    }

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 비밀번호 재설정 이메일 요청 시 성공한다
     */
    @Test
    @DisplayName("비밀번호 재설정 이메일 요청 성공 테스트")
    void passwordResetEmailRequest_유효한이메일_성공() {
        // Given: 테스트 데이터 준비
        String email = "test@email.com";

        // Mock 설정: AuthService의 passwordResetEmailRequest 메서드가 호출되면 아무것도 하지 않음
        doNothing().when(authService).passwordResetEmailRequest(anyString());

        // When: 테스트할 메서드 실행
        authController.passwordResetEmailRequest(email);

        // Then: Mock 객체의 메서드가 호출되었는지 검증
        verify(authService).passwordResetEmailRequest(email);
    }

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 비밀번호 재설정 요청 시 성공한다
     */
    @Test
    @DisplayName("비밀번호 재설정 성공 테스트")
    void passwordReset_유효한토큰_성공() {
        // Given: 테스트 데이터 준비
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
        requestDto.setToken("valid-reset-token");
        requestDto.setPassword("newPassword123");

        // Mock 설정: AuthService의 passwordReset 메서드가 호출되면 아무것도 하지 않음
        doNothing().when(authService).passwordReset(any(PasswordResetRequestDto.class));

        // When: 테스트할 메서드 실행
        authController.passwordReset(requestDto, null);

        // Then: Mock 객체의 메서드가 호출되었는지 검증
        verify(authService).passwordReset(requestDto);
    }

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 잘못된 로그인 정보로 요청 시 실패한다
     */
    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 사용자 정보")
    void login_잘못된사용자정보_실패응답반환() {
        // Given: 잘못된 테스트 데이터 준비
        LoginDto loginDto = LoginDto.builder()
                .userId("") // 빈 사용자 ID
                .password("") // 빈 비밀번호
                .build();

        // Mock 설정: 예외 발생
        when(authService.authorize(any(LoginDto.class)))
                .thenThrow(new RuntimeException("인증 실패"));

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> authController.authorize(loginDto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인증 실패");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(authService).authorize(loginDto);
    }
}

/**
 * 📝 Controller 테스트 작성 팁:
 * <p>
 * 1. @ExtendWith(MockitoExtension.class): Mockito 테스트 환경 설정
 * 2. @Mock: 의존성을 Mock 객체로 대체
 * 3. @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상
 * 4. verify(): Mock 객체의 메서드 호출 여부 검증
 * 5. assertThat(): AssertJ를 사용한 가독성 좋은 검증
 * <p>
 * 🚀 다음 단계:
 * 1. 이 테스트를 실행하여 실패하는지 확인 (Red 단계)
 * 2. AuthController의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Controller 테스트는 비즈니스 로직보다는 HTTP 요청/응답 처리에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 */
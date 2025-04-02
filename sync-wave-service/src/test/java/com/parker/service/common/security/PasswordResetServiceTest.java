package com.parker.service.common.security;

import com.parker.common.enums.Role;
import com.parker.common.enums.UserStatus;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.PasswordResetTokenRepository;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.service.EmailService;
import com.parker.common.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // JUnit 5와 Mockito를 통합하여 테스트 환경을 설정하는 애너테이션
public class PasswordResetServiceTest {

    @Mock  // UserRepository를 Mock 객체로 생성하여 실제 DB 호출을 피함
    private UserRepository userRepository;

    @Mock  // EmailService를 Mock 객체로 생성하여 실제 이메일 발송을 피함
    private EmailService emailService;

    @Mock  // Mock the PasswordResetTokenRepository
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks  // 위에서 Mock 객체로 생성한 UserRepository와 EmailService를 PasswordResetService에 주입
    private PasswordResetService passwordResetService;


    @Test  // JUnit 5의 테스트 메서드, 테스트가 실행되는 부분
    void shouldSendPasswordResetEmail_WhenEmailIsValid() {
        // given: 테스트에 필요한 사전 조건을 설정
        String email = "user@example.com";  // 테스트용 이메일 설정
        String subject = "Password Reset Request";  // 이메일 제목 설정

        // UserEntity 객체 생성: 비밀번호 초기화 이메일을 발송할 사용자 정보를 설정
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .userName("홍길동")  // 사용자의 이름
                .password("password!")  // 사용자의 비밀번호
                .nickName("nick")  // 사용자의 닉네임
                .phone("01012345678")  // 사용자의 전화번호
                .email(email)  // 테스트용 이메일 주소
                .role(Role.ROLE_MASTER.code())  // 사용자의 역할
                .status(UserStatus.ACTIVATED.code())  // 사용자의 상태 (활성화)
                .build();

        // when: 실제 테스트할 비즈니스 로직 실행
        // userRepository.findByEmail(email)가 호출되면 mock 데이터를 반환하도록 설정
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // 비밀번호 재설정 이메일 발송 메서드 호출
        passwordResetService.sendResetEmail(email);

        // then: 메서드 실행 후 검증할 부분
        // 이메일 서비스의 sendEmail 메서드가 정확히 1번 호출되었는지 확인
        // 첫 번째 파라미터는 이메일 주소, 두 번째 파라미터는 이메일 제목, 세 번째는 본문 내용에 "reset your password"가 포함되어야 함
        verify(emailService, times(1)).sendEmail(
                eq(email),  // 이메일 주소가 정확히 일치하는지 확인
                eq(subject),  // 제목이 정확히 일치하는지 확인
                contains("reset your password")  // 본문에 "reset your password"라는 문구가 포함되어야 함
        );
    }
}


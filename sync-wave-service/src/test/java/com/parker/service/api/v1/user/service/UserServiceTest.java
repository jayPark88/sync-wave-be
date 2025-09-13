package com.parker.service.api.v1.user.service;

import com.parker.common.enums.Role;
import com.parker.common.enums.UserStatus;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.jwt.TokenProvider;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.user.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService 단위 테스트 - 간단한 버전
 *
 * @ExtendWith(MockitoExtension.class): Mockito를 사용한 테스트를 위한 어노테이션
 * - @Mock: Mock 객체를 생성
 * - @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상 객체
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository; // UserRepository Mock 객체

    @Mock
    private PasswordEncoder passwordEncoder; // PasswordEncoder Mock 객체

    @Mock
    private MessageSource messageSource; // MessageSource Mock 객체

    @Mock
    private TokenProvider tokenProvider; // TokenProvider Mock 객체

    @InjectMocks
    private UserService userService; // 테스트 대상 UserService (Mock 객체들이 주입됨)

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 새로운 사용자 회원가입 시 성공한다
     */
    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_새로운사용자_성공() {
        // Given: 테스트 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("홍길동")
                .nickName("길동이")
                .phone("010-1234-5678")
                .email("hong@email.com")
                .password("password123")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .nickName("길동이")
                .phone("010-1234-5678")
                .email("hong@email.com")
                .role(Role.ROLE_USER.code())
                .status(UserStatus.ACTIVATED.code())
                .build();

        // Mock 설정
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // 이메일 중복 없음
        when(userRepository.count()).thenReturn(1L); // 기존 사용자 1명 존재
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword"); // 비밀번호 암호화
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser); // 저장된 사용자 반환

        // When: 테스트할 메서드 실행
        UserEntity result = userService.signUp(userDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("hong@email.com");
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER.code());
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVATED.code());

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userRepository).findByEmail("hong@email.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 중복된 이메일로 회원가입 시 실패한다
     */
    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 이메일")
    void signUp_중복된이메일_실패() {
        // Given: 중복된 이메일을 가진 사용자 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("홍길동")
                .email("existing@email.com")
                .password("password123")
                .build();

        UserEntity existingUser = UserEntity.builder()
                .email("existing@email.com")
                .build();

        // Mock 설정: 이미 존재하는 이메일
        when(userRepository.findByEmail("existing@email.com")).thenReturn(Optional.of(existingUser));
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("이미 존재하는 사용자입니다.");

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> userService.signUp(userDto))
                .isInstanceOf(RuntimeException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userRepository).findByEmail("existing@email.com");
        verify(userRepository, never()).save(any(UserEntity.class)); // save가 호출되지 않았는지 확인
    }

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 사용자 목록 조회 시 성공한다
     */
    @Test
    @DisplayName("사용자 목록 조회 성공 테스트")
    void searchUserList_마스터사용자_성공() {
        // Given: 테스트 데이터 준비
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .role(Role.ROLE_MASTER.code())
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .userName("김철수")
                .email("kim@email.com")
                .role(Role.ROLE_USER.code())
                .build();

        List<UserEntity> userList = Arrays.asList(user1, user2);
        Page<UserEntity> userPage = new PageImpl<>(userList, PageRequest.of(0, 10), 2);

        // Mock 설정
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("master@email.com"));

            UserEntity masterUser = UserEntity.builder()
                    .email("master@email.com")
                    .role(Role.ROLE_MASTER.code())
                    .build();

            when(userRepository.findByEmail("master@email.com")).thenReturn(Optional.of(masterUser));
            when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

            // When: 테스트할 메서드 실행
            Page<UserEntity> result = userService.searchUserList(0, 10);

            // Then: 결과 검증
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getUserName()).isEqualTo("홍길동");
            assertThat(result.getContent().get(1).getUserName()).isEqualTo("김철수");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(userRepository, atLeastOnce()).findByEmail("master@email.com");
            verify(userRepository).findAll(any(Pageable.class));
        }
    }

    /**
     * 🔴 TDD Step 1: 실패하는 테스트 작성
     * <p>
     * 테스트 시나리오: 사용자 정보 조회 시 성공한다
     */
    @Test
    @DisplayName("사용자 정보 조회 성공 테스트")
    void getUserInfo_유효한사용자_성공() {
        // Given: 테스트 데이터 준비
        String userId = "test@email.com";
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("test@email.com")
                .role(Role.ROLE_USER.code())
                .build();

        // Mock 설정
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("test@email.com"));

            when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

            // When: 테스트할 메서드 실행
            Optional<UserEntity> result = userService.getUserInfo(userId);

            // Then: 결과 검증
            assertThat(result).isPresent();
            assertThat(result.get().getUserName()).isEqualTo("홍길동");
            assertThat(result.get().getEmail()).isEqualTo("test@email.com");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(userRepository, atLeastOnce()).findByEmail("test@email.com");
        }
    }
}

/**
 * 📝 Service 테스트 작성 팁:
 * <p>
 * 1. @ExtendWith(MockitoExtension.class): Mockito 테스트 환경 설정
 * 2. @Mock: 의존성을 Mock 객체로 대체
 * 3. @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상
 * 4. MockedStatic: 정적 메서드를 Mock 처리
 * 5. verify(): Mock 객체의 메서드 호출 여부 검증
 * 6. assertThat(): AssertJ를 사용한 가독성 좋은 검증
 * <p>
 * 🚀 다음 단계:
 * 1. 이 테스트를 실행하여 실패하는지 확인 (Red 단계)
 * 2. UserService의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - 테스트는 작은 단위로 나누어 작성하세요
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성하세요
 * - Mock 객체는 실제 동작을 시뮬레이션하므로 신중하게 설정하세요
 */
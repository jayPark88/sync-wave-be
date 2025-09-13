package com.parker.service.api.v1.user.service;

import com.parker.common.enums.Role;
import com.parker.common.enums.UserStatus;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.jwt.TokenProvider;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.user.dto.UserDto;
import com.parker.service.api.v1.user.dto.UserUpdateRequestDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 단위 테스트 - TDD 방법론 적용
 * 
 * 🔴 TDD Step 1: 실패하는 테스트 작성 (Red)
 * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 (Green)
 * 🔵 TDD Step 3: 코드 리팩토링 (Refactor)
 * 
 * @ExtendWith(MockitoExtension.class): Mockito를 사용한 테스트를 위한 어노테이션
 * - @Mock: Mock 객체를 생성
 * - @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상 객체
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository; // UserRepository를 Mock 객체로 생성

    @Mock
    private PasswordEncoder passwordEncoder; // PasswordEncoder를 Mock 객체로 생성

    @Mock
    private MessageSource messageSource; // MessageSource를 Mock 객체로 생성

    @Mock
    private TokenProvider tokenProvider; // TokenProvider를 Mock 객체로 생성

    @InjectMocks
    private UserService userService; // 테스트 대상 UserService (Mock 객체들이 주입됨)

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 유효한 사용자 정보로 회원가입 요청 시 성공한다
     */
    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_유효한사용자정보_성공() {
        // Given: 테스트 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("홍길동")
                .password("password123456")
                .nickName("길동이")
                .phone("01012345678")
                .email("hong@email.com")
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .role(Role.ROLE_USER.code())
                .status(UserStatus.ACTIVATED.code())
                .build();

        // Mock 설정: 이메일 중복 체크 - 사용자가 존재하지 않음
        when(userRepository.findByEmail("hong@email.com")).thenReturn(Optional.empty());
        
        // Mock 설정: 비밀번호 인코딩
        when(passwordEncoder.encode("password123456")).thenReturn("encodedPassword");
        
        // Mock 설정: 사용자 수 카운트 - 첫 번째 사용자이므로 ROLE_USER (실제 로직에 맞춤)
        when(userRepository.count()).thenReturn(0L);
        
        // Mock 설정: 사용자 저장
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // When: 테스트할 메서드 실행
        UserEntity result = userService.signUp(userDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("hong@email.com");
        assertThat(result.getRole()).isEqualTo(Role.ROLE_USER.code()); // 첫 번째 사용자는 ROLE_USER (실제 로직)

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userRepository).findByEmail("hong@email.com");
        verify(passwordEncoder).encode("password123456");
        verify(userRepository).count();
        verify(userRepository).save(any(UserEntity.class));
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 중복된 이메일로 회원가입 요청 시 실패한다
     */
    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 이메일")
    void signUp_중복된이메일_실패() {
        // Given: 테스트 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("홍길동")
                .password("password123456")
                .nickName("길동이")
                .phone("01012345678")
                .email("hong@email.com")
                .build();

        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .userName("기존사용자")
                .email("hong@email.com")
                .build();

        // Mock 설정: 이메일 중복 체크 - 사용자가 이미 존재함
        when(userRepository.findByEmail("hong@email.com")).thenReturn(Optional.of(existingUser));
        
        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("이미 존재하는 사용자입니다");

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> userService.signUp(userDto))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userRepository).findByEmail("hong@email.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 유효한 토큰으로 사용자 정보 수정 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 정보 수정 성공 테스트")
    void updateUser_유효한토큰_성공() {
        // Given: 테스트 데이터 준비
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setToken("valid-token");
        updateDto.setUserName("수정된이름");
        updateDto.setNickName("수정된닉네임");

        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .nickName("길동이")
                .email("hong@email.com")
                .role(Role.ROLE_MASTER.code())
                .build();

        // Mock 설정: 토큰에서 사용자 ID 추출
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(tokenProvider.getAuthentication("valid-token")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("hong@email.com");

        // Mock 설정: 사용자 체크 - 마스터 권한
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("hong@email.com"));
            when(userRepository.findByEmail("hong@email.com")).thenReturn(Optional.of(existingUser));

            // Mock 설정: 사용자 저장
            when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);

            // When: 테스트할 메서드 실행
            UserEntity result = userService.updateUser(updateDto);

            // Then: 결과 검증
            assertThat(result).isNotNull();
            assertThat(result.getUserName()).isEqualTo("수정된이름");
            assertThat(result.getNickName()).isEqualTo("수정된닉네임");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(userRepository, atLeastOnce()).findByEmail("hong@email.com");
            verify(userRepository).save(any(UserEntity.class));
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 사용자 삭제 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 삭제 성공 테스트")
    void deleteUserInfo_유효한사용자ID_성공() {
        // Given: 테스트 데이터 준비
        String userId = "hong@email.com";
        String expectedMessage = "hong@email.com deleted!";

        UserEntity user = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .role(Role.ROLE_MASTER.code())
                .build();

        // Mock 설정: 사용자 체크 - 마스터 권한
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("hong@email.com"));
            when(userRepository.findByEmail("hong@email.com")).thenReturn(Optional.of(user));

            // Mock 설정: 사용자 삭제
            doNothing().when(userRepository).deleteByEmail(userId);

            // When: 테스트할 메서드 실행
            String result = userService.deleteUserInfo(userId);

            // Then: 결과 검증
            assertThat(result).isEqualTo(expectedMessage);

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(userRepository, atLeastOnce()).findByEmail("hong@email.com");
            verify(userRepository).deleteByEmail(userId);
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 사용자 목록 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 목록 조회 성공 테스트")
    void searchUserList_마스터사용자_성공() {
        // Given: 테스트 데이터 준비
        int page = 0;
        int size = 10;

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
        Page<UserEntity> userPage = new PageImpl<>(userList, PageRequest.of(page, size), 2);

        // Mock 설정: 사용자 체크 - 마스터 권한
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("master@email.com"));
            when(userRepository.findByEmail("master@email.com")).thenReturn(Optional.of(user1));
            when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

            // When: 테스트할 메서드 실행
            Page<UserEntity> result = userService.searchUserList(page, size);

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
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 사용자 정보 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 정보 조회 성공 테스트")
    void getUserInfo_유효한사용자_성공() {
        // Given: 테스트 데이터 준비
        String userId = "hong@email.com";

        UserEntity user = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .role(Role.ROLE_USER.code())
                .build();

        // Mock 설정: 사용자 체크
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("hong@email.com"));
            when(userRepository.findByEmail("hong@email.com")).thenReturn(Optional.of(user));

            // When: 테스트할 메서드 실행
            Optional<UserEntity> result = userService.getUserInfo(userId);

            // Then: 결과 검증
            assertThat(result).isPresent();
            assertThat(result.get().getUserName()).isEqualTo("홍길동");
            assertThat(result.get().getEmail()).isEqualTo("hong@email.com");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(userRepository, atLeastOnce()).findByEmail("hong@email.com");
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 토큰으로 사용자 정보 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("토큰으로 사용자 정보 조회 성공 테스트")
    void getUserInfoByToken_유효한토큰_성공() {
        // Given: 테스트 데이터 준비
        String token = "valid-token";
        String email = "hong@email.com";

        UserEntity user = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .role(Role.ROLE_USER.code())
                .build();

        // Mock 설정: 토큰에서 사용자 ID 추출
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(tokenProvider.getAuthentication(token)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);

        // Mock 설정: 사용자 체크
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of(email));
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // When: 테스트할 메서드 실행
            Optional<UserEntity> result = userService.getUserInfoByToken(token);

            // Then: 결과 검증
            assertThat(result).isPresent();
            assertThat(result.get().getUserName()).isEqualTo("홍길동");
            assertThat(result.get().getEmail()).isEqualTo("hong@email.com");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(tokenProvider).getAuthentication(token);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 잘못된 토큰으로 사용자 정보 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("토큰으로 사용자 정보 조회 실패 테스트 - 잘못된 토큰")
    void getUserInfoByToken_잘못된토큰_실패() {
        // Given: 테스트 데이터 준비
        String token = "invalid-token";
        String email = "hong@email.com";

        // Mock 설정: 토큰에서 사용자 ID 추출
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(tokenProvider.getAuthentication(token)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);

        // Mock 설정: 사용자가 존재하지 않음
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When: 테스트할 메서드 실행
        Optional<UserEntity> result = userService.getUserInfoByToken(token);

        // Then: 결과 검증
        assertThat(result).isEmpty();

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(tokenProvider).getAuthentication(token);
        verify(userRepository).findByEmail(email);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 권한이 없는 사용자가 사용자 목록 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("사용자 목록 조회 실패 테스트 - 권한 없음")
    void searchUserList_권한없음_실패() {
        // Given: 테스트 데이터 준비
        int page = 0;
        int size = 10;

        // Mock 설정: 사용자 체크 - 권한 없음
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.empty());

            // Mock 설정: MessageSource 메시지 반환
            when(messageSource.getMessage(anyString(), any(), any())).thenReturn("권한이 없습니다");

            // When & Then: 예외 발생 검증
            assertThatThrownBy(() -> userService.searchUserList(page, size))
                    .isInstanceOf(CustomException.class);

            // Mock 객체의 메서드가 호출되지 않았는지 검증
            verify(userRepository, never()).findAll(any(Pageable.class));
        }
    }
}

/**
 * 📝 Service 테스트 작성 팁:
 * <p>
 * 1. @ExtendWith(MockitoExtension.class): Mockito 테스트 환경 설정
 * 2. @Mock: 의존성을 Mock 객체로 대체
 * 3. @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상
 * 4. MockedStatic: 정적 메서드를 Mock하는 방법
 * 5. verify(): Mock 객체의 메서드 호출 여부 검증
 * 6. assertThat(): AssertJ를 사용한 가독성 좋은 검증
 * <p>
 * 🚀 다음 단계:
 * 1. 이 테스트를 실행하여 실패하는지 확인 (Red 단계)
 * 2. UserService의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Service 테스트는 비즈니스 로직에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 * - Given-When-Then 패턴을 사용하여 테스트 구조를 명확하게 작성
 * - 정적 메서드는 MockedStatic을 사용하여 Mock 처리
 */

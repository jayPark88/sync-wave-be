package com.parker.service.api.v1.user.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.user.dto.UserDto;
import com.parker.service.api.v1.user.dto.UserInfoRequestDto;
import com.parker.service.api.v1.user.dto.UserUpdateRequestDto;
import com.parker.service.api.v1.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserController 단위 테스트 - TDD 방법론 적용
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
class UserControllerTest {

    @Mock
    private UserService userService; // UserService를 Mock 객체로 생성

    @Mock
    private MessageSource messageSource; // MessageSource를 Mock 객체로 생성

    @Mock
    private BindingResult bindingResult; // BindingResult를 Mock 객체로 생성

    @InjectMocks
    private UserController userController; // 테스트 대상 UserController (Mock 객체들이 주입됨)

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 유효한 사용자 정보로 회원가입 요청 시 성공 응답을 받는다
     */
    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_유효한사용자정보_성공응답반환() {
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
                .build();

        // Mock 설정: BindingResult에 에러가 없다고 설정
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // Mock 설정: UserService의 signUp 메서드가 호출되면 savedUser를 반환
        when(userService.signUp(any(UserDto.class))).thenReturn(savedUser);

        // When: 테스트할 메서드 실행
        CommonResponse<UserEntity> result = userController.signup(userDto, bindingResult);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getUserName()).isEqualTo("홍길동");
        assertThat(result.getData().getEmail()).isEqualTo("hong@email.com");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).signUp(userDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 잘못된 사용자 정보로 회원가입 요청 시 실패한다
     */
    @Test
    @DisplayName("회원가입 실패 테스트 - 잘못된 사용자 정보")
    void signup_잘못된사용자정보_실패응답반환() {
        // Given: 잘못된 테스트 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("") // 빈 사용자명
                .password("") // 빈 비밀번호
                .email("") // 빈 이메일
                .build();

        // Mock 설정: BindingResult에 에러가 있다고 설정 (실패 시나리오)
        when(bindingResult.hasErrors()).thenReturn(true);
        
        // Mock 설정: FieldError 생성
        FieldError fieldError = new FieldError("userDto", "userName", "사용자명은 필수입니다.");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // When & Then: 예외 발생 검증 (Controller에서 CustomException을 던짐)
        assertThatThrownBy(() -> userController.signup(userDto, bindingResult))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되지 않았는지 검증 (bindingResult.hasErrors()가 true이므로)
        verify(userService, never()).signUp(any(UserDto.class));
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 사용자 정보 수정 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 정보 수정 성공 테스트")
    void modifyUserInfo_유효한정보_성공() {
        // Given: 테스트 데이터 준비
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setToken("valid-token");
        updateDto.setUserName("수정된이름");
        updateDto.setNickName("수정된닉네임");

        UserEntity updatedUser = UserEntity.builder()
                .id(1L)
                .userName("수정된이름")
                .nickName("수정된닉네임")
                .email("hong@email.com")
                .build();

        // Mock 설정: UserService의 updateUser 메서드가 호출되면 updatedUser를 반환
        when(userService.updateUser(any(UserUpdateRequestDto.class))).thenReturn(updatedUser);

        // When: 테스트할 메서드 실행
        CommonResponse<UserEntity> result = userController.modifyUserInfo(updateDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getUserName()).isEqualTo("수정된이름");
        assertThat(result.getData().getNickName()).isEqualTo("수정된닉네임");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).updateUser(updateDto);
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

        // Mock 설정: UserService의 deleteUserInfo 메서드가 호출되면 expectedMessage를 반환
        when(userService.deleteUserInfo(userId)).thenReturn(expectedMessage);

        // When: 테스트할 메서드 실행
        CommonResponse<String> result = userController.deleteUserInfo(userId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isEqualTo(expectedMessage);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).deleteUserInfo(userId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 사용자 목록 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 목록 조회 성공 테스트")
    void searchUserList_유효한페이지정보_성공() {
        // Given: 테스트 데이터 준비
        int page = 0;
        int size = 10;

        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .userName("김철수")
                .email("kim@email.com")
                .build();

        List<UserEntity> userList = Arrays.asList(user1, user2);
        Page<UserEntity> userPage = new PageImpl<>(userList, PageRequest.of(page, size), 2);

        // Mock 설정: UserService의 searchUserList 메서드가 호출되면 userPage를 반환
        when(userService.searchUserList(page, size)).thenReturn(userPage);

        // When: 테스트할 메서드 실행
        CommonResponse<Page<UserEntity>> result = userController.searchUserList(page, size);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getContent()).hasSize(2);
        assertThat(result.getData().getContent().get(0).getUserName()).isEqualTo("홍길동");
        assertThat(result.getData().getContent().get(1).getUserName()).isEqualTo("김철수");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).searchUserList(page, size);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 토큰으로 사용자 정보 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("토큰으로 사용자 정보 조회 성공 테스트")
    void getUserInfo_유효한토큰_성공() {
        // Given: 테스트 데이터 준비
        UserInfoRequestDto requestDto = new UserInfoRequestDto();
        requestDto.setToken("valid-token");

        UserEntity user = UserEntity.builder()
                .id(1L)
                .userName("홍길동")
                .email("hong@email.com")
                .build();

        // Mock 설정: UserService의 getUserInfoByToken 메서드가 호출되면 Optional.of(user)를 반환
        when(userService.getUserInfoByToken("valid-token")).thenReturn(java.util.Optional.of(user));

        // When: 테스트할 메서드 실행
        CommonResponse<UserEntity> result = userController.getUserInfo(requestDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getUserName()).isEqualTo("홍길동");
        assertThat(result.getData().getEmail()).isEqualTo("hong@email.com");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).getUserInfoByToken("valid-token");
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 잘못된 토큰으로 사용자 정보 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("토큰으로 사용자 정보 조회 실패 테스트 - 잘못된 토큰")
    void getUserInfo_잘못된토큰_실패() {
        // Given: 잘못된 테스트 데이터 준비
        UserInfoRequestDto requestDto = new UserInfoRequestDto();
        requestDto.setToken("invalid-token");

        // Mock 설정: UserService의 getUserInfoByToken 메서드가 호출되면 Optional.empty()를 반환
        when(userService.getUserInfoByToken("invalid-token")).thenReturn(java.util.Optional.empty());
        
        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("사용자를 찾을 수 없습니다");

        // When & Then: 예외 발생 검증 (Controller에서 CustomException을 던짐)
        assertThatThrownBy(() -> userController.getUserInfo(requestDto))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).getUserInfoByToken("invalid-token");
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 데이터베이스 오류로 회원가입 요청 시 실패한다
     */
    @Test
    @DisplayName("회원가입 실패 테스트 - 데이터베이스 오류")
    void signup_데이터베이스오류_실패() {
        // Given: 테스트 데이터 준비
        UserDto userDto = UserDto.builder()
                .userName("홍길동")
                .password("password123456")
                .nickName("길동이")
                .phone("01012345678")
                .email("hong@email.com")
                .build();

        // Mock 설정: BindingResult에 에러가 없다고 설정
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // Mock 설정: UserService의 signUp 메서드가 호출되면 DataAccessException을 던짐
        when(userService.signUp(any(UserDto.class))).thenThrow(new DataAccessException("Database error") {});
        
        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("회원가입에 실패했습니다");

        // When & Then: 예외 발생 검증 (Controller에서 CustomException을 던짐)
        assertThatThrownBy(() -> userController.signup(userDto, bindingResult))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).signUp(userDto);
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
 * 2. UserController의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Controller 테스트는 비즈니스 로직보다는 HTTP 요청/응답 처리에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 * - Given-When-Then 패턴을 사용하여 테스트 구조를 명확하게 작성
 */

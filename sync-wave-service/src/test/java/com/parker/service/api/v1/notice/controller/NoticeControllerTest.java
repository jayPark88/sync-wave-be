package com.parker.service.api.v1.notice.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import com.parker.service.api.v1.notice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NoticeController 단위 테스트 - TDD 방법론 적용
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
class NoticeControllerTest {

    @Mock
    private NoticeService noticeService; // NoticeService를 Mock 객체로 생성

    @InjectMocks
    private NoticeController noticeController; // 테스트 대상 NoticeController (Mock 객체들이 주입됨)

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 유효한 공지사항 정보로 생성 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 생성 성공 테스트")
    void createNotice_유효한공지사항정보_성공() {
        // Given: 테스트 데이터 준비
        NoticeDto noticeDto = NoticeDto.builder()
                .title("새로운 공지사항")
                .content("공지사항 내용입니다.")
                .priority("HIGH")
                .build();

        NoticeEntity createdNotice = NoticeEntity.builder()
                .id(1L)
                .title("새로운 공지사항")
                .content("공지사항 내용입니다.")
                .priority("HIGH")
                .isActive(true)
                .build();

        // Mock 설정: NoticeService의 createNotice 메서드가 호출되면 createdNotice를 반환
        when(noticeService.createNotice(any(NoticeDto.class))).thenReturn(createdNotice);

        // When: 테스트할 메서드 실행
        CommonResponse<NoticeEntity> result = noticeController.createNotice(noticeDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getTitle()).isEqualTo("새로운 공지사항");
        assertThat(result.getData().getContent()).isEqualTo("공지사항 내용입니다.");
        assertThat(result.getData().getPriority()).isEqualTo("HIGH");
        assertThat(result.getData().getIsActive()).isTrue();

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).createNotice(noticeDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 공지사항 목록 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 목록 조회 성공 테스트")
    void getNoticeList_유효한검색조건_성공() {
        // Given: 테스트 데이터 준비
        NoticeSearchDto searchDto = new NoticeSearchDto();
        searchDto.setPage(0);
        searchDto.setSize(10);
        searchDto.setIsActive(true);

        NoticeEntity notice1 = NoticeEntity.builder()
                .id(1L)
                .title("공지사항 1")
                .content("내용 1")
                .priority("HIGH")
                .isActive(true)
                .build();

        NoticeEntity notice2 = NoticeEntity.builder()
                .id(2L)
                .title("공지사항 2")
                .content("내용 2")
                .priority("MEDIUM")
                .isActive(true)
                .build();

        List<NoticeEntity> noticeList = Arrays.asList(notice1, notice2);
        Page<NoticeEntity> noticePage = new PageImpl<>(noticeList, PageRequest.of(0, 10), 2);

        // Mock 설정: NoticeService의 getNoticeList 메서드가 호출되면 noticePage를 반환
        when(noticeService.getNoticeList(any(NoticeSearchDto.class))).thenReturn(noticePage);

        // When: 테스트할 메서드 실행
        CommonResponse<Page<NoticeEntity>> result = noticeController.getNoticeList(searchDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getContent()).hasSize(2);
        assertThat(result.getData().getContent().get(0).getTitle()).isEqualTo("공지사항 1");
        assertThat(result.getData().getContent().get(1).getTitle()).isEqualTo("공지사항 2");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).getNoticeList(searchDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 사용자 권한 정보 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("사용자 권한 조회 성공 테스트")
    void getUserRole_유효한사용자_성공() {
        // Given: 테스트 데이터 준비
        String expectedRole = "ROLE_MASTER";

        // Mock 설정: SecurityUtil의 getCurrentUserRole 메서드가 호출되면 expectedRole을 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of(expectedRole));

            // When: 테스트할 메서드 실행
            CommonResponse<String> result = noticeController.getUserRole();

            // Then: 결과 검증
            assertThat(result).isNotNull();
            assertThat(result.getData()).isEqualTo(expectedRole);
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 공지사항 상세 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 상세 조회 성공 테스트")
    void getNoticeDetail_유효한공지사항ID_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;

        NoticeEntity notice = NoticeEntity.builder()
                .id(noticeId)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .priority("HIGH")
                .isActive(true)
                .build();

        // Mock 설정: NoticeService의 getNoticeDetailForUser 메서드가 호출되면 notice를 반환
        when(noticeService.getNoticeDetailForUser(noticeId)).thenReturn(notice);

        // When: 테스트할 메서드 실행
        CommonResponse<NoticeEntity> result = noticeController.getNoticeDetail(noticeId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getId()).isEqualTo(noticeId);
        assertThat(result.getData().getTitle()).isEqualTo("공지사항 제목");
        assertThat(result.getData().getContent()).isEqualTo("공지사항 내용");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).getNoticeDetailForUser(noticeId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 공지사항 수정 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 수정 성공 테스트")
    void updateNotice_유효한공지사항정보_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;
        NoticeDto noticeDto = NoticeDto.builder()
                .title("수정된 공지사항")
                .content("수정된 내용")
                .priority("MEDIUM")
                .build();

        NoticeEntity updatedNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("수정된 공지사항")
                .content("수정된 내용")
                .priority("MEDIUM")
                .isActive(true)
                .build();

        // Mock 설정: NoticeService의 updateNotice 메서드가 호출되면 updatedNotice를 반환
        when(noticeService.updateNotice(noticeId, noticeDto)).thenReturn(updatedNotice);

        // When: 테스트할 메서드 실행
        CommonResponse<NoticeEntity> result = noticeController.updateNotice(noticeId, noticeDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getId()).isEqualTo(noticeId);
        assertThat(result.getData().getTitle()).isEqualTo("수정된 공지사항");
        assertThat(result.getData().getContent()).isEqualTo("수정된 내용");
        assertThat(result.getData().getPriority()).isEqualTo("MEDIUM");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).updateNotice(noticeId, noticeDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 공지사항 상태 변경 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 상태 변경 성공 테스트")
    void updateNoticeStatus_유효한상태변경_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;
        Boolean isActive = false;

        NoticeEntity updatedNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .priority("HIGH")
                .isActive(false)
                .build();

        // Mock 설정: NoticeService의 updateNoticeStatus 메서드가 호출되면 updatedNotice를 반환
        when(noticeService.updateNoticeStatus(noticeId, isActive)).thenReturn(updatedNotice);

        // When: 테스트할 메서드 실행
        CommonResponse<NoticeEntity> result = noticeController.updateNoticeStatus(noticeId, isActive);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getId()).isEqualTo(noticeId);
        assertThat(result.getData().getIsActive()).isFalse();

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).updateNoticeStatus(noticeId, isActive);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 공지사항 삭제 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 삭제 성공 테스트")
    void deleteNotice_유효한공지사항ID_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;
        String expectedMessage = "공지사항이 삭제되었습니다.";

        // Mock 설정: NoticeService의 deleteNotice 메서드가 호출되면 아무것도 하지 않음
        doNothing().when(noticeService).deleteNotice(noticeId);

        // When: 테스트할 메서드 실행
        CommonResponse<String> result = noticeController.deleteNotice(noticeId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isEqualTo(expectedMessage);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).deleteNotice(noticeId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 존재하지 않는 공지사항 상세 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("공지사항 상세 조회 실패 테스트 - 존재하지 않는 공지사항")
    void getNoticeDetail_존재하지않는공지사항_실패() {
        // Given: 테스트 데이터 준비
        Long noticeId = 999L;

        // Mock 설정: NoticeService의 getNoticeDetailForUser 메서드가 호출되면 CustomException을 던짐
        when(noticeService.getNoticeDetailForUser(noticeId))
                .thenThrow(new CustomException("500", "공지사항을 찾을 수 없습니다", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> noticeController.getNoticeDetail(noticeId))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).getNoticeDetailForUser(noticeId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 권한이 없는 사용자가 공지사항 수정 요청 시 실패한다
     */
    @Test
    @DisplayName("공지사항 수정 실패 테스트 - 권한 없음")
    void updateNotice_권한없음_실패() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;
        NoticeDto noticeDto = NoticeDto.builder()
                .title("수정된 공지사항")
                .content("수정된 내용")
                .priority("MEDIUM")
                .build();

        // Mock 설정: NoticeService의 updateNotice 메서드가 호출되면 CustomException을 던짐
        when(noticeService.updateNotice(noticeId, noticeDto))
                .thenThrow(new CustomException("403", "공지사항 관리 권한이 없습니다", org.springframework.http.HttpStatus.FORBIDDEN));

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> noticeController.updateNotice(noticeId, noticeDto))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeService).updateNotice(noticeId, noticeDto);
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
 * 6. MockedStatic: 정적 메서드를 Mock하는 방법
 * <p>
 * 🚀 다음 단계:
 * 1. 이 테스트를 실행하여 실패하는지 확인 (Red 단계)
 * 2. NoticeController의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Controller 테스트는 비즈니스 로직보다는 HTTP 요청/응답 처리에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 * - Given-When-Then 패턴을 사용하여 테스트 구조를 명확하게 작성
 * - 정적 메서드는 MockedStatic을 사용하여 Mock 처리
 */

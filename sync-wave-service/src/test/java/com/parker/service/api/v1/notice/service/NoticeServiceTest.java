package com.parker.service.api.v1.notice.service;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.jpa.repository.NoticeRepository;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * com.parker.service.api.v1.notice.service
 * ㄴ NoticeServiceTest
 *
 * <pre>
 * description : 공지사항 서비스 테스트
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 12/23/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private NoticeService noticeService;

    private NoticeEntity testNotice;
    private NoticeDto testNoticeDto;

    @BeforeEach
    void setUp() {
        testNotice = NoticeEntity.builder()
                .id(1L)
                .title("테스트 공지사항")
                .content("테스트 공지사항 내용입니다.")
                .priority("HIGH")
                .isActive(true)
                .createdBy("testuser")
                .createdDateTime(LocalDateTime.now())
                .modifiedDateTime(LocalDateTime.now())
                .build();

        testNoticeDto = NoticeDto.builder()
                .title("테스트 공지사항")
                .content("테스트 공지사항 내용입니다.")
                .priority("HIGH")
                .build();
    }

    /**
     * 공지사항 생성 성공 테스트
     * 
     * 테스트 시나리오:
     * 1. ROLE_MASTER 권한을 가진 사용자가 공지사항을 생성
     * 2. SecurityUtil을 모킹하여 사용자 정보와 권한 정보 제공
     * 3. Repository의 save 메서드를 모킹하여 저장된 엔티티 반환
     * 4. 생성된 공지사항의 모든 필드가 올바르게 설정되었는지 검증
     * 5. Repository의 save 메서드가 호출되었는지 검증
     */
    @Test
    @DisplayName("공지사항 생성 성공 테스트")
    void createNotice_Success() {
        // given - 테스트 준비 단계
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            // SecurityUtil 모킹: 현재 사용자명과 권한 정보 제공
            securityUtilMock.when(SecurityUtil::getCurrentUserName)
                    .thenReturn(Optional.of("testuser"));
            securityUtilMock.when(SecurityUtil::getCurrentUserRole)
                    .thenReturn(Optional.of("ROLE_MASTER"));

            // Repository 모킹: 저장된 엔티티 반환
            when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(testNotice);

            // when - 테스트 실행 단계
            NoticeEntity result = noticeService.createNotice(testNoticeDto);

            // then - 검증 단계
            // 1. 결과가 null이 아닌지 확인
            assertNotNull(result);
            // 2. 제목이 올바르게 설정되었는지 확인
            assertEquals(testNotice.getTitle(), result.getTitle());
            // 3. 내용이 올바르게 설정되었는지 확인
            assertEquals(testNotice.getContent(), result.getContent());
            // 4. 중요도가 올바르게 설정되었는지 확인
            assertEquals(testNotice.getPriority(), result.getPriority());
            // 5. 활성화 상태가 true로 설정되었는지 확인
            assertTrue(result.getIsActive());
            // 6. 작성자가 올바르게 설정되었는지 확인
            assertEquals("testuser", result.getCreatedBy());

            // Repository의 save 메서드가 호출되었는지 검증
            verify(noticeRepository).save(any(NoticeEntity.class));
        }
    }

    /**
     * 공지사항 생성 실패 테스트 - 권한 없음
     * 
     * 테스트 시나리오:
     * 1. ROLE_USER 권한을 가진 사용자가 공지사항 생성 시도
     * 2. SecurityUtil을 모킹하여 ROLE_USER 권한 정보 제공
     * 3. CustomException이 발생하는지 검증
     * 4. 예외의 HTTP 상태 코드가 FORBIDDEN인지 검증
     * 5. 예외 메시지가 올바른지 검증
     */
    @Test
    @DisplayName("공지사항 생성 실패 - 권한 없음")
    void createNotice_Fail_NoPermission() {
        // given - 테스트 준비 단계
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            // SecurityUtil 모킹: ROLE_USER 권한 정보 제공 (권한 없음)
            securityUtilMock.when(SecurityUtil::getCurrentUserRole)
                    .thenReturn(Optional.of("ROLE_USER"));

            // when & then - 테스트 실행 및 검증 단계
            // CustomException이 발생하는지 검증
            CustomException exception = assertThrows(CustomException.class, () -> {
                noticeService.createNotice(testNoticeDto);
            });

            // 예외의 HTTP 상태 코드가 FORBIDDEN인지 검증
            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            // 예외 메시지가 올바른지 검증
            assertEquals("공지사항 관리 권한이 없습니다.", exception.getMessage());
        }
    }

    /**
     * 공지사항 목록 조회 성공 테스트
     * 
     * 테스트 시나리오:
     * 1. 검색 조건 없이 공지사항 목록 조회
     * 2. Repository의 findByIsActiveTrueOrderByCreatedDateTimeDesc 메서드 모킹
     * 3. 반환된 목록이 올바른지 검증
     * 4. Repository 메서드가 호출되었는지 검증
     */
    @Test
    @DisplayName("공지사항 목록 조회 성공 테스트")
    void getNoticeList_Success() {
        // given - 테스트 준비 단계
        List<NoticeEntity> noticeList = Arrays.asList(testNotice);
        // Repository 모킹: 활성화된 공지사항 목록 반환
        when(noticeRepository.findByIsActiveTrueOrderByCreatedDateTimeDesc()).thenReturn(noticeList);

        // when - 테스트 실행 단계
        List<NoticeEntity> result = noticeService.getNoticeList(null);

        // then - 검증 단계
        // 1. 결과가 null이 아닌지 확인
        assertNotNull(result);
        // 2. 목록 크기가 1인지 확인
        assertEquals(1, result.size());
        // 3. 첫 번째 공지사항의 제목이 올바른지 확인
        assertEquals(testNotice.getTitle(), result.get(0).getTitle());

        // Repository의 findByIsActiveTrueOrderByCreatedDateTimeDesc 메서드가 호출되었는지 검증
        verify(noticeRepository).findByIsActiveTrueOrderByCreatedDateTimeDesc();
    }

    /**
     * 공지사항 키워드 검색 성공 테스트
     * 
     * 테스트 시나리오:
     * 1. 키워드 검색 조건으로 공지사항 목록 조회
     * 2. Repository의 findByTitleOrContentContainingAndIsActiveTrue 메서드 모킹
     * 3. 검색 결과가 올바른지 검증
     * 4. 올바른 Repository 메서드가 호출되었는지 검증
     */
    @Test
    @DisplayName("공지사항 키워드 검색 성공 테스트")
    void getNoticeList_Success_WithKeyword() {
        // given - 테스트 준비 단계
        // 키워드 검색 조건 생성
        NoticeSearchDto searchDto = NoticeSearchDto.builder()
                .keyword("테스트")
                .build();
        List<NoticeEntity> noticeList = Arrays.asList(testNotice);
        // Repository 모킹: 키워드 검색 결과 반환
        when(noticeRepository.findByTitleOrContentContainingAndIsActiveTrue("테스트")).thenReturn(noticeList);

        // when - 테스트 실행 단계
        List<NoticeEntity> result = noticeService.getNoticeList(searchDto);

        // then - 검증 단계
        // 1. 결과가 null이 아닌지 확인
        assertNotNull(result);
        // 2. 검색 결과가 1개인지 확인
        assertEquals(1, result.size());

        // Repository의 findByTitleOrContentContainingAndIsActiveTrue 메서드가 올바른 키워드로 호출되었는지 검증
        verify(noticeRepository).findByTitleOrContentContainingAndIsActiveTrue("테스트");
    }

    @Test
    @DisplayName("공지사항 상세 조회 성공 테스트")
    void getNoticeDetail_Success() {
        // given
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(testNotice));

        // when
        NoticeEntity result = noticeService.getNoticeDetail(1L);

        // then
        assertNotNull(result);
        assertEquals(testNotice.getTitle(), result.getTitle());
        assertEquals(testNotice.getContent(), result.getContent());

        verify(noticeRepository).findById(1L);
    }

    @Test
    @DisplayName("공지사항 상세 조회 실패 - 존재하지 않는 공지사항")
    void getNoticeDetail_Fail_NotFound() {
        // given
        when(noticeRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("notice.not.found"), any(), any(Locale.class)))
                .thenReturn("공지사항을 찾을 수 없습니다.");

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            noticeService.getNoticeDetail(999L);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
        assertEquals("공지사항을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("공지사항 수정 성공 테스트")
    void updateNotice_Success() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserRole)
                    .thenReturn(Optional.of("ROLE_MASTER"));

            when(noticeRepository.findById(1L)).thenReturn(Optional.of(testNotice));
            when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(testNotice);

            NoticeDto updateDto = NoticeDto.builder()
                    .title("수정된 공지사항")
                    .content("수정된 내용")
                    .priority("MEDIUM")
                    .build();

            // when
            NoticeEntity result = noticeService.updateNotice(1L, updateDto);

            // then
            assertNotNull(result);
            verify(noticeRepository).findById(1L);
            verify(noticeRepository).save(any(NoticeEntity.class));
        }
    }

    @Test
    @DisplayName("공지사항 상태 변경 성공 테스트")
    void updateNoticeStatus_Success() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserRole)
                    .thenReturn(Optional.of("ROLE_MASTER"));

            when(noticeRepository.findById(1L)).thenReturn(Optional.of(testNotice));
            when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(testNotice);

            // when
            NoticeEntity result = noticeService.updateNoticeStatus(1L, false);

            // then
            assertNotNull(result);
            verify(noticeRepository).findById(1L);
            verify(noticeRepository).save(any(NoticeEntity.class));
        }
    }

    @Test
    @DisplayName("공지사항 삭제 성공 테스트")
    void deleteNotice_Success() {
        // given
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserRole)
                    .thenReturn(Optional.of("ROLE_MASTER"));

            when(noticeRepository.findById(1L)).thenReturn(Optional.of(testNotice));

            // when
            noticeService.deleteNotice(1L);

            // then
            verify(noticeRepository).findById(1L);
            verify(noticeRepository).delete(testNotice);
        }
    }
} 
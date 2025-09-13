package com.parker.service.api.v1.notice.service;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.jpa.repository.NoticeRepository;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NoticeService 단위 테스트 - TDD 방법론 적용
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
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository; // NoticeRepository를 Mock 객체로 생성

    @Mock
    private MessageSource messageSource; // MessageSource를 Mock 객체로 생성

    @InjectMocks
    private NoticeService noticeService; // 테스트 대상 NoticeService (Mock 객체들이 주입됨)

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 마스터 권한으로 공지사항 생성 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 생성 성공 테스트 - 마스터 권한")
    void createNotice_마스터권한_성공() {
        // Given: 테스트 데이터 준비
        NoticeDto noticeDto = NoticeDto.builder()
                .title("새로운 공지사항")
                .content("공지사항 내용입니다.")
                .priority("HIGH")
                .build();

        NoticeEntity savedNotice = NoticeEntity.builder()
                .id(1L)
                .title("새로운 공지사항")
                .content("공지사항 내용입니다.")
                .priority("HIGH")
                .isActive(true)
                .createdBy("master@email.com")
                .build();

        // Mock 설정: SecurityUtil의 getCurrentUserName 메서드가 호출되면 "master@email.com"을 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserName).thenReturn(Optional.of("master@email.com"));
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of("ROLE_MASTER"));

            // Mock 설정: NoticeRepository의 save 메서드가 호출되면 savedNotice를 반환
            when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(savedNotice);

            // When: 테스트할 메서드 실행
            NoticeEntity result = noticeService.createNotice(noticeDto);

            // Then: 결과 검증
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("새로운 공지사항");
            assertThat(result.getContent()).isEqualTo("공지사항 내용입니다.");
            assertThat(result.getPriority()).isEqualTo("HIGH");
            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getCreatedBy()).isEqualTo("master@email.com");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(noticeRepository).save(any(NoticeEntity.class));
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 일반 사용자가 공지사항 생성 요청 시 실패한다
     */
    @Test
    @DisplayName("공지사항 생성 실패 테스트 - 권한 없음")
    void createNotice_권한없음_실패() {
        // Given: 테스트 데이터 준비
        NoticeDto noticeDto = NoticeDto.builder()
                .title("새로운 공지사항")
                .content("공지사항 내용입니다.")
                .priority("HIGH")
                .build();

        // Mock 설정: SecurityUtil의 getCurrentUserRole 메서드가 호출되면 "ROLE_USER"를 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of("ROLE_USER"));

            // When & Then: 예외 발생 검증
            assertThatThrownBy(() -> noticeService.createNotice(noticeDto))
                    .isInstanceOf(CustomException.class);

            // Mock 객체의 메서드가 호출되지 않았는지 검증
            verify(noticeRepository, never()).save(any(NoticeEntity.class));
        }
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

        // Mock 설정: NoticeRepository의 findByIsActive 메서드가 호출되면 noticePage를 반환
        when(noticeRepository.findByIsActive(eq(true), any(Pageable.class))).thenReturn(noticePage);

        // When: 테스트할 메서드 실행
        Page<NoticeEntity> result = noticeService.getNoticeList(searchDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("공지사항 1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("공지사항 2");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeRepository).findByIsActive(eq(true), any(Pageable.class));
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 키워드로 공지사항 목록 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("키워드로 공지사항 목록 조회 성공 테스트")
    void getNoticeList_키워드검색_성공() {
        // Given: 테스트 데이터 준비
        NoticeSearchDto searchDto = new NoticeSearchDto();
        searchDto.setPage(0);
        searchDto.setSize(10);
        searchDto.setKeyword("테스트");

        NoticeEntity notice = NoticeEntity.builder()
                .id(1L)
                .title("테스트 공지사항")
                .content("테스트 내용")
                .priority("HIGH")
                .isActive(true)
                .build();

        List<NoticeEntity> noticeList = Arrays.asList(notice);
        Page<NoticeEntity> noticePage = new PageImpl<>(noticeList, PageRequest.of(0, 10), 1);

        // Mock 설정: NoticeRepository의 findByTitleOrContentContaining 메서드가 호출되면 noticePage를 반환
        when(noticeRepository.findByTitleOrContentContaining(eq("테스트"), any(Pageable.class))).thenReturn(noticePage);

        // When: 테스트할 메서드 실행
        Page<NoticeEntity> result = noticeService.getNoticeList(searchDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 공지사항");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeRepository).findByTitleOrContentContaining(eq("테스트"), any(Pageable.class));
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

        // Mock 설정: NoticeRepository의 findById 메서드가 호출되면 Optional.of(notice)를 반환
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        // When: 테스트할 메서드 실행
        NoticeEntity result = noticeService.getNoticeDetail(noticeId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(noticeId);
        assertThat(result.getTitle()).isEqualTo("공지사항 제목");
        assertThat(result.getContent()).isEqualTo("공지사항 내용");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeRepository).findById(noticeId);
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

        // Mock 설정: NoticeRepository의 findById 메서드가 호출되면 Optional.empty()를 반환
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.empty());
        
        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("공지사항을 찾을 수 없습니다");

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> noticeService.getNoticeDetail(noticeId))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(noticeRepository).findById(noticeId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 마스터 권한으로 공지사항 수정 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 수정 성공 테스트 - 마스터 권한")
    void updateNotice_마스터권한_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;
        NoticeDto noticeDto = NoticeDto.builder()
                .title("수정된 공지사항")
                .content("수정된 내용")
                .priority("MEDIUM")
                .build();

        NoticeEntity existingNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("기존 공지사항")
                .content("기존 내용")
                .priority("HIGH")
                .isActive(true)
                .build();

        NoticeEntity updatedNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("수정된 공지사항")
                .content("수정된 내용")
                .priority("MEDIUM")
                .isActive(true)
                .build();

        // Mock 설정: SecurityUtil의 getCurrentUserRole 메서드가 호출되면 "ROLE_MASTER"를 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of("ROLE_MASTER"));

            // Mock 설정: NoticeRepository의 findById 메서드가 호출되면 Optional.of(existingNotice)를 반환
            when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(existingNotice));
            
            // Mock 설정: NoticeRepository의 save 메서드가 호출되면 updatedNotice를 반환
            when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(updatedNotice);

            // When: 테스트할 메서드 실행
            NoticeEntity result = noticeService.updateNotice(noticeId, noticeDto);

            // Then: 결과 검증
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(noticeId);
            assertThat(result.getTitle()).isEqualTo("수정된 공지사항");
            assertThat(result.getContent()).isEqualTo("수정된 내용");
            assertThat(result.getPriority()).isEqualTo("MEDIUM");

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(noticeRepository).findById(noticeId);
            verify(noticeRepository).save(any(NoticeEntity.class));
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 마스터 권한으로 공지사항 상태 변경 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 상태 변경 성공 테스트 - 마스터 권한")
    void updateNoticeStatus_마스터권한_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;
        Boolean isActive = false;

        NoticeEntity existingNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .priority("HIGH")
                .isActive(true)
                .build();

        NoticeEntity updatedNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .priority("HIGH")
                .isActive(false)
                .build();

        // Mock 설정: SecurityUtil의 getCurrentUserRole 메서드가 호출되면 "ROLE_MASTER"를 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of("ROLE_MASTER"));

            // Mock 설정: NoticeRepository의 findById 메서드가 호출되면 Optional.of(existingNotice)를 반환
            when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(existingNotice));
            
            // Mock 설정: NoticeRepository의 save 메서드가 호출되면 updatedNotice를 반환
            when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(updatedNotice);

            // When: 테스트할 메서드 실행
            NoticeEntity result = noticeService.updateNoticeStatus(noticeId, isActive);

            // Then: 결과 검증
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(noticeId);
            assertThat(result.getIsActive()).isFalse();

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(noticeRepository).findById(noticeId);
            verify(noticeRepository).save(any(NoticeEntity.class));
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 마스터 권한으로 공지사항 삭제 요청 시 성공한다
     */
    @Test
    @DisplayName("공지사항 삭제 성공 테스트 - 마스터 권한")
    void deleteNotice_마스터권한_성공() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;

        NoticeEntity existingNotice = NoticeEntity.builder()
                .id(noticeId)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .priority("HIGH")
                .isActive(true)
                .build();

        // Mock 설정: SecurityUtil의 getCurrentUserRole 메서드가 호출되면 "ROLE_MASTER"를 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of("ROLE_MASTER"));

            // Mock 설정: NoticeRepository의 findById 메서드가 호출되면 Optional.of(existingNotice)를 반환
            when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(existingNotice));
            
            // Mock 설정: NoticeRepository의 delete 메서드가 호출되면 아무것도 하지 않음
            doNothing().when(noticeRepository).delete(any(NoticeEntity.class));

            // When: 테스트할 메서드 실행
            noticeService.deleteNotice(noticeId);

            // Then: Mock 객체의 메서드가 호출되었는지 검증
            verify(noticeRepository).findById(noticeId);
            verify(noticeRepository).delete(existingNotice);
        }
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 일반 사용자가 비활성화된 공지사항 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("공지사항 상세 조회 실패 테스트 - 비활성화된 공지사항")
    void getNoticeDetailForUser_비활성화된공지사항_실패() {
        // Given: 테스트 데이터 준비
        Long noticeId = 1L;

        NoticeEntity notice = NoticeEntity.builder()
                .id(noticeId)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .priority("HIGH")
                .isActive(false) // 비활성화된 공지사항
                .build();

        // Mock 설정: SecurityUtil의 getCurrentUserRole 메서드가 호출되면 "ROLE_USER"를 반환
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserRole).thenReturn(Optional.of("ROLE_USER"));

            // Mock 설정: NoticeRepository의 findById 메서드가 호출되면 Optional.of(notice)를 반환
            when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

            // When & Then: 예외 발생 검증
            assertThatThrownBy(() -> noticeService.getNoticeDetailForUser(noticeId))
                    .isInstanceOf(CustomException.class);

            // Mock 객체의 메서드가 호출되었는지 검증
            verify(noticeRepository).findById(noticeId);
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
 * 2. NoticeService의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Service 테스트는 비즈니스 로직에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 * - Given-When-Then 패턴을 사용하여 테스트 구조를 명확하게 작성
 * - 정적 메서드는 MockedStatic을 사용하여 Mock 처리
 * - 권한 체크 로직을 포함한 다양한 시나리오 테스트
 */

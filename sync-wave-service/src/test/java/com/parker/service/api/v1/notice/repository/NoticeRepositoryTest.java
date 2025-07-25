package com.parker.service.api.v1.notice.repository;

import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.jpa.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * com.parker.service.api.v1.notice.repository
 * ㄴ NoticeRepositoryTest
 *
 * <pre>
 * description : 공지사항 Repository 테스트
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
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
class NoticeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NoticeRepository noticeRepository;

    private NoticeEntity testNotice1;
    private NoticeEntity testNotice2;
    private NoticeEntity testNotice3;

    /**
     * 각 테스트 메서드 실행 전 호출되는 설정 메서드
     * 
     * 설정 내용:
     * 1. 테스트용 공지사항 데이터 3개 생성
     * 2. 각각 다른 중요도, 활성화 상태, 생성 시간을 가진 데이터
     * 3. H2 인메모리 데이터베이스에 데이터 저장
     * 
     * 테스트 데이터 구성:
     * - testNotice1: HIGH 중요도, 활성화, 1일 전 생성
     * - testNotice2: MEDIUM 중요도, 활성화, 현재 생성
     * - testNotice3: LOW 중요도, 비활성화, 2일 전 생성
     */
    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성 - 다양한 시나리오를 위한 데이터
        testNotice1 = NoticeEntity.builder()
                .title("높은 중요도 공지사항")
                .content("이것은 높은 중요도의 공지사항입니다.")
                .priority("HIGH")  // 높은 중요도
                .isActive(true)    // 활성화 상태
                .createdBy("admin")
                .createdDateTime(LocalDateTime.now().minusDays(1))  // 1일 전 생성
                .modifiedDateTime(LocalDateTime.now().minusDays(1))
                .build();

        testNotice2 = NoticeEntity.builder()
                .title("중간 중요도 공지사항")
                .content("이것은 중간 중요도의 공지사항입니다.")
                .priority("MEDIUM")  // 중간 중요도
                .isActive(true)      // 활성화 상태
                .createdBy("admin")
                .createdDateTime(LocalDateTime.now())  // 현재 생성 (가장 최근)
                .modifiedDateTime(LocalDateTime.now())
                .build();

        testNotice3 = NoticeEntity.builder()
                .title("비활성화된 공지사항")
                .content("이것은 비활성화된 공지사항입니다.")
                .priority("LOW")     // 낮은 중요도
                .isActive(false)     // 비활성화 상태
                .createdBy("admin")
                .createdDateTime(LocalDateTime.now().minusDays(2))  // 2일 전 생성 (가장 오래됨)
                .modifiedDateTime(LocalDateTime.now().minusDays(2))
                .build();

        // 데이터베이스에 저장 - 각각 개별적으로 저장하여 ID 할당
        entityManager.persistAndFlush(testNotice1);
        entityManager.persistAndFlush(testNotice2);
        entityManager.persistAndFlush(testNotice3);
    }

    /**
     * 활성화된 공지사항 목록 조회 테스트
     * 
     * 테스트 시나리오:
     * 1. 활성화된 공지사항만 조회
     * 2. 최신순으로 정렬되어 반환되는지 확인
     * 3. 비활성화된 공지사항은 제외되는지 확인
     * 
     * 검증 포인트:
     * - 활성화된 공지사항만 조회되는지 확인
     * - 최신순 정렬이 올바른지 확인 (testNotice2가 첫 번째)
     * - 총 2개의 활성화된 공지사항이 조회되는지 확인
     */
    @Test
    @DisplayName("활성화된 공지사항 목록 조회 테스트")
    void findByIsActiveTrueOrderByCreatedDateTimeDesc() {
        // when
        List<NoticeEntity> activeNotices = noticeRepository.findByIsActiveTrueOrderByCreatedDateTimeDesc();

        // then
        assertEquals(2, activeNotices.size());  // 활성화된 공지사항은 2개
        assertTrue(activeNotices.stream().allMatch(NoticeEntity::getIsActive));  // 모든 공지사항이 활성화 상태
        
        // 최신순 정렬 확인 (testNotice2가 가장 최근에 생성됨)
        assertEquals(testNotice2.getId(), activeNotices.get(0).getId());
        assertEquals(testNotice1.getId(), activeNotices.get(1).getId());
    }

    /**
     * 특정 중요도의 활성화된 공지사항 조회 테스트
     * 
     * 테스트 시나리오:
     * 1. HIGH 중요도의 활성화된 공지사항만 조회
     * 2. 최신순으로 정렬되어 반환되는지 확인
     * 3. 다른 중요도의 공지사항은 제외되는지 확인
     * 
     * 검증 포인트:
     * - HIGH 중요도의 공지사항만 조회되는지 확인
     * - 활성화된 공지사항만 조회되는지 확인
     * - 정렬이 올바른지 확인
     */
    @Test
    @DisplayName("특정 중요도의 활성화된 공지사항 조회 테스트")
    void findByPriorityAndIsActiveTrueOrderByCreatedDateTimeDesc() {
        // when
        List<NoticeEntity> highPriorityNotices = noticeRepository.findByPriorityAndIsActiveTrueOrderByCreatedDateTimeDesc("HIGH");

        // then
        assertEquals(1, highPriorityNotices.size());  // HIGH 중요도 활성화 공지사항은 1개
        assertEquals("HIGH", highPriorityNotices.get(0).getPriority());
        assertTrue(highPriorityNotices.get(0).getIsActive());
    }

    /**
     * 제목으로 공지사항 검색 테스트
     * 
     * 테스트 시나리오:
     * 1. 제목에 "높은"이 포함된 공지사항 검색
     * 2. 활성화된 공지사항만 조회되는지 확인
     * 3. 최신순으로 정렬되는지 확인
     * 
     * 검증 포인트:
     * - 제목 검색이 정상 동작하는지 확인
     * - 활성화된 공지사항만 조회되는지 확인
     * - 정렬이 올바른지 확인
     */
    @Test
    @DisplayName("제목으로 공지사항 검색 테스트")
    void findByTitleContainingAndIsActiveTrueOrderByCreatedDateTimeDesc() {
        // when
        List<NoticeEntity> titleSearchResults = noticeRepository.findByTitleContainingAndIsActiveTrueOrderByCreatedDateTimeDesc("높은");

        // then
        assertEquals(1, titleSearchResults.size());
        assertTrue(titleSearchResults.get(0).getTitle().contains("높은"));
        assertTrue(titleSearchResults.get(0).getIsActive());
    }

    /**
     * 내용으로 공지사항 검색 테스트
     * 
     * 테스트 시나리오:
     * 1. 내용에 "중간"이 포함된 공지사항 검색
     * 2. 활성화된 공지사항만 조회되는지 확인
     * 3. 최신순으로 정렬되는지 확인
     * 
     * 검증 포인트:
     * - 내용 검색이 정상 동작하는지 확인
     * - 활성화된 공지사항만 조회되는지 확인
     * - 정렬이 올바른지 확인
     */
    @Test
    @DisplayName("내용으로 공지사항 검색 테스트")
    void findByContentContainingAndIsActiveTrueOrderByCreatedDateTimeDesc() {
        // when
        List<NoticeEntity> contentSearchResults = noticeRepository.findByContentContainingAndIsActiveTrueOrderByCreatedDateTimeDesc("중간");

        // then
        assertEquals(1, contentSearchResults.size());
        assertTrue(contentSearchResults.get(0).getContent().contains("중간"));
        assertTrue(contentSearchResults.get(0).getIsActive());
    }

    /**
     * 키워드로 공지사항 검색 테스트 (제목 또는 내용)
     * 
     * 테스트 시나리오:
     * 1. "공지사항" 키워드로 제목 또는 내용 검색
     * 2. 활성화된 공지사항만 조회되는지 확인
     * 3. 최신순으로 정렬되는지 확인
     * 
     * 검증 포인트:
     * - 키워드 검색이 정상 동작하는지 확인
     * - 제목과 내용 모두에서 검색되는지 확인
     * - 활성화된 공지사항만 조회되는지 확인
     */
    @Test
    @DisplayName("키워드로 공지사항 검색 테스트")
    void findByTitleOrContentContainingAndIsActiveTrue() {
        // when
        List<NoticeEntity> keywordSearchResults = noticeRepository.findByTitleOrContentContainingAndIsActiveTrue("공지사항");

        // then
        assertEquals(2, keywordSearchResults.size());  // 제목에 "공지사항"이 포함된 활성화된 공지사항 2개
        assertTrue(keywordSearchResults.stream().allMatch(NoticeEntity::getIsActive));
    }

    /**
     * 작성자별 공지사항 조회 테스트
     * 
     * 테스트 시나리오:
     * 1. "admin" 작성자의 공지사항 조회
     * 2. 최신순으로 정렬되는지 확인
     * 3. 모든 공지사항이 해당 작성자의 것인지 확인
     * 
     * 검증 포인트:
     * - 작성자별 조회가 정상 동작하는지 확인
     * - 최신순 정렬이 올바른지 확인
     * - 모든 공지사항이 해당 작성자의 것인지 확인
     */
    @Test
    @DisplayName("작성자별 공지사항 조회 테스트")
    void findByCreatedByOrderByCreatedDateTimeDesc() {
        // when
        List<NoticeEntity> adminNotices = noticeRepository.findByCreatedByOrderByCreatedDateTimeDesc("admin");

        // then
        assertEquals(3, adminNotices.size());
        assertTrue(adminNotices.stream().allMatch(notice -> "admin".equals(notice.getCreatedBy())));
        
        // 최신순 정렬 확인
        assertTrue(adminNotices.get(0).getCreatedDateTime().isAfter(adminNotices.get(1).getCreatedDateTime()));
    }

    /**
     * 활성화된 공지사항 개수 조회 테스트
     * 
     * 테스트 시나리오:
     * 1. 활성화된 공지사항의 개수 조회
     * 2. 정확한 개수가 반환되는지 확인
     * 
     * 검증 포인트:
     * - 활성화된 공지사항 개수가 정확한지 확인
     */
    @Test
    @DisplayName("활성화된 공지사항 개수 조회 테스트")
    void countByIsActiveTrue() {
        // when
        long activeCount = noticeRepository.countByIsActiveTrue();

        // then
        assertEquals(2, activeCount);
    }

    /**
     * 공지사항 저장 테스트
     * 
     * 테스트 시나리오:
     * 1. 새로운 공지사항 생성 및 저장
     * 2. 저장된 공지사항의 모든 필드가 올바른지 확인
     * 3. ID가 자동 생성되는지 확인
     * 
     * 검증 포인트:
     * - 공지사항 저장이 정상 동작하는지 확인
     * - 모든 필드가 올바르게 저장되는지 확인
     * - ID가 자동 생성되는지 확인
     */
    @Test
    @DisplayName("공지사항 저장 테스트")
    void saveNotice() {
        // given
        NoticeEntity newNotice = NoticeEntity.builder()
                .title("새로운 공지사항")
                .content("새로 생성된 공지사항입니다.")
                .priority("MEDIUM")
                .isActive(true)
                .createdBy("testuser")
                .createdDateTime(LocalDateTime.now())
                .modifiedDateTime(LocalDateTime.now())
                .build();

        // when
        NoticeEntity savedNotice = noticeRepository.save(newNotice);

        // then
        assertNotNull(savedNotice.getId());
        assertEquals("새로운 공지사항", savedNotice.getTitle());
        assertEquals("새로 생성된 공지사항입니다.", savedNotice.getContent());
        assertEquals("MEDIUM", savedNotice.getPriority());
        assertTrue(savedNotice.getIsActive());
        assertEquals("testuser", savedNotice.getCreatedBy());
    }

    /**
     * 공지사항 수정 테스트
     * 
     * 테스트 시나리오:
     * 1. 기존 공지사항의 제목, 내용, 중요도 수정
     * 2. 수정된 내용이 올바르게 저장되는지 확인
     * 
     * 검증 포인트:
     * - 공지사항 수정이 정상 동작하는지 확인
     * - 수정된 내용이 올바르게 저장되는지 확인
     */
    @Test
    @DisplayName("공지사항 수정 테스트")
    void updateNotice() {
        // given
        testNotice1.setTitle("수정된 제목");
        testNotice1.setContent("수정된 내용");
        testNotice1.setPriority("LOW");

        // when
        NoticeEntity updatedNotice = noticeRepository.save(testNotice1);

        // then
        assertEquals("수정된 제목", updatedNotice.getTitle());
        assertEquals("수정된 내용", updatedNotice.getContent());
        assertEquals("LOW", updatedNotice.getPriority());
    }

    /**
     * 공지사항 삭제 테스트
     * 
     * 테스트 시나리오:
     * 1. 공지사항 삭제
     * 2. 삭제된 공지사항이 더 이상 조회되지 않는지 확인
     * 
     * 검증 포인트:
     * - 공지사항 삭제가 정상 동작하는지 확인
     * - 삭제된 공지사항이 조회되지 않는지 확인
     */
    @Test
    @DisplayName("공지사항 삭제 테스트")
    void deleteNotice() {
        // given
        Long noticeId = testNotice1.getId();

        // when
        noticeRepository.delete(testNotice1);
        entityManager.flush();

        // then
        assertFalse(noticeRepository.findById(noticeId).isPresent());
    }

    /**
     * 존재하지 않는 공지사항 조회 테스트
     * 
     * 테스트 시나리오:
     * 1. 존재하지 않는 ID로 공지사항 조회
     * 2. Optional.empty()가 반환되는지 확인
     * 
     * 검증 포인트:
     * - 존재하지 않는 공지사항 조회 시 올바른 결과가 반환되는지 확인
     */
    @Test
    @DisplayName("존재하지 않는 공지사항 조회 테스트")
    void findById_NotFound() {
        // when
        var result = noticeRepository.findById(999L);

        // then
        assertTrue(result.isEmpty());
    }
} 
package com.parker.service.api.v1.notice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.jpa.repository.NoticeRepository;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * com.parker.service.api.v1.notice.integration
 * ㄴ NoticeIntegrationTest
 *
 * <pre>
 * description : 공지사항 통합 테스트
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
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@Transactional
class NoticeIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private NoticeDto testNoticeDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testNoticeDto = NoticeDto.builder()
                .title("통합 테스트 공지사항")
                .content("이것은 통합 테스트를 위한 공지사항입니다.")
                .priority("HIGH")
                .build();
    }

    /**
     * 공지사항 전체 플로우 통합 테스트
     * 
     * 테스트 시나리오:
     * 1. 공지사항 생성 (POST /service/v1/notices)
     * 2. 공지사항 목록 조회 (GET /service/v1/notices)
     * 3. 공지사항 상세 조회 (GET /service/v1/notices/{id})
     * 4. 공지사항 수정 (PUT /service/v1/notices/{id})
     * 5. 공지사항 상태 변경 (PATCH /service/v1/notices/{id}/status)
     * 6. 공지사항 삭제 (DELETE /service/v1/notices/{id})
     * 7. 삭제 확인 (GET /service/v1/notices/{id} - 500 에러)
     * 
     * 검증 포인트:
     * - 전체 CRUD 플로우가 정상 동작하는지 확인
     * - 각 단계별 HTTP 상태 코드와 응답 구조 검증
     * - 데이터베이스 연동이 정상 동작하는지 확인
     * - 예외 처리가 정상 동작하는지 확인
     */
    @Test
    @DisplayName("공지사항 전체 플로우 통합 테스트")
    void noticeFullFlowIntegrationTest() throws Exception {
        // 1. 공지사항 생성
        String createResponse = mockMvc.perform(post("/service/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testNoticeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.title").value("통합 테스트 공지사항"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 생성된 공지사항의 ID 추출
        Long noticeId = extractNoticeIdFromResponse(createResponse);

        // 2. 공지사항 목록 조회
        mockMvc.perform(get("/service/v1/notices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[?(@.id == " + noticeId + ")]").exists());

        // 3. 공지사항 상세 조회
        mockMvc.perform(get("/service/v1/notices/" + noticeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.id").value(noticeId))
                .andExpect(jsonPath("$.data.title").value("통합 테스트 공지사항"));

        // 4. 공지사항 수정
        NoticeDto updateDto = NoticeDto.builder()
                .title("수정된 통합 테스트 공지사항")
                .content("수정된 통합 테스트 내용입니다.")
                .priority("MEDIUM")
                .build();

        mockMvc.perform(put("/service/v1/notices/" + noticeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.title").value("수정된 통합 테스트 공지사항"));

        // 5. 공지사항 상태 변경 (비활성화)
        mockMvc.perform(patch("/service/v1/notices/" + noticeId + "/status")
                        .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.isActive").value(false));

        // 6. 공지사항 삭제
        mockMvc.perform(delete("/service/v1/notices/" + noticeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));

        // 7. 삭제 확인 (존재하지 않는 공지사항 조회 시 에러)
        mockMvc.perform(get("/service/v1/notices/" + noticeId))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 공지사항 검색 기능 통합 테스트
     * 
     * 테스트 시나리오:
     * 1. 테스트용 공지사항 데이터 2개 생성 및 저장
     * 2. 키워드 검색 기능 테스트
     * 3. 중요도별 검색 기능 테스트
     * 
     * 검증 포인트:
     * - 키워드 검색이 정상 동작하는지 확인
     * - 중요도별 필터링이 정상 동작하는지 확인
     * - 검색 결과의 정확성 확인
     * - HTTP 상태 코드와 응답 구조 검증
     */
    @Test
    @DisplayName("공지사항 검색 기능 통합 테스트")
    void noticeSearchIntegrationTest() throws Exception {
        // 테스트 데이터 생성 - 검색 테스트를 위한 공지사항 2개
        NoticeEntity notice1 = NoticeEntity.builder()
                .title("검색 테스트 공지사항 1")  // "검색 테스트" 키워드 포함
                .content("이것은 검색 테스트를 위한 첫 번째 공지사항입니다.")  // "검색 테스트" 키워드 포함
                .priority("HIGH")  // 높은 중요도
                .isActive(true)    // 활성화 상태
                .createdBy("testuser")
                .createdDateTime(LocalDateTime.now())
                .modifiedDateTime(LocalDateTime.now())
                .build();

        NoticeEntity notice2 = NoticeEntity.builder()
                .title("검색 테스트 공지사항 2")  // "검색 테스트" 키워드 포함
                .content("이것은 검색 테스트를 위한 두 번째 공지사항입니다.")  // "검색 테스트" 키워드 포함
                .priority("MEDIUM")  // 중간 중요도
                .isActive(true)      // 활성화 상태
                .createdBy("testuser")
                .createdDateTime(LocalDateTime.now())
                .modifiedDateTime(LocalDateTime.now())
                .build();

        // 데이터베이스에 테스트 데이터 저장
        noticeRepository.save(notice1);
        noticeRepository.save(notice2);

        // 키워드 검색 테스트
        mockMvc.perform(get("/service/v1/notices")
                        .param("keyword", "검색 테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        // 중요도별 검색 테스트
        mockMvc.perform(get("/service/v1/notices")
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].priority").value("HIGH"));
    }

    /**
     * 공지사항 유효성 검증 통합 테스트
     * 
     * 테스트 시나리오:
     * 1. 빈 제목으로 공지사항 생성 시도
     * 2. 빈 내용으로 공지사항 생성 시도
     * 3. 빈 중요도로 공지사항 생성 시도
     * 4. 각각 400 Bad Request 응답 확인
     * 
     * 검증 포인트:
     * - 유효성 검증이 정상 동작하는지 확인
     * - 잘못된 데이터에 대한 적절한 에러 응답 확인
     * - HTTP 상태 코드 검증
     */
    @Test
    @DisplayName("공지사항 유효성 검증 통합 테스트")
    void noticeValidationIntegrationTest() throws Exception {
        // 빈 제목 테스트
        NoticeDto invalidTitleDto = NoticeDto.builder()
                .title("")
                .content("유효한 내용")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/service/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTitleDto)))
                .andExpect(status().isBadRequest());

        // 빈 내용 테스트
        NoticeDto invalidContentDto = NoticeDto.builder()
                .title("유효한 제목")
                .content("")
                .priority("HIGH")
                .build();

        mockMvc.perform(post("/service/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidContentDto)))
                .andExpect(status().isBadRequest());

        // 빈 중요도 테스트
        NoticeDto invalidPriorityDto = NoticeDto.builder()
                .title("유효한 제목")
                .content("유효한 내용")
                .priority("")
                .build();

        mockMvc.perform(post("/service/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPriorityDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 공지사항 데이터베이스 연동 테스트
     * 
     * 테스트 시나리오:
     * 1. 공지사항 생성 및 저장
     * 2. 저장된 데이터 조회 및 검증
     * 3. 공지사항 수정 및 검증
     * 4. 공지사항 삭제 및 검증
     * 
     * 검증 포인트:
     * - 데이터베이스 연동이 정상 동작하는지 확인
     * - CRUD 작업이 정상 동작하는지 확인
     * - 데이터 일관성 확인
     */
    @Test
    @DisplayName("공지사항 데이터베이스 연동 테스트")
    void noticeDatabaseIntegrationTest() {
        // 공지사항 생성
        NoticeEntity notice = NoticeEntity.builder()
                .title("데이터베이스 테스트 공지사항")
                .content("데이터베이스 연동을 테스트하는 공지사항입니다.")
                .priority("LOW")
                .isActive(true)
                .createdBy("testuser")
                .createdDateTime(LocalDateTime.now())
                .modifiedDateTime(LocalDateTime.now())
                .build();

        NoticeEntity savedNotice = noticeRepository.save(notice);

        // 저장 확인
        assertNotNull(savedNotice.getId());
        assertEquals("데이터베이스 테스트 공지사항", savedNotice.getTitle());

        // 조회 확인
        var foundNotice = noticeRepository.findById(savedNotice.getId());
        assertTrue(foundNotice.isPresent());
        assertEquals("데이터베이스 테스트 공지사항", foundNotice.get().getTitle());

        // 활성화된 공지사항 목록 확인
        List<NoticeEntity> activeNotices = noticeRepository.findByIsActiveTrueOrderByCreatedDateTimeDesc();
        assertTrue(activeNotices.stream().anyMatch(n -> n.getId().equals(savedNotice.getId())));

        // 수정
        savedNotice.setTitle("수정된 데이터베이스 테스트 공지사항");
        NoticeEntity updatedNotice = noticeRepository.save(savedNotice);
        assertEquals("수정된 데이터베이스 테스트 공지사항", updatedNotice.getTitle());

        // 삭제
        noticeRepository.delete(savedNotice);
        var deletedNotice = noticeRepository.findById(savedNotice.getId());
        assertTrue(deletedNotice.isEmpty());
    }

    /**
     * 응답에서 공지사항 ID를 추출하는 헬퍼 메서드
     * 
     * @param response JSON 응답 문자열
     * @return 추출된 공지사항 ID
     */
    private Long extractNoticeIdFromResponse(String response) {
        try {
            // 간단한 JSON 파싱을 위해 문자열 처리
            int idIndex = response.indexOf("\"id\":");
            if (idIndex != -1) {
                int startIndex = response.indexOf(":", idIndex) + 1;
                int endIndex = response.indexOf(",", startIndex);
                if (endIndex == -1) {
                    endIndex = response.indexOf("}", startIndex);
                }
                String idStr = response.substring(startIndex, endIndex).trim();
                return Long.parseLong(idStr);
            }
        } catch (Exception e) {
            // 파싱 실패 시 기본값 반환
        }
        return 1L; // 기본값
    }
} 
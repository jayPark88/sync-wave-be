package com.parker.service.api.v1.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import com.parker.service.api.v1.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * com.parker.service.api.v1.notice.controller
 * ㄴ NoticeControllerTest
 *
 * <pre>
 * description : 공지사항 컨트롤러 테스트
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
class NoticeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NoticeService noticeService;

    @InjectMocks
    private NoticeController noticeController;

    private ObjectMapper objectMapper;

    private NoticeEntity testNotice;
    private NoticeDto testNoticeDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(noticeController).build();
        objectMapper = new ObjectMapper();

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
     * 공지사항 생성 API 테스트 - 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. POST /v1/notices 엔드포인트 호출
     * 2. JSON 형태의 공지사항 데이터 전송
     * 3. HTTP 상태 코드 201 (Created) 반환 확인
     * 4. 응답 JSON 구조 검증 (result, data)
     * 5. Service 메서드 호출 확인
     */
    @Test
    @DisplayName("공지사항 생성 API 테스트 - 성공")
    void createNotice_Success() throws Exception {
        // given - 테스트 준비 단계
        when(noticeService.createNotice(any(NoticeDto.class))).thenReturn(testNotice);

        // when & then - 테스트 실행 및 검증 단계
        mockMvc.perform(post("/v1/notices")  // POST 요청
                        .contentType(MediaType.APPLICATION_JSON)  // JSON 컨텐츠 타입
                        .content(objectMapper.writeValueAsString(testNoticeDto)))  // JSON 데이터 전송
                .andExpect(status().isCreated())  // HTTP 201 상태 코드 확인
                .andExpect(jsonPath("$.result").value(true))  // 응답의 result 필드가 true인지 확인
                .andExpect(jsonPath("$.data.title").value("테스트 공지사항"));  // data 필드의 제목 검증

        // Service의 createNotice 메서드가 호출되었는지 검증
        verify(noticeService).createNotice(any(NoticeDto.class));
    }

    /**
     * 공지사항 목록 조회 API 테스트 - 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. GET /v1/notices 엔드포인트 호출
     * 2. HTTP 상태 코드 200 (OK) 반환 확인
     * 3. 응답 JSON 구조 검증 (result, data 배열)
     * 4. 반환된 배열의 첫 번째 요소 검증
     * 5. Service 메서드 호출 확인
     */
    @Test
    @DisplayName("공지사항 목록 조회 API 테스트 - 성공")
    void getNoticeList_Success() throws Exception {
        // given - 테스트 준비 단계
        List<NoticeEntity> noticeList = Arrays.asList(testNotice);
        // Service 모킹: 공지사항 목록 반환
        when(noticeService.getNoticeList(any(NoticeSearchDto.class))).thenReturn(noticeList);

        // when & then - 테스트 실행 및 검증 단계
        mockMvc.perform(get("/v1/notices"))  // GET 요청
                .andExpect(status().isOk())  // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.result").value(true))  // 응답의 result 필드가 true인지 확인
                .andExpect(jsonPath("$.data").isArray())  // data 필드가 배열인지 확인
                .andExpect(jsonPath("$.data[0].title").value("테스트 공지사항"));  // 첫 번째 요소의 제목 검증

        // Service의 getNoticeList 메서드가 호출되었는지 검증
        verify(noticeService).getNoticeList(any(NoticeSearchDto.class));
    }

    @Test
    @DisplayName("공지사항 목록 조회 API 테스트 - 검색 조건 포함")
    void getNoticeList_WithSearchCriteria() throws Exception {
        // given
        List<NoticeEntity> noticeList = Arrays.asList(testNotice);
        when(noticeService.getNoticeList(any(NoticeSearchDto.class))).thenReturn(noticeList);

        // when & then
        mockMvc.perform(get("/v1/notices")
                        .param("keyword", "테스트")
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(noticeService).getNoticeList(any(NoticeSearchDto.class));
    }

    /**
     * 공지사항 상세 조회 API 테스트 - 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. GET /v1/notices/{noticeId} 엔드포인트 호출
     * 2. HTTP 상태 코드 200 (OK) 반환 확인
     * 3. 응답 JSON 구조 검증 (result, data)
     * 4. 반환된 공지사항 데이터 검증
     * 5. Service 메서드 호출 확인
     */
    @Test
    @DisplayName("공지사항 상세 조회 API 테스트 - 성공")
    void getNoticeDetail_Success() throws Exception {
        // given - 테스트 준비 단계
        when(noticeService.getNoticeDetail(1L)).thenReturn(testNotice);

        // when & then - 테스트 실행 및 검증 단계
        mockMvc.perform(get("/v1/notices/1"))  // GET 요청
                .andExpect(status().isOk())  // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.result").value(true))  // 응답의 result 필드가 true인지 확인
                .andExpect(jsonPath("$.data.title").value("테스트 공지사항"))  // data 필드의 제목 검증
                .andExpect(jsonPath("$.data.content").value("테스트 공지사항 내용입니다."));  // data 필드의 내용 검증

        // Service의 getNoticeDetail 메서드가 호출되었는지 검증
        verify(noticeService).getNoticeDetail(1L);
    }

    /**
     * 공지사항 수정 API 테스트 - 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. PUT /v1/notices/{noticeId} 엔드포인트 호출
     * 2. JSON 형태의 수정 데이터 전송
     * 3. HTTP 상태 코드 200 (OK) 반환 확인
     * 4. 응답 JSON 구조 검증 (result, data)
     * 5. Service 메서드 호출 확인
     */
    @Test
    @DisplayName("공지사항 수정 API 테스트 - 성공")
    void updateNotice_Success() throws Exception {
        // given - 테스트 준비 단계
        when(noticeService.updateNotice(eq(1L), any(NoticeDto.class))).thenReturn(testNotice);

        // when & then - 테스트 실행 및 검증 단계
        mockMvc.perform(put("/v1/notices/1")  // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON)  // JSON 컨텐츠 타입
                        .content(objectMapper.writeValueAsString(testNoticeDto)))  // JSON 데이터 전송
                .andExpect(status().isOk())  // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.result").value(true))  // 응답의 result 필드가 true인지 확인
                .andExpect(jsonPath("$.data.title").value("테스트 공지사항"));  // data 필드의 제목 검증

        // Service의 updateNotice 메서드가 호출되었는지 검증
        verify(noticeService).updateNotice(eq(1L), any(NoticeDto.class));
    }

    /**
     * 공지사항 상태 변경 API 테스트 - 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. PATCH /v1/notices/{noticeId}/status 엔드포인트 호출
     * 2. 활성화 상태 파라미터 전송
     * 3. HTTP 상태 코드 200 (OK) 반환 확인
     * 4. 응답 JSON 구조 검증 (result, data)
     * 5. Service 메서드 호출 확인
     */
    @Test
    @DisplayName("공지사항 상태 변경 API 테스트 - 성공")
    void updateNoticeStatus_Success() throws Exception {
        // given - 테스트 준비 단계
        when(noticeService.updateNoticeStatus(eq(1L), eq(false))).thenReturn(testNotice);

        // when & then - 테스트 실행 및 검증 단계
        mockMvc.perform(patch("/v1/notices/1/status")  // PATCH 요청
                        .param("isActive", "false"))  // 활성화 상태 파라미터
                .andExpect(status().isOk())  // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.result").value(true))  // 응답의 result 필드가 true인지 확인
                .andExpect(jsonPath("$.data.title").value("테스트 공지사항"));  // data 필드의 제목 검증

        // Service의 updateNoticeStatus 메서드가 호출되었는지 검증
        verify(noticeService).updateNoticeStatus(eq(1L), eq(false));
    }

    /**
     * 공지사항 삭제 API 테스트 - 성공 케이스
     * 
     * 테스트 시나리오:
     * 1. DELETE /v1/notices/{noticeId} 엔드포인트 호출
     * 2. HTTP 상태 코드 200 (OK) 반환 확인
     * 3. 응답 JSON 구조 검증 (result, data는 null)
     * 4. Service 메서드 호출 확인
     */
    @Test
    @DisplayName("공지사항 삭제 API 테스트 - 성공")
    void deleteNotice_Success() throws Exception {
        // given - 테스트 준비 단계
        doNothing().when(noticeService).deleteNotice(1L);

        // when & then - 테스트 실행 및 검증 단계
        mockMvc.perform(delete("/v1/notices/1"))  // DELETE 요청
                .andExpect(status().isOk())  // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.result").value(true))  // 응답의 result 필드가 true인지 확인
                .andExpect(jsonPath("$.data").isEmpty());  // data 필드가 null인지 확인

        // Service의 deleteNotice 메서드가 호출되었는지 검증
        verify(noticeService).deleteNotice(1L);
    }
} 
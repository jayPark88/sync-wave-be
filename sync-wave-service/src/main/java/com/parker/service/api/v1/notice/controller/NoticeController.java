package com.parker.service.api.v1.notice.controller;

import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import com.parker.service.api.v1.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * com.parker.service.api.v1.notice.controller
 * ㄴ NoticeController
 *
 * <pre>
 * description : 공지사항 REST API 컨트롤러
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
@Tag(name = "Notice", description = "공지사항 API")
@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 생성 (ROLE_MASTER만 가능)
     * @param noticeDto 공지사항 생성 정보
     * @return 생성된 공지사항
     */
    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 생성합니다. (ROLE_MASTER만 가능)")
    @PostMapping
    public CommonResponse<NoticeEntity> createNotice(@Valid @RequestBody NoticeDto noticeDto) {
        log.info("공지사항 생성 요청: {}", noticeDto.getTitle());
        
        NoticeEntity createdNotice = noticeService.createNotice(noticeDto);
        
        return new CommonResponse<>(createdNotice);
    }

    /**
     * 공지사항 목록 조회 (모든 사용자 가능) - 페이징 지원
     * @param searchDto 검색 조건
     * @return 페이징된 공지사항 목록
     */
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 페이징하여 조회합니다. (모든 사용자 가능)")
    @GetMapping
    public CommonResponse<Page<NoticeEntity>> getNoticeList(
            @ModelAttribute NoticeSearchDto searchDto) {
        log.info("공지사항 목록 조회 요청");
        
        Page<NoticeEntity> noticePage = noticeService.getNoticeList(searchDto);
        
        return new CommonResponse<>(noticePage);
    }

    /**
     * 공지사항 상세 조회 (모든 사용자 가능)
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    @Operation(summary = "공지사항 상세 조회", description = "특정 공지사항의 상세 정보를 조회합니다. (모든 사용자 가능)")
    @GetMapping("/{noticeId}")
    public CommonResponse<NoticeEntity> getNoticeDetail(@PathVariable("noticeId") Long noticeId) {
        log.info("공지사항 상세 조회 요청: {}", noticeId);
        
        NoticeEntity notice = noticeService.getNoticeDetail(noticeId);
        
        return new CommonResponse<>(notice);
    }

    /**
     * 공지사항 수정 (ROLE_MASTER만 가능)
     * @param noticeId 공지사항 ID
     * @param noticeDto 수정할 공지사항 정보
     * @return 수정된 공지사항
     */
    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다. (ROLE_MASTER만 가능)")
    @PutMapping("/{noticeId}")
    public CommonResponse<NoticeEntity> updateNotice(
            @PathVariable("noticeId") Long noticeId, 
            @Valid @RequestBody NoticeDto noticeDto) {
        log.info("공지사항 수정 요청: {}", noticeId);
        
        NoticeEntity updatedNotice = noticeService.updateNotice(noticeId, noticeDto);
        
        return new CommonResponse<>(updatedNotice);
    }

    /**
     * 공지사항 활성화/비활성화 (ROLE_MASTER만 가능)
     * @param noticeId 공지사항 ID
     * @param isActive 활성화 상태
     * @return 수정된 공지사항
     */
    @Operation(summary = "공지사항 상태 변경", description = "공지사항의 활성화 상태를 변경합니다. (ROLE_MASTER만 가능)")
    @PatchMapping("/{noticeId}/status")
    public CommonResponse<NoticeEntity> updateNoticeStatus(
            @PathVariable("noticeId") Long noticeId, 
            @RequestParam("isActive") Boolean isActive) {
        log.info("공지사항 상태 변경 요청: {}, isActive: {}", noticeId, isActive);
        
        NoticeEntity updatedNotice = noticeService.updateNoticeStatus(noticeId, isActive);
        
        return new CommonResponse<>(updatedNotice);
    }

    /**
     * 공지사항 삭제 (ROLE_MASTER만 가능)
     * @param noticeId 공지사항 ID
     * @return 삭제 결과
     */
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다. (ROLE_MASTER만 가능)")
    @DeleteMapping("/{noticeId}")
    public CommonResponse<String> deleteNotice(@PathVariable("noticeId") Long noticeId) {
        log.info("공지사항 삭제 요청: {}", noticeId);
        
        noticeService.deleteNotice(noticeId);
        
        return new CommonResponse<>("공지사항이 삭제되었습니다.");
    }
} 
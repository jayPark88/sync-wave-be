package com.parker.service.api.v1.notice.dto;

import lombok.*;

/**
 * com.parker.service.api.v1.notice.dto
 * ㄴ NoticeSearchDto
 *
 * <pre>
 * description : 공지사항 검색 DTO
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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSearchDto {
    
    private String title; // 제목 검색 키워드
    private String content; // 내용 검색 키워드
    private String keyword; // 제목 또는 내용 검색 키워드
    private String priority; // 중요도 필터 (HIGH, MEDIUM, LOW)
    private Boolean isActive; // 활성화 상태 필터 (true, false)
    
    // 페이징 파라미터
    @Builder.Default
    private Integer page = 0; // 페이지 번호 (0부터 시작)
    @Builder.Default
    private Integer size = 10; // 페이지 크기
} 
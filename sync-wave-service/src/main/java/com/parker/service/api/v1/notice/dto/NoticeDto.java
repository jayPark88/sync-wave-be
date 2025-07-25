package com.parker.service.api.v1.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * com.parker.service.api.v1.notice.dto
 * ㄴ NoticeDto
 *
 * <pre>
 * description : 공지사항 생성/수정 DTO
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
public class NoticeDto {
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title; // 공지사항 제목
    
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
    private String content; // 공지사항 내용
    
    @NotBlank(message = "중요도는 필수입니다.")
    private String priority; // 중요도 (HIGH, MEDIUM, LOW)
} 
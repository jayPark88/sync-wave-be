package com.parker.service.api.v1.notice.repository;

import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * com.parker.service.api.v1.notice.repository
 * ㄴ NoticeRepositoryCustom
 *
 * <pre>
 * description : 공지사항 Custom Repository 인터페이스 (QueryDSL 사용)
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
public interface NoticeRepositoryCustom {
    
    /**
     * 동적 조건으로 공지사항 검색 (QueryDSL 사용)
     * @param searchDto 검색 조건
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    Page<NoticeEntity> searchNotices(NoticeSearchDto searchDto, Pageable pageable);
}


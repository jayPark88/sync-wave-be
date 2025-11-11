package com.parker.service.api.v1.notice.repository;

import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.jpa.entity.QNoticeEntity;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import com.parker.service.api.v1.notice.repository.NoticeRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * com.parker.service.api.v1.notice.repository
 * ㄴ NoticeRepositoryCustomImpl
 *
 * <pre>
 * description : 공지사항 Custom Repository 구현체 (QueryDSL 사용)
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
@Component
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    
    private static final QNoticeEntity notice = QNoticeEntity.noticeEntity;

    @Override
    public Page<NoticeEntity> searchNotices(NoticeSearchDto searchDto, Pageable pageable) {
        // QueryDSL을 사용하여 동적 쿼리 생성
        BooleanBuilder builder = buildBooleanBuilder(searchDto);

        // 동적 조건으로 검색 (모든 조건을 조합하여 검색)
        List<NoticeEntity> content = queryFactory
                .selectFrom(notice)
                .where(builder)
                .orderBy(notice.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        // 전체 개수 조회
        Long total = queryFactory
                .select(notice.count())
                .from(notice)
                .where(builder)
                .fetchOne();
        
        return PageableExecutionUtils.getPage(content, pageable, () -> total != null ? total : 0L);
    }
    
    /**
     * 검색 조건에 따라 BooleanBuilder 생성
     * 모든 조건이 null이거나 비어있으면 빈 BooleanBuilder 반환 (전체 조회)
     * @param searchDto 검색 조건 (null 가능)
     * @return BooleanBuilder
     */
    private BooleanBuilder buildBooleanBuilder(NoticeSearchDto searchDto) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // searchDto가 null이면 빈 BooleanBuilder 반환 (전체 조회)
        if (searchDto == null) {
            return builder;
        }
        
        // isActive 조건
        if (searchDto.getIsActive() != null) {
            builder.and(notice.isActive.eq(searchDto.getIsActive()));
        }
        
        // keyword 조건 (제목 또는 내용에 포함)
        if (StringUtils.hasText(searchDto.getKeyword())) {
            String keyword = searchDto.getKeyword().trim();
            builder.and(
                notice.title.containsIgnoreCase(keyword)
                    .or(notice.content.containsIgnoreCase(keyword))
            );
        }
        
        // title 조건
        if (StringUtils.hasText(searchDto.getTitle())) {
            builder.and(notice.title.containsIgnoreCase(searchDto.getTitle().trim()));
        }
        
        // content 조건
        if (StringUtils.hasText(searchDto.getContent())) {
            builder.and(notice.content.containsIgnoreCase(searchDto.getContent().trim()));
        }
        
        // priority 조건
        if (StringUtils.hasText(searchDto.getPriority())) {
            builder.and(notice.priority.eq(searchDto.getPriority().trim()));
        }
        
        return builder;
    }
}


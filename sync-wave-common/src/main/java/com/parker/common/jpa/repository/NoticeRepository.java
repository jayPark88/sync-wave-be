package com.parker.common.jpa.repository;

import com.parker.common.jpa.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * com.parker.common.jpa.repository
 * ㄴ NoticeRepository
 *
 * <pre>
 * description : 공지사항 Repository
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
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    
    /**
     * 활성화된 공지사항 목록을 최신순으로 조회
     * @return 활성화된 공지사항 목록
     */
    List<NoticeEntity> findByIsActiveTrueOrderByCreatedDateTimeDesc();
    
    /**
     * 특정 중요도의 활성화된 공지사항 목록을 최신순으로 조회
     * @param priority 중요도
     * @return 해당 중요도의 활성화된 공지사항 목록
     */
    List<NoticeEntity> findByPriorityAndIsActiveTrueOrderByCreatedDateTimeDesc(String priority);
    
    /**
     * 제목으로 공지사항 검색 (활성화된 것만)
     * @param title 검색할 제목
     * @return 해당 제목의 활성화된 공지사항 목록
     */
    List<NoticeEntity> findByTitleContainingAndIsActiveTrueOrderByCreatedDateTimeDesc(String title);
    
    /**
     * 내용으로 공지사항 검색 (활성화된 것만)
     * @param content 검색할 내용
     * @return 해당 내용이 포함된 활성화된 공지사항 목록
     */
    List<NoticeEntity> findByContentContainingAndIsActiveTrueOrderByCreatedDateTimeDesc(String content);
    
    /**
     * 제목 또는 내용으로 공지사항 검색 (활성화된 것만)
     * @param keyword 검색 키워드
     * @return 제목 또는 내용에 키워드가 포함된 활성화된 공지사항 목록
     */
    @Query("SELECT n FROM NoticeEntity n WHERE n.isActive = true AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) ORDER BY n.createdDateTime DESC")
    List<NoticeEntity> findByTitleOrContentContainingAndIsActiveTrue(@Param("keyword") String keyword);
    
    /**
     * 작성자별 공지사항 목록 조회
     * @param createdBy 작성자
     * @return 해당 작성자의 공지사항 목록 (최신순)
     */
    List<NoticeEntity> findByCreatedByOrderByCreatedDateTimeDesc(String createdBy);
    
    /**
     * 활성화된 공지사항 개수 조회
     * @return 활성화된 공지사항 개수
     */
    long countByIsActiveTrue();
    
    /**
     * 모든 공지사항 목록을 최신순으로 조회 (활성화 상태 무관)
     * @return 모든 공지사항 목록
     */
    List<NoticeEntity> findAllByOrderByCreatedDateTimeDesc();
    
    /**
     * 특정 활성화 상태의 공지사항 목록을 최신순으로 조회
     * @param isActive 활성화 상태
     * @return 해당 상태의 공지사항 목록
     */
    List<NoticeEntity> findByIsActiveOrderByCreatedDateTimeDesc(Boolean isActive);
    
    /**
     * 특정 중요도의 공지사항 목록을 최신순으로 조회 (활성화 상태 무관)
     * @param priority 중요도
     * @return 해당 중요도의 공지사항 목록
     */
    List<NoticeEntity> findByPriorityOrderByCreatedDateTimeDesc(String priority);
    
    /**
     * 제목으로 공지사항 검색 (활성화 상태 무관)
     * @param title 검색할 제목
     * @return 해당 제목의 공지사항 목록
     */
    List<NoticeEntity> findByTitleContainingOrderByCreatedDateTimeDesc(String title);
    
    /**
     * 내용으로 공지사항 검색 (활성화 상태 무관)
     * @param content 검색할 내용
     * @return 해당 내용이 포함된 공지사항 목록
     */
    List<NoticeEntity> findByContentContainingOrderByCreatedDateTimeDesc(String content);
    
    /**
     * 제목 또는 내용으로 공지사항 검색 (활성화 상태 무관)
     * @param keyword 검색 키워드
     * @return 제목 또는 내용에 키워드가 포함된 공지사항 목록
     */
    @Query("SELECT n FROM NoticeEntity n WHERE n.title LIKE %:keyword% OR n.content LIKE %:keyword% ORDER BY n.createdDateTime DESC")
    List<NoticeEntity> findByTitleOrContentContaining(@Param("keyword") String keyword);
    
    // 페이징 메서드들
    /**
     * 모든 공지사항을 페이징하여 조회
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    Page<NoticeEntity> findAll(Pageable pageable);
    
    /**
     * 특정 활성화 상태의 공지사항을 페이징하여 조회
     * @param isActive 활성화 상태
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    Page<NoticeEntity> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * 제목으로 공지사항 검색 (페이징)
     * @param title 검색할 제목
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    Page<NoticeEntity> findByTitleContaining(String title, Pageable pageable);
    
    /**
     * 내용으로 공지사항 검색 (페이징)
     * @param content 검색할 내용
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    Page<NoticeEntity> findByContentContaining(String content, Pageable pageable);
    
    /**
     * 중요도로 공지사항 검색 (페이징)
     * @param priority 중요도
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    Page<NoticeEntity> findByPriority(String priority, Pageable pageable);
    
    /**
     * 제목 또는 내용으로 공지사항 검색 (페이징)
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 공지사항 목록
     */
    @Query("SELECT n FROM NoticeEntity n WHERE n.title LIKE %:keyword% OR n.content LIKE %:keyword%")
    Page<NoticeEntity> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
} 
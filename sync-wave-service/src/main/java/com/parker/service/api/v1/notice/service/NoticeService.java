package com.parker.service.api.v1.notice.service;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.NoticeEntity;
import com.parker.common.jpa.repository.NoticeRepository;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.notice.dto.NoticeDto;
import com.parker.service.api.v1.notice.dto.NoticeSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

/**
 * com.parker.service.api.v1.notice.service
 * ㄴ NoticeService
 *
 * <pre>
 * description : 공지사항 서비스
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
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MessageSource messageSource;

    /**
     * 공지사항 생성 (ROLE_MASTER만 가능)
     * @param noticeDto 공지사항 생성 정보
     * @return 생성된 공지사항
     */
    @Transactional
    public NoticeEntity createNotice(NoticeDto noticeDto) {
        // 권한 체크: ROLE_MASTER만 공지사항 생성 가능
        checkMasterPermission();
        
        String currentUser = SecurityUtil.getCurrentUserName()
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    "사용자 정보를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR));

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .priority(noticeDto.getPriority())
                .isActive(true)
                .createdBy(currentUser)
                .build();

        return noticeRepository.save(noticeEntity);
    }

    /**
     * 공지사항 목록 조회 (모든 사용자 가능)
     * @param searchDto 검색 조건
     * @return 공지사항 목록
     */
    public List<NoticeEntity> getNoticeList(NoticeSearchDto searchDto) {
        log.info("공지사항 목록 조회 요청 - searchDto: {}", searchDto);
        
        if (searchDto == null) {
            // 검색 조건이 없으면 모든 공지사항 조회
            log.info("검색 조건이 없어 모든 공지사항 조회");
            return noticeRepository.findAllByOrderByCreatedDateTimeDesc();
        }

        // isActive 필터링이 명시적으로 지정된 경우 (true/false)
        log.info("isActive 필터링: {}", searchDto.getIsActive());
        if (searchDto.getIsActive() != null) {
            if (searchDto.getKeyword() != null && !searchDto.getKeyword().trim().isEmpty()) {
                // 키워드 검색 + 활성화 상태 필터링
                List<NoticeEntity> allNotices = noticeRepository.findByTitleOrContentContaining(searchDto.getKeyword().trim());
                return allNotices.stream()
                        .filter(notice -> notice.getIsActive().equals(searchDto.getIsActive()))
                        .toList();
            }

            if (searchDto.getTitle() != null && !searchDto.getTitle().trim().isEmpty()) {
                // 제목 검색 + 활성화 상태 필터링
                List<NoticeEntity> allNotices = noticeRepository.findByTitleContainingOrderByCreatedDateTimeDesc(searchDto.getTitle().trim());
                return allNotices.stream()
                        .filter(notice -> notice.getIsActive().equals(searchDto.getIsActive()))
                        .toList();
            }

            if (searchDto.getContent() != null && !searchDto.getContent().trim().isEmpty()) {
                // 내용 검색 + 활성화 상태 필터링
                List<NoticeEntity> allNotices = noticeRepository.findByContentContainingOrderByCreatedDateTimeDesc(searchDto.getContent().trim());
                return allNotices.stream()
                        .filter(notice -> notice.getIsActive().equals(searchDto.getIsActive()))
                        .toList();
            }

            if (searchDto.getPriority() != null && !searchDto.getPriority().trim().isEmpty()) {
                // 중요도 검색 + 활성화 상태 필터링
                List<NoticeEntity> allNotices = noticeRepository.findByPriorityOrderByCreatedDateTimeDesc(searchDto.getPriority().trim());
                return allNotices.stream()
                        .filter(notice -> notice.getIsActive().equals(searchDto.getIsActive()))
                        .toList();
            }

            // 활성화 상태만으로 필터링
            return noticeRepository.findByIsActiveOrderByCreatedDateTimeDesc(searchDto.getIsActive());
        }

        // isActive가 null인 경우 (모든 상태 조회) 또는 isActive 필터링이 지정되지 않은 경우
        log.info("모든 상태 조회 또는 필터링 미지정");
        if (searchDto.getKeyword() != null && !searchDto.getKeyword().trim().isEmpty()) {
            return noticeRepository.findByTitleOrContentContaining(searchDto.getKeyword().trim());
        }

        if (searchDto.getTitle() != null && !searchDto.getTitle().trim().isEmpty()) {
            return noticeRepository.findByTitleContainingOrderByCreatedDateTimeDesc(searchDto.getTitle().trim());
        }

        if (searchDto.getContent() != null && !searchDto.getContent().trim().isEmpty()) {
            return noticeRepository.findByContentContainingOrderByCreatedDateTimeDesc(searchDto.getContent().trim());
        }

        if (searchDto.getPriority() != null && !searchDto.getPriority().trim().isEmpty()) {
            return noticeRepository.findByPriorityOrderByCreatedDateTimeDesc(searchDto.getPriority().trim());
        }

        // 기본적으로 모든 공지사항 조회
        return noticeRepository.findAllByOrderByCreatedDateTimeDesc();
    }

    /**
     * 공지사항 상세 조회 (모든 사용자 가능)
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    public NoticeEntity getNoticeDetail(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    messageSource.getMessage("notice.not.found", null, Locale.getDefault()), 
                    HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * 공지사항 수정 (ROLE_MASTER만 가능)
     * @param noticeId 공지사항 ID
     * @param noticeDto 수정할 공지사항 정보
     * @return 수정된 공지사항
     */
    @Transactional
    public NoticeEntity updateNotice(Long noticeId, NoticeDto noticeDto) {
        // 권한 체크: ROLE_MASTER만 공지사항 수정 가능
        checkMasterPermission();

        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    messageSource.getMessage("notice.not.found", null, Locale.getDefault()), 
                    HttpStatus.INTERNAL_SERVER_ERROR));

        noticeEntity.setTitle(noticeDto.getTitle());
        noticeEntity.setContent(noticeDto.getContent());
        noticeEntity.setPriority(noticeDto.getPriority());

        return noticeRepository.save(noticeEntity);
    }

    /**
     * 공지사항 활성화/비활성화 (ROLE_MASTER만 가능)
     * @param noticeId 공지사항 ID
     * @param isActive 활성화 상태
     * @return 수정된 공지사항
     */
    @Transactional
    public NoticeEntity updateNoticeStatus(Long noticeId, Boolean isActive) {
        // 권한 체크: ROLE_MASTER만 공지사항 상태 변경 가능
        checkMasterPermission();

        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    messageSource.getMessage("notice.not.found", null, Locale.getDefault()), 
                    HttpStatus.INTERNAL_SERVER_ERROR));

        noticeEntity.setIsActive(isActive);

        return noticeRepository.save(noticeEntity);
    }

    /**
     * 공지사항 삭제 (ROLE_MASTER만 가능)
     * @param noticeId 공지사항 ID
     */
    @Transactional
    public void deleteNotice(Long noticeId) {
        // 권한 체크: ROLE_MASTER만 공지사항 삭제 가능
        checkMasterPermission();

        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    messageSource.getMessage("notice.not.found", null, Locale.getDefault()), 
                    HttpStatus.INTERNAL_SERVER_ERROR));

        noticeRepository.delete(noticeEntity);
    }

    /**
     * ROLE_MASTER 권한 체크
     * @throws CustomException 권한이 없는 경우
     */
    private void checkMasterPermission() {
        String currentUserRole = SecurityUtil.getCurrentUserRole()
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    "사용자 권한 정보를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR));

        if (!"ROLE_MASTER".equals(currentUserRole)) {
            throw new CustomException(FAIL_500.code(), 
                "공지사항 관리 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
} 
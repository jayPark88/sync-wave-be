package com.parker.service.api.v1.schedules.service;

import com.parker.service.api.v1.schedules.dto.SchedulesDto;
import com.parker.service.api.v1.schedules.dto.SearchSchedulesDto;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.SchedulesEntity;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.SchedulesRepository;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_400;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulesService {
    private final SchedulesRepository schedulesRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final UserService userService;

    /**
     * 스케쥴 등록
     *
     * @param schedulesDto
     * @return
     */
    public SchedulesEntity createSchedules(SchedulesDto schedulesDto) {
        Long userId = userService.getUserId();
        return schedulesRepository.save(SchedulesEntity.builder()
                .title(schedulesDto.getTitle())
                .description(schedulesDto.getDescription())
                .startDateTime(schedulesDto.getStartDateTime())
                .endDateTime(schedulesDto.getEndDateTime())
                .userId(userId)
                .build());
    }

    /**
     * 스케쥴 조회
     *
     * @param scheduleId
     * @return
     */
    public SchedulesEntity getDetailScheduleDetailInfo(Long scheduleId) {

        Optional<SchedulesEntity> optionalSchedulesEntity = schedulesRepository.findById(scheduleId);
        if (optionalSchedulesEntity.isPresent()) {
            return optionalSchedulesEntity.get();
        } else {
            throw new CustomException(FAIL_400.code(), messageSource.getMessage("schedules.data.not.found", null, Locale.getDefault()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 스케쥴 리스트 검색 기능
     *
     * @param searchSchedulesDto
     * @return
     */
    public List<SchedulesEntity> getDetailScheduleList(SearchSchedulesDto searchSchedulesDto) {
        Long userId = userService.getUserId();
        List<SchedulesEntity> schedulesEntityList;
        
        // 날짜 범위가 지정된 경우와 아닌 경우를 구분
        if (searchSchedulesDto.getStartDate() != null && searchSchedulesDto.getEndDate() != null) {
            // 날짜 범위로 조회
            schedulesEntityList = schedulesRepository.findByUserIdAndStartDateTimeBetween(
                userId, 
                searchSchedulesDto.getStartDate().atStartOfDay(), 
                searchSchedulesDto.getEndDate().atTime(23, 59, 59)
            );
        } else {
            // 모든 일정 조회
            schedulesEntityList = schedulesRepository.findByUserId(userId);
        }

        if (!schedulesEntityList.isEmpty()) {
            if (!ObjectUtils.isEmpty(searchSchedulesDto.getTitle())) {
                schedulesEntityList = schedulesEntityList.stream()
                        .filter(item -> item.getTitle().contains(searchSchedulesDto.getTitle()))
                        .toList();
            }
            if (!ObjectUtils.isEmpty(searchSchedulesDto.getDescription())) {
                schedulesEntityList = schedulesEntityList.stream()
                        .filter(item -> item.getDescription().contains(searchSchedulesDto.getDescription()))
                        .toList();
            }
            return schedulesEntityList;
        } else {
            log.info("schedulesEntityList is empty");
            return new ArrayList<>();
        }
    }

    /**
     * @param scheduleId
     * @param schedulesDto
     */
    @Transactional
    public SchedulesEntity modifyScheduleInfo(Long scheduleId, SchedulesDto schedulesDto) {
        if (SecurityUtil.getCurrentUserName().isPresent() && !userService.checkUserCheck(SecurityUtil.getCurrentUserName().get())) {
            throw new CustomException(FAIL_500.code(),
                    messageSource.getMessage("user.un.auth", null, Locale.getDefault()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return schedulesRepository.findById(scheduleId).map(
                existingSchedule -> {
                    updateFields(existingSchedule, schedulesDto);
                    return schedulesRepository.save(existingSchedule);
                }
        ).orElseThrow(
                () -> new CustomException(FAIL_500.code(),
                        messageSource.getMessage("schedules.data.not.found", null, Locale.getDefault()),
                        HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    /**
     * @param scheduleId
     */
    public String deleteScheduleData(Long scheduleId) {
        try {
            schedulesRepository.deleteById(scheduleId);
            return messageSource.getMessage("schedules.delete.success", null, Locale.getDefault());
        } catch (EmptyResultDataAccessException e) {
            // 해당 ID로 찾을 수 없을 때 처리
            throw new CustomException(FAIL_400.code(), messageSource.getMessage("schedules.data.not.found", null, Locale.getDefault()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("http.status.inter", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    /**
     * @param existingSchedule
     * @param schedulesDto
     */
    private void updateFields(SchedulesEntity existingSchedule, SchedulesDto schedulesDto) {
        // 명시적으로 모든 필드를 업데이트
        existingSchedule.setTitle(schedulesDto.getTitle());
        existingSchedule.setDescription(schedulesDto.getDescription());
        existingSchedule.setStartDateTime(schedulesDto.getStartDateTime());
        existingSchedule.setEndDateTime(schedulesDto.getEndDateTime());
    }

}

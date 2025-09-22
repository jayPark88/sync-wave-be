package com.parker.batch.schdules.service;


import com.parker.batch.common.intf.AlarmInterface;
import com.parker.common.jpa.entity.SchedulesEntity;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.SchedulesRepository;
import com.parker.common.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulesSchedulerService {
    private final SchedulesRepository schedulesRepository;
    private final UserRepository userRepository;

    private final AlarmInterface alarmSlackImpl;

    /**
     * Schedule 알림 배치
     */
    public void alertUsersAboutScheduleInOneHourTask() {
        log.info("지금 시간 기준으로 한시간 이내 스케쥴이 있는 리스트 조회");
        List<SchedulesEntity> schedulesEntityList = schedulesRepository.findByStartDateTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        schedulesEntityList.stream().parallel().forEach(item -> {
            log.info("Thread: {} 처리 중: {}", Thread.currentThread().getName(), item);
            Optional<UserEntity> optionalUserEntity = userRepository.findById(item.getUserId());
            log.info("알림 대상자 조회 {} ", optionalUserEntity);
            if (optionalUserEntity.isPresent()) {
                log.info("알림 발송 요청! 🚀");
                alarmSlackImpl.sendMsg(optionalUserEntity.get().getEmail(), generateAlaramMsg(item, optionalUserEntity));
            }
        });
    }

    /**
     * @param schedulesEntity
     * @param optionalUserEntity
     * @return
     */
    private String generateAlaramMsg(SchedulesEntity schedulesEntity, Optional<UserEntity> optionalUserEntity) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(optionalUserEntity.isPresent() ? optionalUserEntity.get().getUserName() : "");
        stringBuilder.append("님 한시간 뒤 아래와 같은 일정이 예정되어 있습니다 🚀");
        stringBuilder.append("\n\n");
        stringBuilder.append("◼︎ 스케쥴명: ");
        stringBuilder.append(schedulesEntity.getTitle());
        stringBuilder.append("\n\n");
        stringBuilder.append("︎︎◼︎ 스케쥴 내용: ");
        stringBuilder.append(schedulesEntity.getDescription());
        stringBuilder.append("\n\n");
        stringBuilder.append("︎︎◼︎ 시간 : ");
        stringBuilder.append(schedulesEntity.getStartDateTime());
        return stringBuilder.toString();
    }
}

package com.parker.common.model;

import com.parker.common.intf.ChangableToFromEntity;
import com.parker.common.jpa.entity.SchedulesEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchedulesModel implements ChangableToFromEntity<SchedulesEntity> {
    private Long id; // 일정 고유 ID
    private String title; // 일정 제목
    private String description; // 일정 설명
    private LocalDateTime startDateTime; // 시작 날짜 및 시간
    private LocalDateTime endDateTime; // 종료 날짜 및 시간
    private Long userId; // 사용자 ID

    @Override
    public SchedulesEntity to() {
        return SchedulesEntity.builder()
                .id(id)
                .title(title)
                .description(description)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .userId(userId)
                .build();
    }

    public SchedulesModel(SchedulesEntity entity) {
        from(entity);
    }

    @Override
    public void from(SchedulesEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.startDateTime = entity.getStartDateTime();
        this.endDateTime = entity.getEndDateTime();
        this.userId = entity.getUserId();
    }
}

package com.parker.common.jpa.repository;

import com.parker.common.jpa.entity.SchedulesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SchedulesRepository extends JpaRepository<SchedulesEntity, Long> {
    Optional<SchedulesEntity>findByTitle(String title);
    Optional<SchedulesEntity>findByUserIdAndStartDateTime(Long userId, LocalDateTime startDateTime);
    List<SchedulesEntity> findByUserIdAndStartDateTimeBetween(Long userId, LocalDateTime startDateTime, LocalDateTime eneDateTime);
    List<SchedulesEntity> findByStartDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<SchedulesEntity> findByUserId(Long userId);
}

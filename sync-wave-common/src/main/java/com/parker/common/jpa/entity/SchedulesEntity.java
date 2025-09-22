package com.parker.common.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * com.parker.common.jpa.entity
 * ㄴ schedules
 *
 * <pre>
 * description : 일정 관리 테이블h
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
@Entity
@Table(name = "schedules")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SchedulesEntity extends BaseInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일정 고유 ID

    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100) COMMENT '일정 제목'")
    private String title; // 일정 제목

    @Column(nullable = false, length = 500, columnDefinition = "VARCHAR(500) COMMENT '일정 설명'")
    private String description; // 일정 설명

    @Column(nullable = false, columnDefinition = "DATETIME COMMENT '시작 날짜 및 시간'")
    private LocalDateTime startDateTime; // 시작 날짜 및 시간

    @Column(nullable = false, columnDefinition = "DATETIME COMMENT '종료 날짜 및 시간'")
    private LocalDateTime endDateTime; // 종료 날짜 및 시간

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT COMMENT '사용자 ID'")
    private Long userId; // 사용자 ID
}
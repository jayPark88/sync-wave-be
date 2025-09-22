package com.parker.common.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알림 고유 ID

    @Column(nullable = false, length = 500, columnDefinition = "VARCHAR(500) COMMENT '알림 메시지'")
    private String message; // 알림 메시지

    @Column(nullable = false, columnDefinition = "DATETIME COMMENT '알림 시간'")
    private LocalDateTime notificationTime; // 알림 시간

    @Column(name = "schedule_id", nullable = false, columnDefinition = "BIGINT COMMENT '관련된 일정 ID'")
    private Long scheduleId; // 관련된 일정 ID
}

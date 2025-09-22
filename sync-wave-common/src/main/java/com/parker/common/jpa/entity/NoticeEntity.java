package com.parker.common.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * com.parker.common.jpa.entity
 * ㄴ NoticeEntity
 *
 * <pre>
 * description : 공지사항 테이블
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
@Table(name = "notices")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeEntity extends BaseInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공지사항 고유 ID

    @Column(nullable = false, length = 200, columnDefinition = "VARCHAR(200) COMMENT '공지사항 제목'")
    private String title; // 공지사항 제목

    @Column(nullable = false, length = 2000, columnDefinition = "TEXT COMMENT '공지사항 내용'")
    private String content; // 공지사항 내용

    @Column(nullable = false, length = 10, columnDefinition = "VARCHAR(10) COMMENT '중요도 (HIGH, MEDIUM, LOW)'")
    private String priority; // 중요도 (HIGH, MEDIUM, LOW)

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '활성화 상태'")
    private Boolean isActive; // 활성화 상태 (true: 활성, false: 비활성)

    @Column(name = "created_by", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '작성자'")
    private String createdBy; // 작성자
} 
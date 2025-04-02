package com.parker.common.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "passwordResetToken")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100) COMMENT '로그인 아이디 (이메일)'")
    private String email; // 로그인 아이디 (이메일)

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(255) COMMENT '토큰'")
    private String token;

    @Column(nullable = false, columnDefinition = "DATETIME COMMENT '토큰 시작 시간'")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "DATETIME COMMENT '만료 시간'")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @ColumnDefault("false")  // Hibernate에서 기본값 설정 (null 방지)
    private boolean used = false;

}

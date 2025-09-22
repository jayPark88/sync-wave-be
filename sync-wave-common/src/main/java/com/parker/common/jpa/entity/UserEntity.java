package com.parker.common.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * com.parker.common.jpa.entity
 * ㄴ User
 *
 * <pre>
 * description : 사용자 테이블
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
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseInfoEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100) COMMENT '사용자명'")
    private String userName; // 사용자명

    @Column(nullable = false, columnDefinition = "VARCHAR(255) COMMENT '비밀번호'")
    private String password; // 비밀번호

    @Column(length = 100, unique = true, columnDefinition = "VARCHAR(100) COMMENT '닉네임'")
    private String nickName; // 닉네임

    @Column(nullable = false, unique = true, length = 11, columnDefinition = "VARCHAR(11) COMMENT '핸드폰번호'")
    private String phone; // 휴대폰 번호

    @Column(nullable = false, unique = true, length = 100, columnDefinition = "VARCHAR(100) COMMENT '로그인 아이디 (이메일)'")
    private String email; // 로그인 아이디 (이메일)

    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) COMMENT '권한'")
    private String role; // 권한

    @Column(nullable = false, length = 10, columnDefinition = "VARCHAR(10) COMMENT '회원 상태'")
    private String status; // 회원 상태
}
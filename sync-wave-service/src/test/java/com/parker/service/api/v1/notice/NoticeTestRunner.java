package com.parker.service.api.v1.notice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * com.parker.service.api.v1.notice
 * ㄴ NoticeTestRunner
 *
 * <pre>
 * description : 공지사항 테스트 러너
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
/**
 * 공지사항 테스트 러너 클래스
 * 
 * 목적:
 * - Spring Boot 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인
 * - 테스트 환경 설정이 올바르게 구성되었는지 검증
 * - 의존성 주입이 정상적으로 동작하는지 확인
 * 
 * 사용 방법:
 * - 이 클래스를 실행하여 Spring 컨텍스트 로드 테스트 수행
 * - 다른 테스트 클래스들이 실행되기 전에 환경 설정 검증
 * - CI/CD 파이프라인에서 빌드 환경 검증용으로 활용
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")  // test 프로파일 사용
class NoticeTestRunner {

    /**
     * Spring 컨텍스트 로드 테스트
     * 
     * 테스트 내용:
     * - Spring Boot 애플리케이션 컨텍스트가 정상적으로 시작되는지 확인
     * - 모든 Bean들이 올바르게 등록되는지 확인
     * - 데이터베이스 연결이 정상적으로 설정되는지 확인
     * - 설정 파일들이 올바르게 로드되는지 확인
     */
    @Test
    void contextLoads() {
        // Spring 컨텍스트가 정상적으로 로드되는지 확인
        // 이 메서드가 예외 없이 실행되면 컨텍스트 로드 성공
    }
} 
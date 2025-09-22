package com.parker.batch.auth.scheduler;

import com.parker.common.jpa.entity.PasswordResetTokenEntity;
import com.parker.common.jpa.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthScheduler {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron = "0 */5 * * * *")
    void passwordResetTokenExpireCheck() {
        // 모든 패스워드 reset Token List 조회
        List<PasswordResetTokenEntity> passwordResetTokenEntityList = passwordResetTokenRepository.findAll();
        passwordResetTokenEntityList.stream()
                .filter(token -> !token.isUsed() && token.getExpiresAt().isBefore(LocalDateTime.now())) // 조건을 하나로 합침
                .peek(token -> token.setUsed(true)) // 사용되지 않은 토큰에 대해서만 사용 처리
                .peek(passwordResetTokenRepository::save) // 변경 사항 저장
                .forEach(token -> log.info("Password Reset Token: {}, Expiry Time: {}", token.getToken(), token.getExpiresAt())); // 로그 출력

    }
}

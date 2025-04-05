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

//    @Scheduled(cron = "0 */5 * * * *")
    @Scheduled(cron = "*/5 * * * * *")
    void passwordResetTokenExpireCheck() {
        // 모든 패스워드 reset Token List 조회
        List<PasswordResetTokenEntity> passwordResetTokenEntityList = passwordResetTokenRepository.findAll();
        passwordResetTokenEntityList.stream()
                .filter(passwordResetTokenEntity -> !passwordResetTokenEntity.isUsed()) // 사용되지 않은 토큰만 필터링
                .filter(passwordResetTokenEntity -> passwordResetTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) // expiresAt이 현재 시간 이전인 토큰만 필터링
                .forEach(passwordResetTokenEntity -> {
                    passwordResetTokenEntity.setUsed(true); // 토큰 사용 처리
                    passwordResetTokenRepository.save(passwordResetTokenEntity); // 변경 사항 저장
                    log.info("Password Reset Token: {}, Expiry Time: {}", passwordResetTokenEntity.getToken(), passwordResetTokenEntity.getExpiresAt()); // 로그 출력
                });

    }
}

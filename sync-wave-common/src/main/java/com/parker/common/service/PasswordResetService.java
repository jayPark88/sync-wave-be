package com.parker.common.service;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.PasswordResetTokenEntity;
import com.parker.common.jpa.repository.PasswordResetTokenRepository;
import com.parker.common.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${password.reset.uri}")
    private String passwordResetUti;

    public void sendResetEmail(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(FAIL_500.code(),
                        messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                        HttpStatus.INTERNAL_SERVER_ERROR));

        String token = UUID.randomUUID().toString();
        createPasswordResetToken(email, token);
        String resetLink = passwordResetUti + token;

        // 이메일 발송
        emailService.sendEmail(email, "Password Reset Request", "Click the link to reset your password: " + resetLink);
    }

    private void createPasswordResetToken(String email, String token) {
        passwordResetTokenRepository.save(PasswordResetTokenEntity.builder()
                .email(email)
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build());
    }

}

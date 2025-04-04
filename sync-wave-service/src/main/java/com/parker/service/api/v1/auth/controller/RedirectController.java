package com.parker.service.api.v1.auth.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

@Controller
@RequestMapping("/v1/auth/redirect")
@RequiredArgsConstructor
@Slf4j
public class RedirectController {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MessageSource messageSource;

    @GetMapping("/password-reset/redirect")
    public String passwordResetRedirect(@RequestParam("token") String token) {
        // 1. 토큰 검증
        passwordResetTokenRepository.findByToken(token).filter(item -> item.isUsed() == false)
                .orElseThrow(() -> new CustomException(FAIL_500.code(),
                        messageSource.getMessage("token.expire", null, Locale.getDefault()),
                        HttpStatus.INTERNAL_SERVER_ERROR));

        // 2. 토큰이 유효하면 비밀번호 초기화 화면을 보여주는 URL 리턴 (예: 비밀번호 변경 폼)
        return "redirect:http://localhost:3000/login?token="+ token;  // 외부 URL로 리디렉션
    }
}

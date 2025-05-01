package com.parker.service.api.v1.auth.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.repository.PasswordResetTokenRepository;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.auth.dto.LoginDto;
import com.parker.service.api.v1.auth.dto.PasswordResetRequestDto;
import com.parker.service.api.v1.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_400;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_401;


/**
 * com.jaypark8282.base.api.v1.login.controller;
 * ㄴ AuthController
 *
 * <pre>
 * description : 최초 로그인 호출 시 jwtFilter를 타서 interceptor까지 다 끝난 후 401이 뜨지 않고 온다 permitall이기 때문
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 12/25/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @PostMapping("/login")
    public CommonResponse<?> authorize(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomException(FAIL_401.code(), messageSource.getMessage("http.status.unauthorized", null, Locale.getDefault()), HttpStatus.UNAUTHORIZED);
        }
        return authService.authorize(loginDto);
    }

    /**
     * 비밀번호 변경 메일 발송 요청
     *
     * @param email
     */
    @PostMapping("/password-reset/email")
    public void passwordResetEmailRequest(@RequestParam("email") String email) {
        authService.passwordResetEmailRequest(email);
    }

    /**
     * 비밀번호 변경
     *
     * @param passwordResetRequestDto
     */
    @PostMapping("/password-reset")
    public void passwordReset(@Valid @RequestBody PasswordResetRequestDto passwordResetRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(FAIL_400.code(), bindingResult.getFieldErrors().getFirst().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        authService.passwordReset(passwordResetRequestDto);
    }
}
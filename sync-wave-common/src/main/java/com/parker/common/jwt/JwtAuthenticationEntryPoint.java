package com.parker.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parker.common.resonse.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_401;


/**
 * com.parker.admin.jwt
 * ㄴ JwtAuthenticationEntryPoint
 *
 * <pre>
 * description :이거는 이제 WebSecurity에서 사용될 exceptionHandling 부분인듯
 * 유효한 자격증명을 제공하지 않고 접근하려 할때 401 Unauthorized에러를 리턴할 JwtAuthenticationEntryPoint클래스
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 12/24/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        CommonResponse<?> commonResponse = new CommonResponse<>(FAIL_401.code(), messageSource.getMessage("http.status.unauthorized", null, Locale.getDefault()));

        String responseString = objectMapper.writeValueAsString(commonResponse);

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().append(responseString);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().flush();

    }
}
package com.parker.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parker.common.resonse.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_403;

/**
 * com.parker.admin.jwt
 * ㄴ JwtAccessDeniedHandler
 *
 * <pre>
 * description : 필요한 권한이 존재하지 않는 경우 403
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
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        CommonResponse<?> commonResponse = new CommonResponse<>(FAIL_403.code(), messageSource.getMessage("http.status.forbidden", null, Locale.getDefault()));

        String responseString = objectMapper.writeValueAsString(commonResponse);

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().append(responseString);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().flush();
    }
}
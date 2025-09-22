package com.parker.batch.common.intf.impl;

import com.parker.batch.common.intf.AlarmInterface;
import com.parker.common.exception.CustomException;
import com.parker.common.resonse.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlarmSlackImpl implements AlarmInterface {

    @Value("${notification.slack.webhook.url}")
    private String slackWebhookUrl;
    
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;

    /**
     * Slack Msg 발송 기능
     * @param email
     * @param msg
     * @return
     */
    @Override
    public CommonResponse<String> sendMsg(String email, String msg) {
        // Slack 메시지 포맷 설정
        String payload = String.format("{\"text\": \"%s\"}", msg);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        // Slack으로 POST 요청 전송
        ResponseEntity<String> response = restTemplate.exchange(
                slackWebhookUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if(response.getStatusCode().is2xxSuccessful()){
            return new CommonResponse<>(messageSource.getMessage("alarm.slack.send.success", null, Locale.getDefault()));
        }else{
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("alarm.slack.send.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

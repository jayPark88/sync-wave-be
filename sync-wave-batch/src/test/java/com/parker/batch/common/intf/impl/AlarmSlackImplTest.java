package com.parker.batch.common.intf.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class AlarmSlackImplTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AlarmSlackImpl alarmSlack;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(alarmSlack, "slackWebhookUrl", "http://test-webhook-url");
    }

    @Test
    void sendMsg() {
        // given
        String message = "hello Slack Success Parker.pen!!!!";
        
        // Mock 응답 설정
        when(restTemplate.exchange(
                any(String.class),
                any(),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        when(messageSource.getMessage(eq("alarm.slack.send.success"), any(), any()))
                .thenReturn("Success");

        // when
        alarmSlack.sendMsg("test@example.com", message);

        // then - 예외가 발생하지 않으면 테스트 성공
    }
}
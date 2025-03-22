package com.parker.service.api.v1.user.service;

import com.parker.common.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    EmailService emailService;

    @Test
    void sendEmail_ShouldInvokeMailSender() {
        // given
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        // TDD 스타일로 필요한 값 직접 주입
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@mock.com");

        // when
        emailService.sendEmail("toEmail@mock.com", "방탈출!", "해내자!!");

        // then
        verify(javaMailSender, times(1)).send(mockMessage);
    }
}


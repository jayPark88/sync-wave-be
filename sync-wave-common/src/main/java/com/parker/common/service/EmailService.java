package com.parker.common.service;

import com.parker.common.exception.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

/**
 * 이메일 발송 기능을 담당하는 서비스 클래스입니다.
 * Spring Boot의 JavaMailSender를 이용해 실제 이메일을 발송하며,
 * 실패 시 커스텀 예외를 발생시킵니다.
 *
 * 메일 발송 성공/실패 여부는 로그로 남기고, 국제화 메시지를 통해 에러 메시지를 구성합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    // Spring Boot가 자동 설정한 JavaMailSender 빈을 주입받습니다.
    private final JavaMailSender mailSender;

    // 국제화 메시지 처리용 MessageSource (messages.properties 등에서 메시지 읽기)
    private final MessageSource messageSource;

    // 발신자 이메일 주소 (application.yml의 spring.mail.username 값으로 설정됨)
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 이메일을 발송하는 메서드입니다.
     * 내부적으로 MimeMessage를 생성 후 JavaMailSender로 전송합니다.
     *
     * @param toEmail 수신자 이메일 주소
     * @param subject 이메일 제목
     * @param body    이메일 본문 (HTML 허용)
     * @throws CustomException 이메일 발송 실패 시 사용자 정의 예외 발생
     */
    @Transactional
    public void sendEmail(String toEmail, String subject, String body) {
        MimeMessage message = createMimeMessage(toEmail, subject, body);
        try {
            mailSender.send(message);
            log.info("Email successfully sent to {}", toEmail);
        } catch (MailException e) {
            log.error("Failed to send email to {}", toEmail, e);
            // 국제화 메시지 사용 + 커스텀 예외로 변환하여 핸들러로 전달
            throw new CustomException(
                    FAIL_500.code(),
                    messageSource.getMessage("mail.send.fail", null, Locale.getDefault()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * MimeMessage 객체를 생성하고 필요한 설정(From, To, Subject, Body)을 구성합니다.
     *
     * @param to      수신자 이메일 주소
     * @param subject 이메일 제목
     * @param body    이메일 본문 (HTML 포함 가능)
     * @return 구성된 MimeMessage 객체
     * @throws CustomException 메시지 구성 중 에러가 발생할 경우
     */
    private MimeMessage createMimeMessage(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);  // 설정된 발신자 이메일
            helper.setTo(to);           // 수신자 이메일
            helper.setSubject(subject); // 메일 제목
            helper.setText(body, true); // HTML 지원 본문

            return message;
        } catch (MessagingException e) {
            log.error("Failed to construct email message", e);
            // 국제화 메시지 사용 + 커스텀 예외로 변환
            throw new CustomException(
                    FAIL_500.code(),
                    messageSource.getMessage("mail.message.create.failed", null, Locale.getDefault()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}


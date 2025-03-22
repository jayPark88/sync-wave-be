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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public void sendEmail(String toEmail, String subject, String body) {
        MimeMessage message = createMimeMessage(toEmail, subject, body);
        try {
            mailSender.send(message);
            log.info("Email successfully sent to {}", toEmail);
        } catch (MailException e) {
            log.error("Failed to send email to {}", toEmail, e);
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("mail.send.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MimeMessage createMimeMessage(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            return message;
        } catch (MessagingException e) {
            log.error("Failed to construct email message", e);
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("mail.message.create.failed", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

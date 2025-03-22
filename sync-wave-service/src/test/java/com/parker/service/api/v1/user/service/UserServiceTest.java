package com.parker.service.api.v1.user.service;

import com.parker.common.enums.Role;
import com.parker.common.enums.UserStatus;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Test
    void deleteUserInfo() {
        // given
        UserEntity saveResultEntity =
                userRepository.saveAndFlush(
                        UserEntity.builder()
                                .userName("parker-temp")
                                .password("parker123123!!")
                                .phone("01000000000")
                                .email("parker-test@gmail.com")
                                .role(Role.ROLE_MASTER.code())
                                .status(UserStatus.HOLDING.code())
                                .build()
                );

        // when
        Assertions.assertTrue(userRepository.findByEmail(saveResultEntity.getEmail()).isPresent());
        userRepository.deleteByEmail(saveResultEntity.getEmail());

        Assertions.assertTrue(userRepository.findByEmail(saveResultEntity.getEmail()).isEmpty());
    }

    @Test
    void passwordResetEmailSend() {
        //given
        String toEmail = "parkdev@kakao.com";
        String subject = "5월 부터 지원하자!";
        String body = "가자!!!";

        // when & then
        try {
            emailService.sendEmail(toEmail, subject, body);
            log.info("발송 성공!!");
        } catch (Exception e) {
            Assertions.fail("발송 실패");
        }
    }
}
package com.parker.service.common.security;

import com.parker.common.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordResetServiceTotalTest {

    @Autowired
    private PasswordResetService passwordResetService;


    @Test
    void passwordResetServiceTest() {

        // given
        String email = "jaypark8282@gmail.com";

        // when
        passwordResetService.sendResetEmail(email);
    }
}


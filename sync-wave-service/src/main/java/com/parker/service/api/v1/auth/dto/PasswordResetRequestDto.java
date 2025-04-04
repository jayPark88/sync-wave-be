package com.parker.service.api.v1.auth.dto;

import lombok.Data;

@Data
public class PasswordResetRequestDto {
    private String password;
    private String token;
}

package com.parker.service.api.v1.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequestDto {
    @NotNull(message = "{password.not.null}")
    @Size(min = 12, max = 255, message = "{password.size}")
    private String password;
    @NotNull(message = "{user.token.invalid}")
    private String token;
}

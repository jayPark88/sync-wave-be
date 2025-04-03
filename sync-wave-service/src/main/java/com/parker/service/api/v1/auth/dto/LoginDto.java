package com.parker.service.api.v1.auth.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * com.parker.common.dto
 * ㄴ LoginDto
 *
 * <pre>
 * description : login에 사용될 dto
 * </pre>
 *
 * <pre>
 * <b>History:</b>
 *  parker, 1.0, 12/25/23  초기작성
 * </pre>
 *
 * @author parker
 * @version 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotNull(message = "{login.user.id.not.null}")
    @Size(min = 3, max = 50, message = "{login.user.id.size}")
    private String userId;

    @NotNull(message = "{login.password.not.null}")
    @Size(min = 12, max = 100, message = "{login.password.size}")
    private String password;
}
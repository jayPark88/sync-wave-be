package com.parker.service.api.v1.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;



/**
 * com.jaypark8282.core.dto
 * ㄴ UserDto
 *
 * <pre>
 * description :
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
public class UserDto {

    @NotNull(message = "{user.name.not.null}")
    @Size(min = 3, max =100, message = "{user.name.size}")
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "{password.not.null}")
    @Size(min = 12, max = 255, message = "{password.size}")
    private String password;

    @Size(min = 1, max = 100, message = "{user.nick.name.size}")
    private String nickName;

    @NotNull(message = "{user.phone.not.null}")
    @Pattern(regexp = "^\\d{11}$", message = "{user.phone.size}")
    private String phone;

    @NotNull(message = "{user.email.not.null}")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "{user.email.invalid}")
    @Size(max = 100, message = "{user.email.size}")
    private String email;

    private String role;

    private String status;
}
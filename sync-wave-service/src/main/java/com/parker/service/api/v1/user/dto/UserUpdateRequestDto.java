package com.parker.service.api.v1.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto extends UserInfoRequestDto{
    private String userName;
    private String password;
    private String phone;
    private String email;
    private String role;
    private String nickName;
}

package com.parker.common.model;

import com.parker.common.intf.ChangableToFromEntity;
import com.parker.common.jpa.entity.UserEntity;

public class UserModel implements ChangableToFromEntity<UserEntity> {
    private Long id;
    private String userName;
    private String password;
    private String nickName;
    private String phone;
    private String email;
    private String role;
    private String status;

    public UserModel(UserEntity user) {
        from(user);
    }

    @Override
    public UserEntity to() {

        return UserEntity.builder()
                .id(id)
                .userName(userName)
                .password(password)
                .nickName(nickName)
                .phone(phone)
                .email(email)
                .role(role)
                .status(status)
                .build();
    }

    @Override
    public void from(UserEntity entity) {
        id = entity.getId();
        userName = entity.getUserName();
        password = entity.getPassword();
        nickName = entity.getNickName();
        phone = entity.getPhone();
        email = entity.getEmail();
        role = entity.getRole();
        status = entity.getStatus();
    }
}

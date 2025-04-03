package com.parker.service.api.v1.user.service;


import com.parker.common.enums.Role;
import com.parker.common.enums.UserStatus;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.UserRepository;
import com.parker.common.util.security.SecurityUtil;
import com.parker.service.api.v1.user.dto.UserDto;
import com.parker.service.api.v1.user.dto.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_2000;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;


/**
 * com.parker.admin.api.v1.login.service
 * ㄴ UserService
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
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Transactional
    public UserEntity signUp(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new CustomException(FAIL_2000.code(), messageSource.getMessage("error.2000", new String[]{userDto.getUserName()}, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserEntity userEntity = UserEntity.builder()
                .userName(userDto.getUserName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickName(userDto.getNickName())
                .phone(userDto.getPhone())
                .email(userDto.getEmail())
                .role(userRepository.count() > 0 ? Role.ROLE_MASTER.code() : Role.ROLE_USER.code())
                .status(UserStatus.ACTIVATED.code())
                .createId(userDto.getNickName())// 삭제 예정 notion 기록전 남겨 둠, 회원가입 시 사용자가 인증되지 않은 상태에서 @CreatedBy를 사용하면 anonymousUser가 자동으로 설정됩니다. 따라서 회원가입 후 사용자를 즉시 로그인시키거나, createId를 명시적으로 설정하여 이 문제를 해결할 수 있습니다.
                .build();
        return userRepository.save(userEntity);
    }

    @Transactional
    public UserEntity updateUser(String userId, UserUpdateRequestDto userUpdateRequestDto) {
        if (!checkUserCheck(userId)) {
            throw new CustomException(FAIL_500.code(),
                    messageSource.getMessage("user.un.auth", null, Locale.getDefault()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return userRepository.findByEmail(userId)
                .map(existingUser -> {
                    if (roleCheck(userId)) {
                        updateCommonFields(existingUser, userUpdateRequestDto);
                    }
                    updatePasswordIfPresent(existingUser, userUpdateRequestDto);
                    return existingUser;
                })
                .orElseThrow(() -> new CustomException(FAIL_500.code(),
                        messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    public String deleteUserInfo(String userId) {
        if (checkUserCheck(userId) && roleCheck(userId)) {
            userRepository.deleteByEmail(userId);
            return userId + " deleted!";
        } else {
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("user.un.auth", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> searchUserList(int page, int size) {
        if (checkUserCheck() && roleCheck()) {
            Pageable pageable = PageRequest.of(page, size);
            return userRepository.findAll(pageable);
        } else {
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("user.un.auth", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserInfo(String userId) {
        if (checkUserCheck(userId)) {
            return userRepository.findByEmail(userId);
        } else {
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("user.un.auth", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 사용자 체크 메서드
     *
     * @param userId
     * @return
     */
    public boolean checkUserCheck(String userId) {
        return SecurityUtil.getCurrentUserName()
                .filter(userId::equals)
                .flatMap(userRepository::findByEmail)
                .isPresent();
    }

    /**
     * User Seq Id를 찾아주는 기능
     *
     * @return
     */
    public Long getUserId() {
        if (SecurityUtil.getCurrentUserName().isPresent()) {
            Optional<UserEntity> userEntity = userRepository.findByEmail(SecurityUtil.getCurrentUserName().get());
            if (userEntity.isPresent()) {
                return userEntity.get().getId();
            }
        }
        return 0L;
    }

    private boolean checkUserCheck() {
        return SecurityUtil.getCurrentUserName()
                .flatMap(userRepository::findByEmail)
                .isPresent();
    }

    private boolean roleCheck(String userId) {
        return userRepository.findByEmail(userId).filter(item -> item.getRole().equals(Role.ROLE_MASTER.code())).isPresent();
    }

    private boolean roleCheck() {
        return SecurityUtil.getCurrentUserName()
                .flatMap(userRepository::findByEmail)
                .map(user -> user.getRole().equals(Role.ROLE_MASTER.code()))
                .orElse(false);
    }

    private void updateCommonFields(UserEntity existingUser, UserUpdateRequestDto dto) {
        Optional.ofNullable(dto.getUserName()).ifPresent(existingUser::setUserName);
        Optional.ofNullable(dto.getPhone()).ifPresent(existingUser::setPhone);
        Optional.ofNullable(dto.getEmail()).ifPresent(existingUser::setEmail);
    }

    private void updatePasswordIfPresent(UserEntity existingUser, UserUpdateRequestDto dto) {
        Optional.ofNullable(dto.getPassword())
                .map(passwordEncoder::encode)
                .ifPresent(existingUser::setPassword);
    }
}
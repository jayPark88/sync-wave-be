package com.parker.service.api.v1.user.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.user.dto.UserDto;
import com.parker.service.api.v1.user.dto.UserUpdateRequestDto;
import com.parker.service.api.v1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_400;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;


/**
 * com.parker.admin.api.v1.user.controller
 * ㄴ UserController
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
@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MessageSource messageSource;

    @PostMapping("/signUp")
    public CommonResponse<UserEntity> signup(
            @Valid @RequestBody UserDto userDto, BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(FAIL_400.code(), bindingResult.getFieldErrors().getFirst().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            return new CommonResponse<>(userService.signUp(userDto));
        } catch (DataAccessException e) {
            log.info("signUp error {}", e.getMessage());
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("user.signup.save.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{userId}")
    public CommonResponse<UserEntity> modifyUserInfo(@PathVariable("userId") String userId, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        return new CommonResponse<>(userService.updateUser(userId, userUpdateRequestDto));
    }

    @DeleteMapping("/{userId}")
    public CommonResponse<String> deleteUserInfo(@PathVariable("userId") String userId) {
        try {
            return new CommonResponse<>(userService.deleteUserInfo(userId));
        } catch (RuntimeException e) {
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("user.delete.fail", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public CommonResponse<Page<UserEntity>> searchUserList(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size) {
        return new CommonResponse<>(userService.searchUserList(page, size));
    }

    @GetMapping("/{userId}")
    public CommonResponse<UserEntity> getUserInfo(@PathVariable(name = "userId") String userId) {
        return new CommonResponse<>(userService.getUserInfo(userId).orElseThrow(() -> new CustomException(FAIL_500.code(), messageSource.getMessage("user.not.found", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR)));
    }

}
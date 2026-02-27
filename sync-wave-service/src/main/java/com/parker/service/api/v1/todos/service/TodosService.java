package com.parker.service.api.v1.todos.service;

import com.parker.common.enums.TodoStatus;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.jpa.repository.TodosRepository;
import com.parker.service.api.v1.todos.dto.TodosDto;
import com.parker.service.api.v1.todos.dto.TodosDtoSearchDto;
import com.parker.service.api.v1.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_400;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_403;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_404;
import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_500;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodosService {

    private final TodosRepository todosRepository;
    private final MessageSource messageSource;
    private final UserService userService;

    public List<TodosEntity> createTodos(TodosDto todosDto) {
        List<TodosEntity> todosEntityList = new ArrayList<>();

        todosEntityList.add(TodosEntity.builder()
                .task(todosDto.getTask())
                .status(TodoStatus.PENDING.code())
                .userId(userService.getUserId())
                .build());

        todosRepository.saveAll(todosEntityList);
        return todosEntityList;
    }

    public TodosEntity getDetailTodoDetailInfo(Long todosId) {
        return todosRepository.findById(todosId)
                .orElseThrow(() -> new CustomException(FAIL_404.code(),
                    messageSource.getMessage("todo.info.not.found", null, Locale.getDefault()),
                    HttpStatus.NOT_FOUND));
    }

    public List<TodosEntity> getDetailTodosList(TodosDtoSearchDto todosDtoSearchDto) {
        Long userId = userService.getUserId();
        return todosRepository.findByUserIdOrderByCreatedDateTimeDesc(userId);
    }

    public TodosEntity modifyTodoInfo(TodosDto todosDto) {
        TodosEntity targetEntity = todosRepository.findById(todosDto.getId())
                .orElseThrow(() -> new CustomException(FAIL_404.code(),
                        messageSource.getMessage("todo.info.not.found", null, Locale.getDefault()),
                        HttpStatus.NOT_FOUND));

        // 소유권 검증: 본인의 Todo만 수정 가능
        if (!targetEntity.getUserId().equals(userService.getUserId())) {
            throw new CustomException(FAIL_403.code(),
                    messageSource.getMessage("user.un.auth", null, Locale.getDefault()),
                    HttpStatus.FORBIDDEN);
        }

        if (!ObjectUtils.isEmpty(todosDto.getTask())) {
            log.info("task update {}", todosDto.getTask());
            targetEntity.setTask(todosDto.getTask());
        }

        if (!ObjectUtils.isEmpty(todosDto.getStatus())) {
            boolean isValidStatus = java.util.stream.Stream.of(TodoStatus.values())
                    .anyMatch(s -> s.code().equals(todosDto.getStatus()));
            if (!isValidStatus) {
                throw new CustomException(FAIL_400.code(), "유효하지 않은 상태값입니다.", HttpStatus.BAD_REQUEST);
            }
            log.info("status update {}", todosDto.getStatus());
            targetEntity.setStatus(todosDto.getStatus());
        }

        return todosRepository.save(targetEntity);
    }

    public String deleteTodoData(Long todosId) {
        // 소유권 검증: 본인의 Todo만 삭제 가능
        TodosEntity todo = todosRepository.findById(todosId)
                .orElseThrow(() -> new CustomException(FAIL_400.code(),
                        messageSource.getMessage("todo.info.not.found", null, Locale.getDefault()),
                        HttpStatus.BAD_REQUEST));

        if (!todo.getUserId().equals(userService.getUserId())) {
            throw new CustomException(FAIL_403.code(),
                    messageSource.getMessage("user.un.auth", null, Locale.getDefault()),
                    HttpStatus.FORBIDDEN);
        }

        try {
            todosRepository.deleteById(todosId);
            return messageSource.getMessage("schedules.delete.success", null, Locale.getDefault());
        } catch (Exception e) {
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("http.status.inter", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

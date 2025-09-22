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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_400;
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
                .orElseThrow(() -> new CustomException(FAIL_500.code(), 
                    messageSource.getMessage("todo.info.not.found", null, Locale.getDefault()), 
                    HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public List<TodosEntity> getDetailTodosList(TodosDtoSearchDto todosDtoSearchDto) {
        Long userId = userService.getUserId();
        return todosRepository.findByUserIdOrderByCreatedDateTimeDesc(userId);
    }

    public TodosEntity modifyTodoInfo(TodosDto todosDto) {
        Optional<TodosEntity> targetEntity = todosRepository.findById(todosDto.getId());

        if (targetEntity.isPresent()) {
            if (!ObjectUtils.isEmpty(todosDto.getTask())) {
                log.info("task update {}", todosDto.getTask());
                targetEntity.get().setTask(todosDto.getTask());
            }

            if (!ObjectUtils.isEmpty(todosDto.getStatus())) {
                log.info("status update {}", todosDto.getStatus());
                targetEntity.get().setStatus(todosDto.getStatus());
            }

            return todosRepository.save(targetEntity.get());
        } else {
            throw new CustomException(FAIL_500.code(), 
                messageSource.getMessage("todo.info.not.found", null, Locale.getDefault()), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String deleteTodoData(Long todosId) {
        try {
            todosRepository.deleteById(todosId);
            return messageSource.getMessage("schedules.delete.success", null, Locale.getDefault());
        } catch (EmptyResultDataAccessException e) {
            // 해당 ID로 찾을 수 없을 때 처리
            throw new CustomException(FAIL_400.code(), messageSource.getMessage("todo.info.not.found", null, Locale.getDefault()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new CustomException(FAIL_500.code(), messageSource.getMessage("http.status.inter", null, Locale.getDefault()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

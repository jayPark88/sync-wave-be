package com.parker.service.api.v1.todos.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.todos.dto.TodosDto;
import com.parker.service.api.v1.todos.dto.TodosDtoSearchDto;
import com.parker.service.api.v1.todos.service.TodosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.parker.common.exception.enums.ResponseErrorCode.FAIL_400;

@Slf4j
@RestController
@RequestMapping("/v1/todos")
@RequiredArgsConstructor
public class TodosController {

    private final TodosService todosService;

    @PostMapping
    public CommonResponse<List<TodosEntity>> createTodos(@Valid @RequestBody TodosDto todosDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(FAIL_400.code(), bindingResult.getAllErrors().stream().findFirst().get().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return new CommonResponse<>(todosService.createTodos(todosDto));
    }

    @GetMapping("/{todosId}")
    public CommonResponse<TodosEntity> getDetailScheduleDetailInfo(@PathVariable("todosId") Long todosId) {
        return new CommonResponse<>(todosService.getDetailTodoDetailInfo(todosId));
    }

    @GetMapping
    public CommonResponse<List<TodosEntity>> getDetailTodosList(TodosDtoSearchDto todosDtoSearchDto) {
        return new CommonResponse<>(todosService.getDetailTodosList(todosDtoSearchDto));
    }

    @PatchMapping
    public CommonResponse<TodosEntity> modifyTodoInfo(@Valid @RequestBody TodosDto todosDto) {
        return new CommonResponse<>(todosService.modifyTodoInfo(todosDto));
    }

    @DeleteMapping("/{todosId}")
    public CommonResponse<String> deleteScheduleData(@PathVariable("todosId") Long todosId) {
        return new CommonResponse<>(todosService.deleteTodoData(todosId));
    }
}

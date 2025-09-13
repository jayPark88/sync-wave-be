package com.parker.service.api.v1.todos.controller;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.todos.dto.TodosDto;
import com.parker.service.api.v1.todos.dto.TodosDtoSearchDto;
import com.parker.service.api.v1.todos.service.TodosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TodosController 단위 테스트 - TDD 방법론 적용
 * 
 * 🔴 TDD Step 1: 실패하는 테스트 작성 (Red)
 * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 (Green)
 * 🔵 TDD Step 3: 코드 리팩토링 (Refactor)
 * 
 * @ExtendWith(MockitoExtension.class): Mockito를 사용한 테스트를 위한 어노테이션
 * - @Mock: Mock 객체를 생성
 * - @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상 객체
 */
@ExtendWith(MockitoExtension.class)
class TodosControllerTest {

    @Mock
    private TodosService todosService; // TodosService를 Mock 객체로 생성

    @Mock
    private BindingResult bindingResult; // BindingResult를 Mock 객체로 생성

    @InjectMocks
    private TodosController todosController; // 테스트 대상 TodosController (Mock 객체들이 주입됨)

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 유효한 할일 정보로 생성 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 생성 성공 테스트")
    void createTodos_유효한할일정보_성공() {
        // Given: 테스트 데이터 준비
        TodosDto todosDto = TodosDto.builder()
                .task("새로운 할일")
                .status("PENDING")
                .build();

        TodosEntity createdTodo = TodosEntity.builder()
                .id(1L)
                .task("새로운 할일")
                .status("PENDING")
                .userId(1L)
                .build();

        List<TodosEntity> createdTodos = Arrays.asList(createdTodo);

        // Mock 설정: BindingResult에 에러가 없다고 설정
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // Mock 설정: TodosService의 createTodos 메서드가 호출되면 createdTodos를 반환
        when(todosService.createTodos(any(TodosDto.class))).thenReturn(createdTodos);

        // When: 테스트할 메서드 실행
        CommonResponse<List<TodosEntity>> result = todosController.createTodos(todosDto, bindingResult);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getTask()).isEqualTo("새로운 할일");
        assertThat(result.getData().get(0).getStatus()).isEqualTo("PENDING");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).createTodos(todosDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 잘못된 할일 정보로 생성 요청 시 실패한다
     */
    @Test
    @DisplayName("할일 생성 실패 테스트 - 잘못된 할일 정보")
    void createTodos_잘못된할일정보_실패() {
        // Given: 잘못된 테스트 데이터 준비
        TodosDto todosDto = TodosDto.builder()
                .task("") // 빈 할일
                .status("") // 빈 상태
                .build();

        // Mock 설정: BindingResult에 에러가 있다고 설정 (실패 시나리오)
        when(bindingResult.hasErrors()).thenReturn(true);
        
        // Mock 설정: ObjectError 생성
        ObjectError objectError = new ObjectError("todosDto", "할일은 필수입니다.");
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(objectError));

        // When & Then: 예외 발생 검증 (Controller에서 CustomException을 던짐)
        assertThatThrownBy(() -> todosController.createTodos(todosDto, bindingResult))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되지 않았는지 검증 (bindingResult.hasErrors()가 true이므로)
        verify(todosService, never()).createTodos(any(TodosDto.class));
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 상세 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 상세 조회 성공 테스트")
    void getDetailScheduleDetailInfo_유효한할일ID_성공() {
        // Given: 테스트 데이터 준비
        Long todosId = 1L;

        TodosEntity todo = TodosEntity.builder()
                .id(todosId)
                .task("할일 제목")
                .status("PENDING")
                .userId(1L)
                .build();

        // Mock 설정: TodosService의 getDetailTodoDetailInfo 메서드가 호출되면 todo를 반환
        when(todosService.getDetailTodoDetailInfo(todosId)).thenReturn(todo);

        // When: 테스트할 메서드 실행
        CommonResponse<TodosEntity> result = todosController.getDetailScheduleDetailInfo(todosId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getId()).isEqualTo(todosId);
        assertThat(result.getData().getTask()).isEqualTo("할일 제목");
        assertThat(result.getData().getStatus()).isEqualTo("PENDING");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).getDetailTodoDetailInfo(todosId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 목록 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 목록 조회 성공 테스트")
    void getDetailTodosList_유효한검색조건_성공() {
        // Given: 테스트 데이터 준비
        TodosDtoSearchDto searchDto = new TodosDtoSearchDto();
        searchDto.setStatus("PENDING");

        TodosEntity todo1 = TodosEntity.builder()
                .id(1L)
                .task("할일 1")
                .status("PENDING")
                .userId(1L)
                .build();

        TodosEntity todo2 = TodosEntity.builder()
                .id(2L)
                .task("할일 2")
                .status("COMPLETED")
                .userId(1L)
                .build();

        List<TodosEntity> todoList = Arrays.asList(todo1, todo2);

        // Mock 설정: TodosService의 getDetailTodosList 메서드가 호출되면 todoList를 반환
        when(todosService.getDetailTodosList(any(TodosDtoSearchDto.class))).thenReturn(todoList);

        // When: 테스트할 메서드 실행
        CommonResponse<List<TodosEntity>> result = todosController.getDetailTodosList(searchDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getTask()).isEqualTo("할일 1");
        assertThat(result.getData().get(1).getTask()).isEqualTo("할일 2");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).getDetailTodosList(searchDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 수정 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 수정 성공 테스트")
    void modifyTodoInfo_유효한할일정보_성공() {
        // Given: 테스트 데이터 준비
        TodosDto todosDto = TodosDto.builder()
                .id(1L)
                .task("수정된 할일")
                .status("COMPLETED")
                .build();

        TodosEntity updatedTodo = TodosEntity.builder()
                .id(1L)
                .task("수정된 할일")
                .status("COMPLETED")
                .userId(1L)
                .build();

        // Mock 설정: TodosService의 modifyTodoInfo 메서드가 호출되면 updatedTodo를 반환
        when(todosService.modifyTodoInfo(any(TodosDto.class))).thenReturn(updatedTodo);

        // When: 테스트할 메서드 실행
        CommonResponse<TodosEntity> result = todosController.modifyTodoInfo(todosDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getId()).isEqualTo(1L);
        assertThat(result.getData().getTask()).isEqualTo("수정된 할일");
        assertThat(result.getData().getStatus()).isEqualTo("COMPLETED");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).modifyTodoInfo(todosDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 삭제 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 삭제 성공 테스트")
    void deleteScheduleData_유효한할일ID_성공() {
        // Given: 테스트 데이터 준비
        Long todosId = 1L;
        String expectedMessage = "할일이 삭제되었습니다.";

        // Mock 설정: TodosService의 deleteTodoData 메서드가 호출되면 expectedMessage를 반환
        when(todosService.deleteTodoData(todosId)).thenReturn(expectedMessage);

        // When: 테스트할 메서드 실행
        CommonResponse<String> result = todosController.deleteScheduleData(todosId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getData()).isEqualTo(expectedMessage);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).deleteTodoData(todosId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 존재하지 않는 할일 상세 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("할일 상세 조회 실패 테스트 - 존재하지 않는 할일")
    void getDetailScheduleDetailInfo_존재하지않는할일_실패() {
        // Given: 테스트 데이터 준비
        Long todosId = 999L;

        // Mock 설정: TodosService의 getDetailTodoDetailInfo 메서드가 호출되면 CustomException을 던짐
        when(todosService.getDetailTodoDetailInfo(todosId))
                .thenThrow(new CustomException("500", "할일을 찾을 수 없습니다", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> todosController.getDetailScheduleDetailInfo(todosId))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).getDetailTodoDetailInfo(todosId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 존재하지 않는 할일 수정 요청 시 실패한다
     */
    @Test
    @DisplayName("할일 수정 실패 테스트 - 존재하지 않는 할일")
    void modifyTodoInfo_존재하지않는할일_실패() {
        // Given: 테스트 데이터 준비
        TodosDto todosDto = TodosDto.builder()
                .id(999L)
                .task("수정된 할일")
                .status("COMPLETED")
                .build();

        // Mock 설정: TodosService의 modifyTodoInfo 메서드가 호출되면 CustomException을 던짐
        when(todosService.modifyTodoInfo(todosDto))
                .thenThrow(new CustomException("500", "할일을 찾을 수 없습니다", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> todosController.modifyTodoInfo(todosDto))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).modifyTodoInfo(todosDto);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 존재하지 않는 할일 삭제 요청 시 실패한다
     */
    @Test
    @DisplayName("할일 삭제 실패 테스트 - 존재하지 않는 할일")
    void deleteScheduleData_존재하지않는할일_실패() {
        // Given: 테스트 데이터 준비
        Long todosId = 999L;

        // Mock 설정: TodosService의 deleteTodoData 메서드가 호출되면 CustomException을 던짐
        when(todosService.deleteTodoData(todosId))
                .thenThrow(new CustomException("400", "할일을 찾을 수 없습니다", org.springframework.http.HttpStatus.BAD_REQUEST));

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> todosController.deleteScheduleData(todosId))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosService).deleteTodoData(todosId);
    }
}

/**
 * 📝 Controller 테스트 작성 팁:
 * <p>
 * 1. @ExtendWith(MockitoExtension.class): Mockito 테스트 환경 설정
 * 2. @Mock: 의존성을 Mock 객체로 대체
 * 3. @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상
 * 4. verify(): Mock 객체의 메서드 호출 여부 검증
 * 5. assertThat(): AssertJ를 사용한 가독성 좋은 검증
 * <p>
 * 🚀 다음 단계:
 * 1. 이 테스트를 실행하여 실패하는지 확인 (Red 단계)
 * 2. TodosController의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Controller 테스트는 비즈니스 로직보다는 HTTP 요청/응답 처리에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 * - Given-When-Then 패턴을 사용하여 테스트 구조를 명확하게 작성
 * - BindingResult를 Mock하여 유효성 검사 로직 테스트
 */

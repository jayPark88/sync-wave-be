package com.parker.service.api.v1.todos.service;

import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.jpa.repository.TodosRepository;
import com.parker.service.api.v1.todos.dto.TodosDto;
import com.parker.service.api.v1.todos.dto.TodosDtoSearchDto;
import com.parker.service.api.v1.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TodosService 단위 테스트 - TDD 방법론 적용
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
class TodosServiceTest {

    @Mock
    private TodosRepository todosRepository; // TodosRepository를 Mock 객체로 생성

    @Mock
    private MessageSource messageSource; // MessageSource를 Mock 객체로 생성

    @Mock
    private UserService userService; // UserService를 Mock 객체로 생성

    @InjectMocks
    private TodosService todosService; // 테스트 대상 TodosService (Mock 객체들이 주입됨)

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

        // Mock 설정: UserService의 getUserId 메서드가 호출되면 1L을 반환
        when(userService.getUserId()).thenReturn(1L);
        
        // Mock 설정: TodosRepository의 saveAll 메서드가 호출되면 createdTodos를 반환
        when(todosRepository.saveAll(anyList())).thenReturn(createdTodos);

        // When: 테스트할 메서드 실행
        List<TodosEntity> result = todosService.createTodos(todosDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTask()).isEqualTo("새로운 할일");
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        assertThat(result.get(0).getUserId()).isEqualTo(1L);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).getUserId();
        verify(todosRepository).saveAll(anyList());
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 상세 조회 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 상세 조회 성공 테스트")
    void getDetailTodoDetailInfo_유효한할일ID_성공() {
        // Given: 테스트 데이터 준비
        Long todosId = 1L;

        TodosEntity todo = TodosEntity.builder()
                .id(todosId)
                .task("할일 제목")
                .status("PENDING")
                .userId(1L)
                .build();

        // Mock 설정: TodosRepository의 findById 메서드가 호출되면 Optional.of(todo)를 반환
        when(todosRepository.findById(todosId)).thenReturn(Optional.of(todo));

        // When: 테스트할 메서드 실행
        TodosEntity result = todosService.getDetailTodoDetailInfo(todosId);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(todosId);
        assertThat(result.getTask()).isEqualTo("할일 제목");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getUserId()).isEqualTo(1L);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosRepository).findById(todosId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 존재하지 않는 할일 상세 조회 요청 시 실패한다
     */
    @Test
    @DisplayName("할일 상세 조회 실패 테스트 - 존재하지 않는 할일")
    void getDetailTodoDetailInfo_존재하지않는할일_실패() {
        // Given: 테스트 데이터 준비
        Long todosId = 999L;

        // Mock 설정: TodosRepository의 findById 메서드가 호출되면 Optional.empty()를 반환
        when(todosRepository.findById(todosId)).thenReturn(Optional.empty());
        
        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("할일을 찾을 수 없습니다");

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> todosService.getDetailTodoDetailInfo(todosId))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosRepository).findById(todosId);
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

        // Mock 설정: UserService의 getUserId 메서드가 호출되면 1L을 반환
        when(userService.getUserId()).thenReturn(1L);
        
        // Mock 설정: TodosRepository의 findByUserIdOrderByCreatedDateTimeDesc 메서드가 호출되면 todoList를 반환
        when(todosRepository.findByUserIdOrderByCreatedDateTimeDesc(1L)).thenReturn(todoList);

        // When: 테스트할 메서드 실행
        List<TodosEntity> result = todosService.getDetailTodosList(searchDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTask()).isEqualTo("할일 1");
        assertThat(result.get(1).getTask()).isEqualTo("할일 2");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(userService).getUserId();
        verify(todosRepository).findByUserIdOrderByCreatedDateTimeDesc(1L);
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

        TodosEntity existingTodo = TodosEntity.builder()
                .id(1L)
                .task("기존 할일")
                .status("PENDING")
                .userId(1L)
                .build();

        TodosEntity updatedTodo = TodosEntity.builder()
                .id(1L)
                .task("수정된 할일")
                .status("COMPLETED")
                .userId(1L)
                .build();

        // Mock 설정: 소유권 검증을 위한 현재 사용자 ID 반환
        when(userService.getUserId()).thenReturn(1L);

        // Mock 설정: TodosRepository의 findById 메서드가 호출되면 Optional.of(existingTodo)를 반환
        when(todosRepository.findById(1L)).thenReturn(Optional.of(existingTodo));

        // Mock 설정: TodosRepository의 save 메서드가 호출되면 updatedTodo를 반환
        when(todosRepository.save(any(TodosEntity.class))).thenReturn(updatedTodo);

        // When: 테스트할 메서드 실행
        TodosEntity result = todosService.modifyTodoInfo(todosDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTask()).isEqualTo("수정된 할일");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosRepository).findById(1L);
        verify(userService).getUserId();
        verify(todosRepository).save(any(TodosEntity.class));
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

        // Mock 설정: TodosRepository의 findById 메서드가 호출되면 Optional.empty()를 반환
        when(todosRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("할일을 찾을 수 없습니다");

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> todosService.modifyTodoInfo(todosDto))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosRepository).findById(999L);
        verify(todosRepository, never()).save(any(TodosEntity.class));
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 삭제 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 삭제 성공 테스트")
    void deleteTodoData_유효한할일ID_성공() {
        // Given: 테스트 데이터 준비
        Long todosId = 1L;
        String expectedMessage = "할일이 삭제되었습니다.";

        TodosEntity todo = TodosEntity.builder()
                .id(todosId)
                .task("삭제할 할일")
                .status("PENDING")
                .userId(1L)
                .build();

        // Mock 설정: 소유권 검증을 위한 findById 및 현재 사용자 ID
        when(todosRepository.findById(todosId)).thenReturn(Optional.of(todo));
        when(userService.getUserId()).thenReturn(1L);

        // Mock 설정: TodosRepository의 deleteById 메서드가 호출되면 아무것도 하지 않음
        doNothing().when(todosRepository).deleteById(todosId);

        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn(expectedMessage);

        // When: 테스트할 메서드 실행
        String result = todosService.deleteTodoData(todosId);

        // Then: 결과 검증
        assertThat(result).isEqualTo(expectedMessage);

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosRepository).findById(todosId);
        verify(userService).getUserId();
        verify(todosRepository).deleteById(todosId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 존재하지 않는 할일 삭제 요청 시 실패한다
     */
    @Test
    @DisplayName("할일 삭제 실패 테스트 - 존재하지 않는 할일")
    void deleteTodoData_존재하지않는할일_실패() {
        // Given: 테스트 데이터 준비
        Long todosId = 999L;

        // Mock 설정: TodosRepository의 findById 메서드가 호출되면 Optional.empty()를 반환 (존재하지 않는 할일)
        when(todosRepository.findById(todosId)).thenReturn(Optional.empty());

        // Mock 설정: MessageSource 메시지 반환
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("할일을 찾을 수 없습니다");

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> todosService.deleteTodoData(todosId))
                .isInstanceOf(CustomException.class);

        // Mock 객체의 메서드가 호출되었는지 검증 (findById 호출, deleteById는 호출되지 않음)
        verify(todosRepository).findById(todosId);
        verify(todosRepository, never()).deleteById(todosId);
    }

    /**
     * 🟢 TDD Step 2: 테스트를 통과하는 최소한의 코드 작성 완료
     * 🔵 TDD Step 3: 코드 리팩토링
     * <p>
     * 테스트 시나리오: 할일 수정 시 일부 필드만 수정 요청 시 성공한다
     */
    @Test
    @DisplayName("할일 수정 성공 테스트 - 일부 필드만 수정")
    void modifyTodoInfo_일부필드수정_성공() {
        // Given: 테스트 데이터 준비
        TodosDto todosDto = TodosDto.builder()
                .id(1L)
                .task("수정된 할일")
                .status(null) // 상태는 수정하지 않음
                .build();

        TodosEntity existingTodo = TodosEntity.builder()
                .id(1L)
                .task("기존 할일")
                .status("PENDING")
                .userId(1L)
                .build();

        TodosEntity updatedTodo = TodosEntity.builder()
                .id(1L)
                .task("수정된 할일")
                .status("PENDING") // 기존 상태 유지
                .userId(1L)
                .build();

        // Mock 설정: 소유권 검증을 위한 현재 사용자 ID 반환
        when(userService.getUserId()).thenReturn(1L);

        // Mock 설정: TodosRepository의 findById 메서드가 호출되면 Optional.of(existingTodo)를 반환
        when(todosRepository.findById(1L)).thenReturn(Optional.of(existingTodo));

        // Mock 설정: TodosRepository의 save 메서드가 호출되면 updatedTodo를 반환
        when(todosRepository.save(any(TodosEntity.class))).thenReturn(updatedTodo);

        // When: 테스트할 메서드 실행
        TodosEntity result = todosService.modifyTodoInfo(todosDto);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTask()).isEqualTo("수정된 할일");
        assertThat(result.getStatus()).isEqualTo("PENDING"); // 기존 상태 유지

        // Mock 객체의 메서드가 호출되었는지 검증
        verify(todosRepository).findById(1L);
        verify(userService).getUserId();
        verify(todosRepository).save(any(TodosEntity.class));
    }
}

/**
 * 📝 Service 테스트 작성 팁:
 * <p>
 * 1. @ExtendWith(MockitoExtension.class): Mockito 테스트 환경 설정
 * 2. @Mock: 의존성을 Mock 객체로 대체
 * 3. @InjectMocks: Mock 객체들을 주입받을 실제 테스트 대상
 * 4. verify(): Mock 객체의 메서드 호출 여부 검증
 * 5. assertThat(): AssertJ를 사용한 가독성 좋은 검증
 * <p>
 * 🚀 다음 단계:
 * 1. 이 테스트를 실행하여 실패하는지 확인 (Red 단계)
 * 2. TodosService의 실제 구현이 이 테스트를 통과하는지 확인 (Green 단계)
 * 3. 필요시 코드를 리팩토링 (Refactor 단계)
 * <p>
 * 💡 초급자를 위한 추가 팁:
 * - Service 테스트는 비즈니스 로직에 집중
 * - Mock 객체를 사용하여 의존성을 제거하고 단위 테스트에 집중
 * - 테스트 메서드명은 무엇을 테스트하는지 명확하게 작성
 * - Given-When-Then 패턴을 사용하여 테스트 구조를 명확하게 작성
 * - 예외 상황도 함께 테스트하여 안정성 확보
 * - 부분 수정 시나리오도 테스트하여 유연성 검증
 */

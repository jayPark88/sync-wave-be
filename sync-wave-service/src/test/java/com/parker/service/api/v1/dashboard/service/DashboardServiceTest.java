package com.parker.service.api.v1.dashboard.service;

import com.parker.common.enums.TodoStatus;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.model.TodosModel;
import com.parker.service.api.v1.dashboard.dto.DashBoardDto;
import com.parker.service.api.v1.dashboard.dto.SearchRequestDto;
import com.parker.service.api.v1.schedules.service.SchedulesService;
import com.parker.service.api.v1.todos.service.TodosService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class DashboardServiceTest {

    @Mock
    private TodosService todosService;

    @Mock
    private SchedulesService schedulesService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashBoardInfo() {
        // given
        LocalDate today = LocalDate.now();
        SearchRequestDto searchRequestDto = new SearchRequestDto();
        searchRequestDto.setTargetDate(today);

        TodosEntity mockTodo = TodosEntity.builder()
                .id(1L)
                .task("Test Task")
                .status(TodoStatus.PENDING.code())
                .userId(1L)
                .build();

        when(schedulesService.getDetailScheduleList(any())).thenReturn(Collections.emptyList());
        when(todosService.getDetailTodosList(any())).thenReturn(Collections.singletonList(mockTodo));

        // when
        DashBoardDto result = dashboardService.getDashBoardInfo(searchRequestDto);

        // then
        assertEquals(1, result.getTodayList().size());
        assertEquals("Test Task", result.getTodayList().get(0).getTask());
    }
}
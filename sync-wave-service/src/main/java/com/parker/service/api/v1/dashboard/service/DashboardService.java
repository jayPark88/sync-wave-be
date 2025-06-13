package com.parker.service.api.v1.dashboard.service;

import com.parker.common.enums.TodoStatus;
import com.parker.common.jpa.entity.SchedulesEntity;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.model.SchedulesModel;
import com.parker.common.model.TodosModel;
import com.parker.service.api.v1.dashboard.dto.DashBoardDto;
import com.parker.service.api.v1.dashboard.dto.SearchRequestDto;
import com.parker.service.api.v1.schedules.dto.SearchSchedulesDto;
import com.parker.service.api.v1.schedules.service.SchedulesService;
import com.parker.service.api.v1.todos.dto.TodosDtoSearchDto;
import com.parker.service.api.v1.todos.service.TodosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final SchedulesService schedulesService;
    private final TodosService todosService;

    public DashBoardDto getDashBoardInfo(SearchRequestDto searchRequestDto) {
        YearMonth yearMonth = YearMonth.of(searchRequestDto.getTargetDate().getYear(), searchRequestDto.getTargetDate().getMonth());

        DashBoardDto dashBoardDto = new DashBoardDto();
        dashBoardDto.setSchedulesDtoList(searchScheduleList(yearMonth));
        dashBoardDto.setTodayList(searchTodosList());
        return dashBoardDto;
    }

    private List<SchedulesModel> searchScheduleList(YearMonth yearMonth){
        SearchSchedulesDto searchSchedulesDto = new SearchSchedulesDto();
        searchSchedulesDto.setStartDate(yearMonth.atDay(1));// 1일
        searchSchedulesDto.setEndDate(yearMonth.atEndOfMonth());// 말일

        List<SchedulesEntity> schedulesEntityList = schedulesService.getDetailScheduleList(searchSchedulesDto);
        return schedulesEntityList.stream().map(SchedulesModel::new).toList();
    }

    private List<TodosModel> searchTodosList(){
        TodosDtoSearchDto todosDtoSearchDto = new TodosDtoSearchDto();

        List<TodosEntity>todosEntityList = todosService.getDetailTodosList(todosDtoSearchDto).stream()
            .filter(item -> item.getStatus().equals(TodoStatus.PENDING.code()) || 
                          item.getStatus().equals(TodoStatus.IN_PROGRESS.code()))
            .toList();
        return todosEntityList.stream().map(TodosModel::new).toList();
    }
}

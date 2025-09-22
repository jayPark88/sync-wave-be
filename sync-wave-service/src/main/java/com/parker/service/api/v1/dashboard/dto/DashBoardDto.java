package com.parker.service.api.v1.dashboard.dto;

import com.parker.common.model.SchedulesModel;
import com.parker.common.model.TodosModel;
import lombok.Data;

import java.util.List;

@Data
public class DashBoardDto {
    /**
     * - 상단: 오늘의 일정 표시
     * - 중앙: 주간/월간 일정 캘린더
     * - 하단: todo 목록
     */
    private List<SchedulesModel> schedulesDtoList;// 오늘 할일
    private List<TodosModel> todayList;// 오늘 todo 목록

}

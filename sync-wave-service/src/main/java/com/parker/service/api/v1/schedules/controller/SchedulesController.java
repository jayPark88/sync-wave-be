package com.parker.service.api.v1.schedules.controller;

import com.parker.service.api.v1.schedules.dto.SchedulesDto;
import com.parker.service.api.v1.schedules.dto.SearchSchedulesDto;
import com.parker.common.exception.CustomException;
import com.parker.common.jpa.entity.SchedulesEntity;
import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.schedules.service.SchedulesService;
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
@RequestMapping("/v1/schedules")
@RequiredArgsConstructor
public class SchedulesController {
    private final SchedulesService schedulesService;

    @PostMapping
    public CommonResponse<?> createSchedules(@Valid @RequestBody SchedulesDto schedulesDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException(FAIL_400.code(), bindingResult.getAllErrors().stream().findFirst().get().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        return new CommonResponse<>(schedulesService.createSchedules(schedulesDto));
    }

    @GetMapping("/{scheduleId}")
    public CommonResponse<SchedulesEntity> getDetailScheduleDetailInfo(@PathVariable("scheduleId") Long scheduleId) {
        return new CommonResponse<>(schedulesService.getDetailScheduleDetailInfo(scheduleId));
    }

    @GetMapping
    public CommonResponse<List<SchedulesEntity>> getDetailScheduleList(SearchSchedulesDto searchSchedulesDto) {
        return new CommonResponse<>(schedulesService.getDetailScheduleList(searchSchedulesDto));
    }

    @DeleteMapping("/{scheduleId}")
    public CommonResponse<String> deleteScheduleData(@PathVariable("scheduleId") Long scheduleId) {
        return new CommonResponse<>(schedulesService.deleteScheduleData(scheduleId));
    }

    @PatchMapping("/{scheduleId}")
    public CommonResponse<SchedulesEntity> modifyScheduleInfo(@PathVariable("scheduleId") Long scheduleId, @Valid @RequestBody SchedulesDto schedulesDto) {
        return new CommonResponse<>(schedulesService.modifyScheduleInfo(scheduleId, schedulesDto));
    }

}

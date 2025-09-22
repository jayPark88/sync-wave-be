package com.parker.service.api.v1.dashboard.controller;

import com.parker.common.resonse.CommonResponse;
import com.parker.service.api.v1.dashboard.dto.DashBoardDto;
import com.parker.service.api.v1.dashboard.dto.SearchRequestDto;
import com.parker.service.api.v1.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public CommonResponse<DashBoardDto> getDashBoardInfo(SearchRequestDto searchRequestDto) {
        return new CommonResponse<>(dashboardService.getDashBoardInfo(searchRequestDto));
    }
}

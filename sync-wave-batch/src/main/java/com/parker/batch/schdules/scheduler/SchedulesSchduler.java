package com.parker.batch.schdules.scheduler;

import com.parker.batch.schdules.service.SchedulesSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulesSchduler {
    private final SchedulesSchedulerService schedulesSchedulerService;

    @Scheduled(cron = "0 0 * * * *")
    void alertUsersAboutScheduleInOneHourTask() {
        schedulesSchedulerService.alertUsersAboutScheduleInOneHourTask();
    }
}

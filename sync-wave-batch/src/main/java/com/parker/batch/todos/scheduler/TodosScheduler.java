package com.parker.batch.todos.scheduler;

import com.parker.batch.todos.service.TodosSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class TodosScheduler {

    private final TodosSchedulerService todosSchedulerService;

    @Scheduled(cron = "0 0 9,18 * * *")
    void alertUsersAboutTodosReminderTask() {
        log.info("09, 18시 Todos Reminder Alarm Send Scheduler Start {} ", LocalDateTime.now().getHour());
        todosSchedulerService.alertUsersAboutTodosReminderTask();
    }
}

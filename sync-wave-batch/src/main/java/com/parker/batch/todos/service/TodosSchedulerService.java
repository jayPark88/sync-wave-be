package com.parker.batch.todos.service;

import com.parker.batch.common.intf.AlarmInterface;
import com.parker.common.enums.TodoStatus;
import com.parker.common.jpa.entity.TodosEntity;
import com.parker.common.jpa.entity.UserEntity;
import com.parker.common.jpa.repository.TodosRepository;
import com.parker.common.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodosSchedulerService {
    private final TodosRepository todosRepository;
    private final UserRepository userRepository;

    private final AlarmInterface alarmSlackImpl;

    public void alertUsersAboutTodosReminderTask() {
        log.info("진행 중인 Todos 리스트 조회");
        List<TodosEntity> todosEntityList = todosRepository.findByUserId(1L); // 임시로 userId 1로 설정

        log.info("todosEntityList 중 PENDING 상태인 항목만 필터링");
        List<TodosEntity> pendingList = todosEntityList.stream()
                .filter(item -> item.getStatus().equals(TodoStatus.PENDING.code()))
                .collect(Collectors.toList());

        log.info("사용자별로 그룹화하여 중복 제거");
        List<TodosEntity> removeDuplicateList = pendingList.stream()
                .collect(Collectors.toMap(
                        TodosEntity::getUserId,
                        todosEntity -> todosEntity,
                        (existing, replacement) -> replacement
                )).values().stream().toList();

        log.info("알림 발송 요청! 🚀");
        removeDuplicateList.stream().parallel().forEach(item -> {
            log.info("Thread: {} 처리 중: {}", Thread.currentThread().getName(), item);
            Optional<UserEntity> optionalUserEntity = userRepository.findById(item.getUserId());
            log.info("알림 대상자 조회 {} ", optionalUserEntity);
            if (optionalUserEntity.isPresent()) {
                log.info("알림 발송 요청! 🚀");
                alarmSlackImpl.sendMsg(optionalUserEntity.get().getEmail(), generateAlaramMsg(item, optionalUserEntity));
            }
        });
    }

    /**
     *
     * @param todosEntity
     * @param optionalUserEntity
     * @return
     */
    private String generateAlaramMsg(TodosEntity todosEntity, Optional<UserEntity> optionalUserEntity) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(optionalUserEntity.isPresent() ? optionalUserEntity.get().getUserName() : "");
        stringBuilder.append("님 처리해야 할 할일이 있습니다 🚀");
        stringBuilder.append("\n\n");
        stringBuilder.append("︎︎◼︎ todo 내용: ");
        stringBuilder.append(todosEntity.getTask());
        stringBuilder.append("\n\n");
        return stringBuilder.toString();
    }

}

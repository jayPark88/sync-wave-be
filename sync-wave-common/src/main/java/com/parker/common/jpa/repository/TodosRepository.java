package com.parker.common.jpa.repository;

import com.parker.common.jpa.entity.TodosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodosRepository extends JpaRepository<TodosEntity, Long> {
    Optional<TodosEntity> findByTask(String task);
    List<TodosEntity> findByUserId(Long userId);
    List<TodosEntity> findByStatus(String status);

    // 최신순 조회를 위한 메서드
    List<TodosEntity> findByUserIdOrderByCreatedDateTimeDesc(Long userId);
}

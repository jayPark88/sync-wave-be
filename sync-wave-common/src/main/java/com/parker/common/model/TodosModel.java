package com.parker.common.model;

import com.parker.common.intf.ChangableToFromEntity;
import com.parker.common.jpa.entity.TodosEntity;
import lombok.Data;

@Data
public class TodosModel implements ChangableToFromEntity<TodosEntity> {

    private Long id;
    private String task;
    private String status;
    private Long userId;

    public TodosModel(TodosEntity todosEntity){
        from(todosEntity);
    }

    @Override
    public TodosEntity to() {
        return TodosEntity.builder()
                .id(id)
                .task(task)
                .status(status)
                .userId(userId)
                .build();
    }

    @Override
    public void from(TodosEntity entity) {
        this.id = entity.getId();
        this.task = entity.getTask();
        this.status = entity.getStatus();
        this.userId = entity.getUserId();
    }
}
